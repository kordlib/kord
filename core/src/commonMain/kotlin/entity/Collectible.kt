package dev.kord.core.entity

import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.CollectibleData
import dev.kord.core.cache.data.toData

public class Collectible(
    public val data: CollectibleData,
    override val kord: Kord
) : KordObject {
    public val nameplate: Nameplate? = data.nameplate.value?.let {
        Nameplate(it.toData(), kord)
    }
}