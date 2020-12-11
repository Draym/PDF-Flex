package com.andres_k.lib.parser

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.pdfbox.text.TextPosition

/**
 * Created on 2020/12/10.
 *
 * @author Kevin Andres
 */
class PdfParser(document: PDDocument) {
    private val parser = CustomParser()

    init {
        parser.getText(document)
    }

    fun debug() {
        parser.getPages().forEach { page ->
            println("----")
            println("PAGE n.${page.index}")
            page.lines.forEach { line ->
                println("LINE [${line.index}]")
                line.sentences.forEach { sentence ->
                    println("${sentence}")
                }
            }
        }
    }

    fun search(value: String): ElementPositionResult? {
        parser.getPages().forEach { page ->
            page.lines.forEach { line ->
                line.sentences.forEach { sentence ->
                    val result = sentence.text.indexOf(value)

                    if (result != -1 && result < sentence.characters.size) {
                        val start = sentence.characters[result]
                        return ElementPositionResult(
                            page = page.index,
                            line = line.index,
                            x = start.x,
                            y = start.y,
                            pageWidth = page.pageWidth,
                            pageHeight = page.pageHeight
                        )
                    }
                }
            }
        }
        return null
    }

    private class CustomParser : PDFTextStripper() {
        private val pages: MutableList<Page> = mutableListOf()
        private var currentPage = 0
        private var currentLine = 0
        private var currentSentence = 0
        private lateinit var previous: TextPosition

        override fun processTextPosition(text: TextPosition?) {
            if (text != null) {
                if (this::previous.isInitialized) {
                    if (previous.y > text.y) {
                        ++currentPage
                    } else if (previous.y < text.y) {
                        ++currentLine
                    }
                    if (text.x - previous.x > previous.width) {
                        ++currentSentence
                    }
                }
                if (pages.size == currentPage) {
                    pages.add(Page(index = currentPage, pageWidth = text.pageWidth, pageHeight = text.pageHeight))
                    currentLine = 0
                    currentSentence = 0
                }
                if (pages[currentPage].lines.size == currentLine) {
                    pages[currentPage].lines.add(Line(index = currentLine))
                    currentSentence = 0
                }
                if (pages[currentPage].lines[currentLine].sentences.size == currentSentence) {
                    pages[currentPage].lines[currentLine].sentences.add(Sentence(character = text))
                } else {
                    val it = pages[currentPage].lines[currentLine].sentences[currentSentence]
                    pages[currentPage].lines[currentLine].sentences[currentSentence] = it.copy(characters = it.characters + text)
                }
                previous = text

            }
        }

        fun getPages(): List<Page> {
            return pages
        }
    }

    private data class Page(val lines: MutableList<Line> = mutableListOf(), val index: Int, val pageWidth: Float, val pageHeight: Float)
    private data class Line(val sentences: MutableList<Sentence> = mutableListOf(), val index: Int)
    private data class Sentence(val characters: List<TextPosition>) {
        constructor(character: TextPosition) : this(listOf(character))

        val text: String = characters.joinToString(separator = "") { it.unicode }
        val width: Float = characters.map { it.width }.sum()

        override fun toString(): String {
            return text
        }
    }
}
