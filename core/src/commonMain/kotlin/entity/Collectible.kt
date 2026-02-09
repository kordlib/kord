package dev.kord.core.entity

import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.CollectibleData
import dev.kord.core.cache.data.toData

/**
 * An instance of a [collectible](https://discord.com/developers/docs/resources/user#collectibles)
 */
public class Collectible(
    public val data: CollectibleData,
    override val kord: Kord
) : KordObject {
    /**
     * The [Nameplate] for the collectible
     */
    public val nameplate: Nameplate? = data.nameplate.value?.let {
        Nameplate(it.toData(), kord)
    }
}