package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.Status
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.KordObject
import com.gitlab.kordlib.core.`object`.Activity
import com.gitlab.kordlib.core.`object`.data.ClientStatusData
import com.gitlab.kordlib.core.`object`.data.PresenceData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class Presence(val data: PresenceData, override val kord: Kord) : KordObject {

    val clientStatus: ClientStatus get() = ClientStatus(data.clientStatus)

    val game: Activity? get() = data.game?.let { Activity(it) }

    val guildId: Snowflake get() = Snowflake(data.guildId)

    val roleIds: Set<Snowflake> get() = data.roles.asSequence().map { Snowflake(it) }.toSet()

    val roles: Flow<Role> get() = roleIds.asFlow().map { kord.getRole(guildId, it) }.filterNotNull()

    val status: Status get() = data.status

    val userId: Snowflake get() = Snowflake(data.userId)

    suspend fun getUser(): User = kord.getUser(userId)!!

}

class ClientStatus(val data: ClientStatusData) {
    val desktop: Client.Desktop? get() = data.desktop?.let { Client.Desktop(it) }
    val mobile: Client.Mobile? get() = data.desktop?.let { Client.Mobile(it) }
    val web: Client.Web? get() = data.desktop?.let { Client.Web(it) }

    sealed class Client(val status: Status) {
        class Desktop(status: Status) : Client(status)
        class Mobile(status: Status) : Client(status)
        class Web(status: Status) : Client(status)
    }
}
