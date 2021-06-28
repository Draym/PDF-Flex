package com.andres_k.lib.library.core.component.element

import com.andres_k.lib.builder.converter.utils.RegexUtil
import com.andres_k.lib.library.core.component.ComponentTypeCode
import com.andres_k.lib.library.core.component.PdfComponent
import com.andres_k.lib.library.core.component.custom.PdfTextLine
import com.andres_k.lib.library.core.component.custom.addText
import com.andres_k.lib.library.core.property.*
import com.andres_k.lib.library.utils.*
import com.andres_k.lib.library.utils.config.PdfContext
import com.andres_k.lib.library.utils.config.PdfProperties
import com.andres_k.lib.library.utils.data.PdfDrawnElement
import com.andres_k.lib.library.utils.data.PdfOverdrawResult
import java.awt.Color

/**
 * Created on 2020/10/27.
 *
 * @author Kevin Andres
 */
@Suppress("DataClassPrivateConstructor")
data class PdfParagraph private constructor(
    val lines: List<PdfTextLine>,
    val interLine: Float?,
    val splitOnOverdraw: Boolean,
    override val identifier: String?,
    override val size: Size,
    override val position: Position,
    override val bodyAlign: BodyAlign?,
    override val padding: Spacing,
    override val margin: Spacing,
    override val color: Color?,
    override val background: Background,
    override val borders: Borders,
    override val isBuilt: Boolean,
) : PdfComponent(identifier, position, size, bodyAlign, padding, margin, color, background, borders, isBuilt, ComponentTypeCode.PARAGRAPH.type) {

    constructor(
        lines: List<PdfTextLine>,
        maxWidth: SizeAttr? = null,
        interLine: Float? = null,
        identifier: String? = null,
        position: Position = Position.ORIGIN,
        bodyAlign: BodyAlign? = null,
        padding: Spacing = Spacing.NONE,
        margin: Spacing = Spacing.NONE,
        splitOnOverdraw: Boolean = true,
        color: Color? = null,
        background: Background = Background.NONE,
        borders: Borders = Borders.NONE,
    ) : this(
        lines = lines,
        interLine = interLine,
        splitOnOverdraw = splitOnOverdraw,
        identifier = identifier,
        size = Size(maxWidth ?: SizeAttr.percent(100f), SizeAttr.percent(100f)),
        position = position,
        bodyAlign = bodyAlign,
        padding = padding,
        margin = margin,
        color = color,
        background = background,
        borders = borders,
        isBuilt = false
    )

    constructor(
        text: String,
        textAlign: BodyAlign? = null,
        textFont: FontCode? = null,
        textFontSize: Float? = null,
        maxWidth: SizeAttr? = null,
        interLine: Float? = null,
        identifier: String? = null,
        position: Position = Position.ORIGIN,
        bodyAlign: BodyAlign? = null,
        padding: Spacing = Spacing.NONE,
        margin: Spacing = Spacing.NONE,
        splitOnOverdraw: Boolean = true,
        color: Color? = null,
        background: Background = Background.NONE,
        borders: Borders = Borders.NONE,
    ) : this(
        lines = text.lines().map { PdfTextLine(text = it, bodyAlign = textAlign, textFont = textFont, textFontSize = textFontSize) },
        interLine = interLine,
        splitOnOverdraw = splitOnOverdraw,
        identifier = identifier,
        size = Size(maxWidth ?: SizeAttr.percent(100f), SizeAttr.percent(100f)),
        position = position,
        bodyAlign = bodyAlign,
        padding = padding,
        margin = margin,
        color = color,
        background = background,
        borders = borders,
        isBuilt = false
    )

    override fun buildContent(context: PdfContext, request: Box2dRequest, parent: BoxSize): PdfComponent {
        val calcPos = Position(calcX(request, parent), calcY(request, parent), PosProperty.FIXED)
        val calcWidth: Float = calcWidth(request, parent)
            ?: throw IllegalArgumentException("[BuildContent] PdfTextArea requires a width at build step")
        val calcHeight: Float? = calcHeight(request, parent)
        val containerWidth: Float = calcWidth - padding.spacingX()
        val containerHeight: Float? = if (calcHeight != null) calcHeight - padding.spacingY() else null

        /** SPLIT lines with over-width **/
        val finalLines: MutableList<PdfTextLine> = mutableListOf()

        lines.forEach { line ->
            if (line.getTextWidth(context).bigger(containerWidth)) {
                val newLines = splitWordInLines(line, containerWidth, context)
                finalLines.addAll(newLines)
            } else {
                finalLines.add(line)
            }
        }

        /** CALCULATE position for each line **/
        val calcElements: MutableList<PdfTextLine> = arrayListOf()
        var cursorY = 0f
        finalLines.forEachIndexed { index, line ->
            val interline = getInterLine(line, index == finalLines.size - 1, context.properties)
            var cursorX = 0f

            val items = line.items.map {
                val item = it.build(context, Box2dRequest(x = cursorX, y = cursorY), BoxSize(containerWidth, containerHeight, type)) as PdfText
                cursorX += item.width()
                item
            }
            val newLine = PdfTextLine.of(items)
            calcElements.add(newLine)
            cursorY += newLine.height() + interline
        }
        val maxHeight = calcHeight ?: cursorY + padding.spacingY()
        return this.copy(lines = calcElements, position = calcPos, size = Size(calcWidth, maxHeight), isBuilt = true)
    }

    override fun calcMaxSize(context: PdfContext, parent: BoxSize): SizeResult {
        val calcWidth: Float = calcWidth(null, parent)!!
        val containerWidth = calcWidth - padding.spacingX()

        var maxHeight = 0f
        lines.forEachIndexed { index, line ->
            if (line.getTextWidth(context).bigger(containerWidth)) {
                val newLines = splitWordInLines(line, containerWidth, context)

                newLines.forEachIndexed { newIndex, newLine ->
                    maxHeight += newLine.getTextHeight(context) + getInterLine(newLine, index == lines.size - 1 && newIndex == newLines.size - 1, context.properties)
                }
            } else {
                maxHeight += line.getTextHeight(context) + getInterLine(line, index == lines.size - 1, context.properties)
            }
        }
        return SizeResult(containerWidth + padding.spacingX() + margin.spacingX(), maxHeight + padding.spacingY() + margin.spacingY())
    }

    private fun splitCharacterFromWordInLines(word: String, font: Font, fontSize: Float, initialCursor: Float, containerWidth: Float): List<PdfText> {
        val newLines: MutableList<String> = mutableListOf("")
        val characters = word.chunked(1)

        var cursor = initialCursor
        var index = 0
        characters.forEach { character ->
            val width = FontUtils.getTextWidth(character, font.font, fontSize)

            if ((cursor + width).bigger(containerWidth)) {
                newLines.add("")
                index += 1
                cursor = 0f
            }
            newLines[index] += character
            cursor += width
        }

        return newLines.map {
            PdfText(
                text = it,
                font = font.code,
                fontSize = fontSize
            )
        }
    }

    private fun splitWordInLines(line: PdfTextLine, containerWidth: Float, context: PdfContext): List<PdfTextLine> {

        val newLines: MutableList<PdfTextLine> = mutableListOf(PdfTextLine.EMPTY)
        var index = 0
        var cursor = 0f

        line.items.forEach { text ->
            val textWidth = text.getTextWidth(context)

            if ((cursor + textWidth).smaller(containerWidth)) {
                newLines.addText(index, text)
                cursor += textWidth
            } else {
                val font = text.getFont(context)
                val fontSize = text.getFontSize(context)
                val words = text.text.split(RegexUtil.buildSplitWithDelim(" ", ",", ";", ".", ":", ")", "]", "}", "\"", "'"))

                words.forEach { word ->
                    val wordWidth = FontUtils.getTextWidth(word, font.font, fontSize)

                    if (wordWidth.bigger(containerWidth) || word.hasCJK()) {
                        val result = splitCharacterFromWordInLines(word, font, fontSize, cursor, containerWidth)

                        if (result.isNotEmpty()) {
                            val first = result.first()
                            val last = result.last()

                            newLines.addText(index, first)

                            val linesToAdd = result.subList(1, result.size)
                            if (linesToAdd.isNotEmpty()) {
                                newLines.addAll(linesToAdd.map { PdfTextLine(it) })
                                index += linesToAdd.size
                                cursor = FontUtils.getTextWidth(last.text, font.font, fontSize)
                            } else {
                                cursor += FontUtils.getTextWidth(first.text, font.font, fontSize)
                            }
                        }
                    } else {
                        if ((cursor + wordWidth).eqOrBigger(containerWidth)) {
                            newLines.add(PdfTextLine.EMPTY)
                            index += 1
                            cursor = 0f
                        }
                        newLines.addText(index, PdfText(text = word, font = font.code, fontSize = fontSize))
                        cursor += wordWidth
                    }
                }
            }
        }
        if (newLines.isNotEmpty()) {
            newLines[newLines.size - 1] = newLines[newLines.size - 1].copy(interLine = line.interLine)
        }
        return newLines
    }

    override fun preRenderContent(context: PdfContext, body: Box2d): PdfOverdrawResult {
        /** Try render elements **/
        var overdrawIndex: Int? = null

        drawRow@ for (i in lines.indices) {
            if (lines[i].hasOverdrawY(body, context.viewBody)) {
                overdrawIndex = if (splitOnOverdraw) i else 0
                break@drawRow
            }
        }

        /** Build List of overdraw elements **/
        if (overdrawIndex != null) {
            val overdrawText = lines.subList(overdrawIndex, lines.size)
            var cursorY = 0f

            val calcOverdrawElements = overdrawText.mapIndexed { index, line ->
                val interline = getInterLine(line, index == overdrawText.size - 1, context.properties)
                val result = line.items.map { text ->
                    text.copyAbs(Position(text.position.x, cursorY), isBuilt = true) as PdfText
                }
                cursorY += line.height() + interline
                PdfTextLine.of(result)
            }
            val mainParagraph = if (overdrawIndex != 0) {
                this.copy(lines = lines.subList(0, overdrawIndex), size = Size(size.width?.v, contentHeight() - cursorY))
            } else null

            return PdfOverdrawResult(
                main = mainParagraph,
                overdraw = this.copy(lines = calcOverdrawElements, size = Size(size.width?.v, cursorY + padding.spacingY()))
            )
        }
        return PdfOverdrawResult(main = this)
    }

    override fun drawContent(context: PdfContext, body: Box2d): List<PdfDrawnElement> {
        val drawLines = lines.map { line ->
            line.items.map { it.draw(context = context, parent = body) }.flatten()
        }.flatten()

        return listOf(
            PdfDrawnElement(
                x = body.x,
                y = body.y,
                xAbs = body.x - padding.left,
                yAbs = body.y - padding.top,
                width = body.width,
                height = body.height,
                type = type,
                identifier = identifier,
                text = null
            )
        ) + drawLines
    }

    private fun getInterLine(line: PdfTextLine, isLastLine: Boolean, properties: PdfProperties): Float {
        val interline = line.interLine ?: interLine ?: properties.defaultInterline

        return if (isLastLine) {
            if (line.forceInterLine) interline else 0f
        } else interline
    }

    override fun getChildren(): List<PdfComponent> {
        return lines.map { it.items }.flatten()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : PdfComponent> copyAbs(
        position: Position?,
        size: Size?,
        bodyAlign: BodyAlign?,
        padding: Spacing?,
        margin: Spacing?,
        color: Color?,
        font: FontCode?,
        background: Background?,
        borders: Borders?,
        isBuilt: Boolean,
    ): T {
        return this.copy(
            position = position ?: this.position,
            bodyAlign = bodyAlign ?: this.bodyAlign,
            padding = padding ?: this.padding,
            margin = margin ?: this.margin,
            color = color ?: this.color,
            background = background ?: this.background,
            borders = borders ?: this.borders,
            isBuilt = isBuilt
        ) as T
    }
}
