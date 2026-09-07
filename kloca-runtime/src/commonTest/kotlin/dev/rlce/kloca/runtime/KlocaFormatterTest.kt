package dev.rlce.kloca.runtime

import kotlin.test.Test
import kotlin.test.assertEquals

class KlocaFormatterTest {
    @Test
    fun formatsPlatformNeutralPlaceholders() {
        assertEquals(
            "Kloca has 7 items and costs 2.5",
            KlocaFormatter.format("{0} has {1} items and costs {2}", arrayOf("Kloca", 7, 2.5)),
        )
    }

    @Test
    fun formatsArgumentsIndependentlyOfTheirPlaceholderType() {
        assertEquals(
            "Name: Kloca, count: 7, price: 2.5, enabled: true",
            KlocaFormatter.format(
                "Name: %1\$s, count: %2\$d, price: %3\$.2f, enabled: %4\$b",
                arrayOf("Kloca", 7, 2.5, true),
            ),
        )
    }

    @Test
    fun supportsIosPlaceholdersReorderingAndEscapedPercents() {
        assertEquals(
            "Second then first: B/A (100%)",
            KlocaFormatter.format("Second then first: %2\$@/%1\$@ (100%%)", arrayOf("A", "B")),
        )
    }

    @Test
    fun supportsSequentialPlaceholders() {
        assertEquals(
            "text 42 1.5 false",
            KlocaFormatter.format("%s %d %f %b", arrayOf("text", 42, 1.5, false)),
        )
    }
}
