package com.totem.ia.text

object TextSanitizer {
    private val markdownLinkRegex = Regex("\\[([^\\]]+)]\\(([^)]+)\\)")
    private val footnoteRegex = Regex("\\[\\^\\d+]")
    private val repeatedWhitespaceRegex = Regex("[ \\t]{2,}")
    private val repeatedLineBreakRegex = Regex("\\n{3,}")

    fun forDisplay(text: String): String = text
        .replace(markdownLinkRegex, "$1")
        .replace(footnoteRegex, "")
        .replace("**", "")
        .replace("__", "")
        .replace("`", "")
        .replace("###", "")
        .replace("##", "")
        .replace("#", "")
        .replace("•", "")
        .replace("*", "")
        .replace("_", "")
        .replace(repeatedWhitespaceRegex, " ")
        .replace(repeatedLineBreakRegex, "\n\n")
        .lines()
        .joinToString("\n") { it.trim() }
        .trim()

    fun forSpeech(text: String): String = forDisplay(text)
        .replace("->", " para ")
        .replace("→", " para ")
        .replace("vs.", "versus")
        .replace("vs", "versus")
        .replace("OKRs", "O K Rs")
        .replace("CEO", "C E O")
        .replace("CV", "currículo")
        .replace("IA", "inteligência artificial")
        .replace("/", " ou ")
        .replace(repeatedWhitespaceRegex, " ")
        .trim()
}
