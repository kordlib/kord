package dev.kord.common.entity

private val ALL_PERMISSIONS = Permissions(flags = Permission.entries)

/** All known [Permission]s (as contained in [Permission.entries]) combined into a single [Permissions] object. */
public val Permissions.Companion.ALL_KNOWN: Permissions get() = ALL_PERMISSIONS
