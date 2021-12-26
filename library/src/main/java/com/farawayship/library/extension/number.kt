package com.farawayship.library.extension

import java.text.CharacterIterator
import java.text.StringCharacterIterator
import java.util.*
import kotlin.math.abs

fun Long.humanReadableByteCountBin(): String? {
    val absB = if (this == Long.MIN_VALUE) Long.MAX_VALUE else abs(this)
    if (absB < 1024) {
        return "$this B"
    }
    var value = absB
    val ci: CharacterIterator = StringCharacterIterator("KMGTPE")
    var i = 40
    while (i >= 0 && absB > 0xfffccccccccccccL shr i) {
        value = value shr 10
        ci.next()
        i -= 10
    }
    value *= java.lang.Long.signum(this).toLong()
    return java.lang.String.format(Locale.getDefault(), "%.1f%cB", value / 1024.0, ci.current())
}