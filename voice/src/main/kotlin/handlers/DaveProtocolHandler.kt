package dev.kord.voice.handlers

import dev.kord.voice.dave.DaveCommitResult
import dev.kord.voice.dave.DaveProtocol
import dev.kord.voice.gateway.*
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

private val daveLogger = KotlinLogging.logger { }

/**
 * Handles the DAVE (Discord Audio & Video E2EE) protocol lifecycle.
 *
 * Processes all DAVE-related gateway events and coordinates with a [DaveProtocol]
 * implementation for MLS group management, key ratcheting, and transitions.
 *
 * All events are processed sequentially via a single flow collector to prevent
 * race conditions between MLS protocol operations (e.g., PrepareEpoch and
 * PrepareTransition arriving close together).
 */
internal class DaveProtocolHandler(
    flow: Flow<VoiceEvent>,
    private val daveProtocol: DaveProtocol,
    private val voiceGateway: VoiceGateway,
) : ConnectionEventHandler<VoiceEvent>(flow, "DaveProtocolHandler") {

    /**
     * Guards against repeated recovery attempts when processProposals fails.
     *
     * Starts `true` — proposals are skipped until the bot successfully joins
     * the MLS group via a Welcome (or a PrepareEpoch epoch=1 fresh start).
     *
     * Set to `true` on:
     *  - construction (initial state — not yet joined)
     *  - proposals failure (recovery in progress)
     *  - gateway Close (reset for next connection)
     *
     * Cleared to `false` on:
     *  - Welcome processed successfully (we're in the group)
     */
    private var awaitingRejoin = true

    private suspend fun triggerRejoin(trigger: String) {
        daveLogger.debug { "DAVE: triggerRejoin called from $trigger, awaitingRejoin was $awaitingRejoin" }
        awaitingRejoin = true
        daveProtocol.reset()
        val keyPackage = daveProtocol.getMarshalledKeyPackage()
        if (keyPackage.isNotEmpty()) {
            voiceGateway.sendBinary(OpCode.DaveMlsKeyPackage.code, keyPackage)
            daveLogger.debug { "DAVE: sent key package for rejoin (${keyPackage.size} bytes)" }
        } else {
            daveLogger.warn { "DAVE: key package still empty after reset, escalating to full re-initialization" }
            daveProtocol.reinitialize()
            val reinitKeyPackage = daveProtocol.getMarshalledKeyPackage()
            if (reinitKeyPackage.isNotEmpty()) {
                voiceGateway.sendBinary(OpCode.DaveMlsKeyPackage.code, reinitKeyPackage)
                daveLogger.debug { "DAVE: sent key package after re-initialization (${reinitKeyPackage.size} bytes)" }
            } else {
                daveLogger.error { "DAVE: failed to get key package even after re-initialization from $trigger" }
            }
        }
    }

    override suspend fun start() {
        daveLogger.info { "DAVE: handler initialized [kord-dave-fix-v3] — awaitingRejoin=$awaitingRejoin" }

        flow.collect { event ->
            try {
                when (event) {
                    is ClientsConnect -> handleClientsConnect(event)
                    is ClientDisconnect -> handleClientDisconnect(event)
                    is SessionDescription -> handleSessionDescription(event)
                    is DaveProtocolPrepareEpoch -> handlePrepareEpoch(event)
                    is DaveMlsExternalSenderPackage -> handleExternalSenderPackage(event)
                    is DaveMlsProposals -> handleProposals(event)
                    is DaveMlsAnnounceCommitTransition -> handleAnnounceCommitTransition(event)
                    is DaveMlsWelcome -> handleWelcome(event)
                    is DaveProtocolPrepareTransition -> handlePrepareTransition(event)
                    is DaveProtocolExecuteTransition -> handleExecuteTransition(event)
                    is Close -> handleClose(event)
                    else -> { /* not a DAVE event, ignore */ }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (exception: Exception) {
                daveLogger.error(exception) { "[$name]" }
            }
        }
    }

    // Track connected users for MLS proposal validation
    private suspend fun handleClientsConnect(event: ClientsConnect) {
        daveLogger.debug { "DAVE: clients connected: ${event.userIds}" }
        for (userId in event.userIds) {
            daveProtocol.addUser(userId.value.toLong())
        }
    }

    // Clean up decryptor resources when a client disconnects
    private suspend fun handleClientDisconnect(event: ClientDisconnect) {
        daveLogger.debug { "DAVE: client disconnected: ${event.userId}" }
        daveProtocol.removeUser(event.userId.value.toLong())
    }

    // Session description includes DAVE protocol version confirmation
    private suspend fun handleSessionDescription(event: SessionDescription) {
        val version = event.daveProtocolVersion
        daveLogger.debug { "DAVE: session description received, dave_protocol_version=$version" }
        if (version > 0) {
            daveProtocol.setProtocolVersion(version)
        }
    }

    // Prepare epoch: announces new MLS epoch or group recreation
    private suspend fun handlePrepareEpoch(event: DaveProtocolPrepareEpoch) {
        daveLogger.debug { "DAVE: prepare epoch, version=${event.protocolVersion}, epoch=${event.epoch}" }

        if (event.protocolVersion > 0 && event.epoch == 1) {
            daveLogger.debug { "DAVE: new group detected (epoch=1), sending key package without reset" }
            val keyPackage = daveProtocol.getMarshalledKeyPackage()
            if (keyPackage.isNotEmpty()) {
                voiceGateway.sendBinary(OpCode.DaveMlsKeyPackage.code, keyPackage)
                daveLogger.debug { "DAVE: sent key package for new epoch (${keyPackage.size} bytes)" }
            } else {
                daveLogger.warn { "DAVE: failed to get key package for new epoch" }
                triggerRejoin("PrepareEpoch(keyPackageFailed)")
            }
        }
    }

    // External sender package: voice gateway's MLS credential
    private suspend fun handleExternalSenderPackage(event: DaveMlsExternalSenderPackage) {
        daveLogger.debug { "DAVE: received external sender package (${event.data.size} bytes)" }
        daveProtocol.setExternalSender(event.data)
    }

    // MLS proposals: add/remove members
    private suspend fun handleProposals(event: DaveMlsProposals) {
        daveLogger.debug { "DAVE: received proposals (${event.data.size} bytes), awaitingRejoin=$awaitingRejoin" }

        if (awaitingRejoin) {
            daveLogger.debug { "DAVE: skipping proposals while awaiting rejoin" }
            return
        }

        val response = daveProtocol.processProposals(event.data, emptySet()) // recognizedUserIds managed internally
        if (response != null) {
            // We produced a commit+welcome in response to proposals
            try {
                voiceGateway.sendBinary(OpCode.DaveMlsCommitWelcome.code, response)
                daveLogger.debug { "DAVE: sent commit+welcome (${response.size} bytes)" }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                daveLogger.warn(e) { "DAVE: failed to send commit+welcome, triggering recovery" }
                triggerRejoin("Proposals(sendFailed)")
            }
        } else {
            daveLogger.warn { "DAVE: processProposals failed, resetting MLS state and requesting rejoin" }
            triggerRejoin("Proposals(processFailed)")
        }
    }

    // MLS announce commit transition: winning commit from another member
    private suspend fun handleAnnounceCommitTransition(event: DaveMlsAnnounceCommitTransition) {
        daveLogger.debug { "DAVE: received announce commit transition (transition=${event.transitionId}, ${event.data.size} bytes)" }
        val result = daveProtocol.processCommit(event.data)
        when (result) {
            is DaveCommitResult.Success -> {
                daveLogger.debug { "DAVE: commit processed successfully, roster size=${result.rosterMap.size}" }
                // Prepare key ratchets for the transition
                daveProtocol.prepareKeyRatchets(event.transitionId, daveProtocol.currentProtocolVersion)
                // Signal readiness for transition
                voiceGateway.send(DaveProtocolReadyForTransition(event.transitionId))
                daveLogger.debug { "DAVE: sent ready for transition ${event.transitionId}" }
            }
            is DaveCommitResult.Ignored -> {
                daveLogger.debug { "DAVE: commit ignored (duplicate or irrelevant)" }
            }
            is DaveCommitResult.Failed -> {
                daveLogger.warn { "DAVE: commit processing failed, sending invalid commit" }
                voiceGateway.send(DaveMlsInvalidCommitWelcome(event.transitionId))
            }
        }
    }

    // MLS welcome: joining an existing group
    private suspend fun handleWelcome(event: DaveMlsWelcome) {
        daveLogger.debug { "DAVE: received welcome (transition=${event.transitionId}, ${event.data.size} bytes)" }
        val roster = daveProtocol.processWelcome(event.data, emptySet())
        if (roster != null) {
            daveLogger.debug { "DAVE: welcome processed, roster size=${roster.size}" }
            awaitingRejoin = false // Successfully (re)joined the MLS group
            // Prepare key ratchets for the transition
            daveProtocol.prepareKeyRatchets(event.transitionId, daveProtocol.currentProtocolVersion)
            // Signal readiness
            voiceGateway.send(DaveProtocolReadyForTransition(event.transitionId))
            daveLogger.debug { "DAVE: sent ready for transition ${event.transitionId}" }
        } else {
            daveLogger.warn { "DAVE: welcome processing failed, sending invalid" }
            voiceGateway.send(DaveMlsInvalidCommitWelcome(event.transitionId))
        }
    }

    // Prepare transition: server announces an upcoming transition
    private suspend fun handlePrepareTransition(event: DaveProtocolPrepareTransition) {
        daveLogger.debug { "DAVE: prepare transition, version=${event.protocolVersion}, transition=${event.transitionId}, awaitingRejoin=$awaitingRejoin" }

        if (awaitingRejoin) {
            // Must still respond with TransitionReady to unblock Discord's pipeline,
            // otherwise Discord waits indefinitely and never sends the Welcome we need.
            // Skip prepareKeyRatchets since the session is reset and ratchets would be invalid.
            daveLogger.debug { "DAVE: awaiting rejoin — skipping prepareKeyRatchets but sending TransitionReady to unblock pipeline" }
            voiceGateway.send(DaveProtocolReadyForTransition(event.transitionId))
            daveLogger.debug { "DAVE: sent ready for transition ${event.transitionId} (during rejoin)" }
            return
        }

        daveProtocol.prepareKeyRatchets(event.transitionId, event.protocolVersion)
        // Signal readiness
        voiceGateway.send(DaveProtocolReadyForTransition(event.transitionId))
        daveLogger.debug { "DAVE: sent ready for transition ${event.transitionId}" }
    }

    // Execute transition: server triggers the actual transition
    private suspend fun handleExecuteTransition(event: DaveProtocolExecuteTransition) {
        daveLogger.debug { "DAVE: execute transition ${event.transitionId}" }
        daveProtocol.executeTransition(event.transitionId)
    }

    // Clean up on close
    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleClose(event: Close) {
        daveLogger.debug { "DAVE: gateway closing, resetting protocol state" }
        awaitingRejoin = true // Reset for next connection
        daveProtocol.reset()
    }
}
