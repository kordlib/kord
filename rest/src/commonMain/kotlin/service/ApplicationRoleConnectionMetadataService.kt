package dev.kord.rest.service

import dev.kord.common.entity.DiscordApplicationRoleConnectionMetadata
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.application.ApplicationRoleConnectionMetadataRecordsBuilder
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route
import kotlinx.serialization.builtins.ListSerializer
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract

public class ApplicationRoleConnectionMetadataService(requestHandler: RequestHandler) : RestService(requestHandler) {

    public suspend fun getApplicationRoleConnectionMetadataRecords(
        applicationId: Snowflake,
    ): List<DiscordApplicationRoleConnectionMetadata> = call(Route.ApplicationRoleConnectionMetadataRecordsGet) {
        keys[Route.ApplicationId] = applicationId
    }

    public suspend fun updateApplicationRoleConnectionMetadataRecords(
        applicationId: Snowflake,
        request: List<DiscordApplicationRoleConnectionMetadata>,
    ): List<DiscordApplicationRoleConnectionMetadata> = call(Route.ApplicationRoleConnectionMetadataRecordsUpdate) {
        keys[Route.ApplicationId] = applicationId
        body(ListSerializer(DiscordApplicationRoleConnectionMetadata.serializer()), request)
    }

    public suspend inline fun updateApplicationRoleConnectionMetadataRecords(
        applicationId: Snowflake,
        builder: ApplicationRoleConnectionMetadataRecordsBuilder.() -> Unit,
    ): List<DiscordApplicationRoleConnectionMetadata> {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        val request = ApplicationRoleConnectionMetadataRecordsBuilder().apply(builder).toRequest()
        return updateApplicationRoleConnectionMetadataRecords(applicationId, request)
    }
}
