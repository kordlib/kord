package dev.kord.rest.builder.discovery

import dev.kord.common.entity.PartialDiscordDiscoveryMetadata
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.RequestBuilder

class ModifyDiscoveryMetadataBuilder : RequestBuilder<PartialDiscordDiscoveryMetadata> {
    private var _primaryCategoryId: OptionalSnowflake = OptionalSnowflake.Missing
    val primaryCategoryId by ::_primaryCategoryId.delegate()
    private var _keywords: Optional<List<String>> = Optional.Missing()
    var keywords by ::_keywords.delegate()
    private var _emojiDiscoverabilityEnabled: OptionalBoolean = OptionalBoolean.Missing
    var emojiDiscoverabilityEnabled by ::_emojiDiscoverabilityEnabled.delegate()

    override fun toRequest(): PartialDiscordDiscoveryMetadata = PartialDiscordDiscoveryMetadata(
        _primaryCategoryId, _keywords, _emojiDiscoverabilityEnabled
    )
}
