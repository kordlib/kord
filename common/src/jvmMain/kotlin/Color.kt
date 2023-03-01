@file:JvmName("ColorJvm")
package dev.kord.common

import java.awt.Color as AwtColor

public val AwtColor.kColor: Color get() = Color(rgb)
