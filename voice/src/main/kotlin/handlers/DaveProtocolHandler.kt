package dev.kord.voice.handlers

import dev.kord.voice.dave.DaveCommitResult
import dev.kord.voice.dave.DaveProtocol
import dev.kord.voice.gateway.*
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow

private val daveLogger = KotlinLogging.logger { }

/**
 * Handles the DAVE (Discord Audio & Video E2EE) protocol lifecycle.
 *
 * Processes all DAVE-related gateway events and coordinates with a [DaveProtocol]
 * implementation for MLS group management, key ratcheting, and transitions.
 */
internal class DaveProtocolHandler(
    flow: Flow<VoiceEvent>,
    private val daveProtocol: DaveProtocol,
    private val voiceGateway: VoiceGateway,
) : ConnectionEventHandler<VoiceEvent>(flow, "DaveProtocolHandler") {

    override suspend fun start() = coroutineScope {

        // Track connected users for MLS proposal validation
        on<ClientsConnect> {
            daveLogger.debug { "DAVE: clients connected: ${it.userIds}" }
            for (userId in it.userIds) {
                daveProtocol.addUser(userId.value.toLong())
            }
        }

        // Clean up decryptor resources when a client disconnects
        on<ClientDisconnect> {
            daveLogger.debug { "DAVE: client disconnected: ${it.userId}" }
            daveProtocol.removeUser(it.userId.value.toLong())
        }

        // Session description includes DAVE protocol version confirmation
        on<SessionDescription> {
            val version = it.daveProtocolVersion
            daveLogger.debug { "DAVE: session description received, dave_protocol_version=$version" }
            if (version > 0) {
                daveProtocol.setProtocolVersion(version)
            }
        }

        // Prepare epoch: announces new MLS epoch or group recreation
        on<DaveProtocolPrepareEpoch> {
            daveLogger.debug { "DAVE: prepare epoch, version=${it.protocolVersion}, epoch=${it.epoch}" }

            val isNewGroup = it.epoch == 1

            if (it.protocolVersion > 0 && isNewGroup) {
                // This triggers MLS group initialization and key package generation
                // The initialize call should be done by the voice connection when it has the channel info
                // Here we just need to send our key package
                val keyPackage = daveProtocol.getMarshalledKeyPackage()
                if (keyPackage.isNotEmpty()) {
                    voiceGateway.sendBinary(OpCode.DaveMlsKeyPackage.code, keyPackage)
                    daveLogger.debug { "DAVE: sent key package (${keyPackage.size} bytes)" }
                }
            }
        }

        // External sender package: voice gateway's MLS credential
        on<DaveMlsExternalSenderPackage> {
            daveLogger.debug { "DAVE: received external sender package (${it.data.size} bytes)" }
            daveProtocol.setExternalSender(it.data)
        }

        // MLS proposals: add/remove members
        on<DaveMlsProposals> {
            daveLogger.debug { "DAVE: received proposals (${it.data.size} bytes)" }
            val response = daveProtocol.processProposals(it.data, emptySet()) // recognizedUserIds managed internally
            if (response != null) {
                // We produced a commit+welcome in response to proposals
                voiceGateway.sendBinary(OpCode.DaveMlsCommitWelcome.code, response)
                daveLogger.debug { "DAVE: sent commit+welcome (${response.size} bytes)" }
            }
        }

        // MLS announce commit transition: winning commit from another member
        on<DaveMlsAnnounceCommitTransition> {
            daveLogger.debug { "DAVE: received announce commit transition (transition=${it.transitionId}, ${it.data.size} bytes)" }
            val result = daveProtocol.processCommit(it.data)
            when (result) {
                is DaveCommitResult.Success -> {
                    daveLogger.debug { "DAVE: commit processed successfully, roster size=${result.rosterMap.size}" }
                    // Prepare key ratchets for the transition
                    daveProtocol.prepareKeyRatchets(it.transitionId, daveProtocol.currentProtocolVersion)
                    // Signal readiness for transition
                    voiceGateway.send(DaveProtocolReadyForTransition(it.transitionId))
                    daveLogger.debug { "DAVE: sent ready for transition ${it.transitionId}" }
                }
                is DaveCommitResult.Ignored -> {
                    daveLogger.debug { "DAVE: commit ignored (duplicate or irrelevant)" }
                }
                is DaveCommitResult.Failed -> {
                    daveLogger.warn { "DAVE: commit processing failed, sending invalid commit" }
                    voiceGateway.send(DaveMlsInvalidCommitWelcome(it.transitionId))
                }
            }
        }

        // MLS welcome: joining an existing group
        on<DaveMlsWelcome> {
            daveLogger.debug { "DAVE: received welcome (transition=${it.transitionId}, ${it.data.size} bytes)" }
            val roster = daveProtocol.processWelcome(it.data, emptySet())
            if (roster != null) {
                daveLogger.debug { "DAVE: welcome processed, roster size=${roster.size}" }
                // Prepare key ratchets for the transition
                daveProtocol.prepareKeyRatchets(it.transitionId, daveProtocol.currentProtocolVersion)
                // Signal readiness
                voiceGateway.send(DaveProtocolReadyForTransition(it.transitionId))
                daveLogger.debug { "DAVE: sent ready for transition ${it.transitionId}" }
            } else {
                daveLogger.warn { "DAVE: welcome processing failed, sending invalid" }
                voiceGateway.send(DaveMlsInvalidCommitWelcome(it.transitionId))
            }
        }

        // Prepare transition: server announces an upcoming transition
        on<DaveProtocolPrepareTransition> {
            daveLogger.debug { "DAVE: prepare transition, version=${it.protocolVersion}, transition=${it.transitionId}" }
            daveProtocol.prepareKeyRatchets(it.transitionId, it.protocolVersion)
            // Signal readiness
            voiceGateway.send(DaveProtocolReadyForTransition(it.transitionId))
            daveLogger.debug { "DAVE: sent ready for transition ${it.transitionId}" }
        }

        // Execute transition: server triggers the actual transition
        on<DaveProtocolExecuteTransition> {
            daveLogger.debug { "DAVE: execute transition ${it.transitionId}" }
            daveProtocol.executeTransition(it.transitionId)
        }

        // Clean up on close
        on<Close> {
            daveLogger.debug { "DAVE: gateway closing, resetting protocol state" }
            daveProtocol.reset()
        }
    }
}
