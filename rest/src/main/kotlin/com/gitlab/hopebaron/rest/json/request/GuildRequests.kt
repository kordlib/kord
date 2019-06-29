package com.gitlab.hopebaron.rest.json.request

import com.gitlab.hopebaron.common.entity.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateGuildRequest(val name: String,
                              val region: String,
                              @SerialName("verification_level")
                              val verificationLevel: VerificationLevel,
                              @SerialName("default_message_notifications")
                              val defaultNotificationLevel: DefaultMessageNotificationLevel)

@Serializable
data class CreateGuildChannelRequest(val name: String,
                                     val type: ChannelType,
                                     val topic: String,
                                     val bitrate: Int,
                                     @SerialName("user_limit")
                                     val userLimit: Int,
                                     @SerialName("rate_limit_per_user")
                                     val rateLimitPerUser: Int,
                                     val position: Int,
                                     @SerialName("permission_overwrites")
                                     val permissionOverwrite: List<Overwrite>,
                                     @SerialName("parent_id")
                                     val parentId: String,
                                     val nsfw: Boolean)

@Serializable
data class ModifyGuildChannelPositionRequest(val id: String, val position: Int)

@Serializable
data class AddGuildMemberRequest(@SerialName("access_token") val token: String,
                                 val nick: String,
                                 val roles: List<String>,
                                 val mute: Boolean,
                                 val deaf: Boolean)

@Serializable
data class ModifyGuildMemberRequest(val nick: String? = null,
                                    val roles: List<String>? = null,
                                    val mute: Boolean? = null,
                                    val deaf: Boolean? = null,
                                    @SerialName("channel_id")
                                    val channelId: String? = null)


@Serializable
data class AddGuildBanRequest(val reason: String? = null,
                              @SerialName("delete-message-days")
                              val deleteMessagesDays: String? = null)

@Serializable
data class CreateGuildRoleRequest(val name: String,
                                  val permissions: Permission,
                                  val color: Int,
                                  @SerialName("hoist")
                                  val separate: Boolean,
                                  val Mentionable: Boolean)

@Serializable
data class ModifyGuildRolePositionRequest(val id: String,
                                          val position: String)

@Serializable
data class ModifyGuildRoleRequest(val name: String? = null,
                                  val permissions: Permission? = null,
                                  val color: Int? = null,
                                  @SerialName("hoist")
                                  val separate: Boolean? = null,
                                  val Mentionable: Boolean? = null)

@Serializable
data class CreateGuildIntegrationRequest(val type: Int,
                                         val id: String)

@Serializable
data class ModifyGuildIntegrationRequest(@SerialName("expire_behavior") val expireBehavior: Int? = null,
                                         @SerialName("expire_grace_period") val expirePeriod: Int? = null,
                                         @SerialName("enable_emoticons") val emoticons: Boolean? = null)

@Serializable
data class ModifyGuildEmbedRequest(val enabled: Boolean,
                                   @SerialName("channel_id") val channelId: String)

@Serializable
data class ModifyCurrentUserNicknameRequest(val nick: String? = null)