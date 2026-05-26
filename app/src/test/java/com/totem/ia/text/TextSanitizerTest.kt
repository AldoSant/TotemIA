package com.totem.ia.text

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class TextSanitizerTest {
    @Test
    fun forDisplayRemovesMarkdownNoise() {
        val input = "## **Tema**\n• [Leia aqui](https://exemplo.com) _agora_ [^1]"

        val result = TextSanitizer.forDisplay(input)

        assertEquals("Tema\nLeia aqui agora", result)
        assertFalse(result.contains("*"))
        assertFalse(result.contains("#"))
        assertFalse(result.contains("•"))
        assertFalse(result.contains("[^1]"))
    }

    @Test
    fun forSpeechExpandsSymbolsAndAcronyms() {
        val input = "IA -> CEO/CV vs OKRs"

        val result = TextSanitizer.forSpeech(input)

        assertEquals("inteligência artificial para C E O ou currículo versus O K Rs", result)
    }
}
