package com.andres_k.lib.test.element.text

import com.andres_k.lib.extension.builder.BuilderTestSupport
import com.andres_k.lib.library.core.component.ComponentTypeCode
import com.andres_k.lib.library.core.component.element.PdfText
import com.andres_k.lib.library.core.property.BodyAlign
import com.andres_k.lib.library.core.property.BoxSize
import com.andres_k.lib.library.core.property.PosProperty
import com.andres_k.lib.library.core.property.Position
import com.andres_k.lib.library.utils.BaseFont
import com.andres_k.lib.library.utils.FontUtils
import com.andres_k.lib.test.PdfFlexBase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Created on 2021/06/28.
 *
 * @author Kevin Andres
 */
internal class ComputePdfTextTest : PdfFlexBase() {

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun computeText() {
        val container = BoxSize(100f, 100f, ComponentTypeCode.ROW.type)
        val text = "Hello world"
        val title = PdfText(text, identifier = "text1", fontSize = 17f)

        val width = FontUtils.getTextWidth(text, properties.getFont(BaseFont.DEFAULT.code).font, 17f)
        val height = FontUtils.getTextHeight(properties.getFont(BaseFont.DEFAULT.code).font, 17f)

        val result = BuilderTestSupport.build(title, container)
        assertEquals("Hello world", result.text)
        assertEquals(0f, result.position.x)
        assertEquals(0f, result.position.y)
        assertEquals(width, result.width())
        assertEquals(height, result.height())
    }

    @Test
    fun computeCenteredText() {
        val container = BoxSize(100f, 100f, ComponentTypeCode.ROW.type)
        val text = "Hello world"
        val title = PdfText(text, identifier = "text1", fontSize = 17f, position = Position.relative(50f, 50f))

        val width = FontUtils.getTextWidth(text, properties.getFont(BaseFont.DEFAULT.code).font, 17f)
        val height = FontUtils.getTextHeight(properties.getFont(BaseFont.DEFAULT.code).font, 17f)

        val result = BuilderTestSupport.build(title, container)
        assertEquals("Hello world", result.text)
        assertEquals(50f, result.position.x)
        assertEquals(50f, result.position.y)
        assertEquals(width, result.width())
        assertEquals(height, result.height())
    }
}