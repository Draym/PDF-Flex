package com.andres_k.lib.test.unit.element.text

import com.andres_k.lib.extension.template.BaseTestTemplate
import com.andres_k.lib.library.core.component.container.PdfRow
import com.andres_k.lib.library.core.component.element.PdfText
import com.andres_k.lib.library.core.property.Position
import com.andres_k.lib.library.utils.BaseFont
import com.andres_k.lib.library.utils.FontUtils
import com.andres_k.lib.library.utils.round
import com.andres_k.lib.test.PdfFlexBase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Created on 2021/06/28.
 *
 * @author Kevin Andres
 */
internal class DrawPdfTextTest : PdfFlexBase() {

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun drawText() {
        val text = "Hello world"
        val title = PdfText(text, identifier = "text1", fontSize = 17f)

        val width = FontUtils.getTextWidth(text, properties.getFont(BaseFont.DEFAULT.code).font, 17f)
        val height = FontUtils.getTextHeight(properties.getFont(BaseFont.DEFAULT.code).font, 17f)

        val template = BaseTestTemplate(listOf(PdfRow(listOf(title))))

        template.use { builder ->
            dummyOutput.use { output ->
                val exlorer = builder.build(output)

                val result = exlorer.searchByIdentifier("text1", true)
                assertNotNull(result)
                assertEquals(0f, result.getPositionFromTop().x)
                assertEquals(0f, result.getPositionFromTop().y)
                assertEquals(width, result.width)
                assertEquals(height, result.height)
            }
        }
    }

    @Test
    fun drawCenteredText() {
        val text = "Hello world"
        val title = PdfText(text, identifier = "text1", fontSize = 17f, position = Position.relative(50f, 50f))

        val width = FontUtils.getTextWidth(text, properties.getFont(BaseFont.DEFAULT.code).font, 17f)
        val height = FontUtils.getTextHeight(properties.getFont(BaseFont.DEFAULT.code).font, 17f)

        val template = BaseTestTemplate(listOf(PdfRow(listOf(title), identifier = "row1")))

        template.use { builder ->
            dummyOutput.use { output ->
                val exlorer = builder.build(output)

                val textResult = exlorer.searchByIdentifier("text1", true)
                val rowResult = exlorer.searchByIdentifier("row1", true)
                assertNotNull(textResult)
                assertNotNull(rowResult)
                assertEquals(rowResult.pageWidth, rowResult.width)
                assertEquals(width, textResult.width)
                assertEquals(height, textResult.height)
                assertEquals((rowResult.width / 2).round(3), textResult.getPositionFromTop().x.round(3))
                assertEquals((rowResult.height / 2).round(3), textResult.getPositionFromTop().y.round(3))
            }
        }
    }
}