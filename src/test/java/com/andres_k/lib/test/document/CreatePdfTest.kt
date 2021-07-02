package com.andres_k.lib.test.document

import com.andres_k.lib.extension.template.BaseTestTemplate
import com.andres_k.lib.library.core.component.PdfComponent
import com.andres_k.lib.library.core.component.container.PdfCol
import com.andres_k.lib.library.core.component.container.PdfRow
import com.andres_k.lib.library.core.component.custom.PdfTextLine
import com.andres_k.lib.library.core.component.element.PdfParagraph
import com.andres_k.lib.library.core.component.element.PdfText
import com.andres_k.lib.library.core.property.BodyAlign
import com.andres_k.lib.library.core.property.SizeAttr
import com.andres_k.lib.library.core.property.Spacing
import com.andres_k.lib.library.factory.FConf
import com.andres_k.lib.library.factory.conf
import com.andres_k.lib.library.output.OutputBuilder
import com.andres_k.lib.library.output.PdfToFile
import com.andres_k.lib.library.utils.BaseFont
import com.andres_k.lib.parser.PdfParser
import com.andres_k.lib.test.PdfFlexBase
import com.andres_k.lib.wrapper.PDFGeneratedWrapper
import org.apache.pdfbox.pdmodel.PDDocument
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.awt.Color
import java.nio.file.InvalidPathException
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


/**
 * Created on 2021/06/28.
 *
 * @author Kevin Andres
 */
internal class CreatePdfTest : PdfFlexBase() {

    @TempDir
    lateinit var tempDir: Path

    private lateinit var createPdfTestFile: Path

    @BeforeEach
    fun setUp() {
        try {
            createPdfTestFile = tempDir.resolve("CreatePdfTest.pdf")
        } catch (ipe: InvalidPathException) {
            System.err.println("[CreatePdfTest] error creating temporary test file in " + this.javaClass.simpleName)
        }
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun createPDFInMemory() {
        val components = createComponents()
        val template = BaseTestTemplate(components)

        val pdf = template.use { builder ->
            OutputBuilder.asByteArray().use { output ->
                // build the PDF in memory
                // explorer will contains information on what has been actually created
                val explorer = builder.build(output)

                // write the PDF into a byte array
                val pdfAsBytes = output.get()

                // return the generated result data
                PDFGeneratedWrapper(
                    pdf = pdfAsBytes,
                    explorer = explorer
                )
            }
        }
    }

    @Test
    fun createPDFIntoFile() {
        val components = createComponents()
        val template = BaseTestTemplate(components)
        val fileOutput = PdfToFile(createPdfTestFile.toFile())

        val explorer = template.use { builder ->
            fileOutput.use { output ->
                // build the PDF in memory and save it to a file
                builder.build(output)
            }
        }


        assertTrue(createPdfTestFile.exists());

        PDDocument.load(createPdfTestFile.toFile()).use { pdf ->
            val parser = PdfParser(pdf)

            val drawnTitle = explorer.searchText("Hello world").firstOrNull()
            assertNotNull(drawnTitle)

            val resultTitle = parser.search("Hello world")
            println("result title: $resultTitle")
            assertNotNull(resultTitle)
            assertEquals((resultTitle.pageWidth / 2) - (drawnTitle.width / 2), resultTitle.getPositionFromTop().x)
            assertEquals(Color(90, 43, 129), resultTitle.color)
        }
    }


    private fun createComponents(): List<PdfComponent> {
        val fontB = BaseFont.BOLD.code

        /** Title **/
        val rtTxt = FConf(font = fontB, bodyAlign = BodyAlign.CENTER_CENTER, color = Color(90, 43, 129))
        val rowTitle = PdfRow(
            elements = listOf(PdfText("Hello world", fontSize = 17f).conf(rtTxt)),
            margin = Spacing(top = 20f)
        )

        /** Paragraph **/
        val messages = listOf("Test Line1.", "Test Line2.\nTest Line3.")
        val paragraph1 = PdfParagraph(
            lines = messages
                .map { text ->
                    text.lines().map { PdfTextLine(PdfText(text = it, bodyAlign = BodyAlign.LEFT, font = BaseFont.DEFAULT.code, color = Color.BLACK)) }
                }.flatten(),
            bodyAlign = BodyAlign.TOP_LEFT
        )

        val flatMessage = "Test Line1.\n Test Line2.\nTest Line3."
        val paragraph2 = PdfParagraph(
            text = flatMessage,
            textAlign = BodyAlign.LEFT,
            textFont = BaseFont.DEFAULT.code,
            color = Color.BLACK,
            bodyAlign = BodyAlign.TOP_LEFT
        )

        val rowParagraph = PdfRow(
            elements = listOf(
                PdfCol(paragraph1, maxWidth = SizeAttr.percent(50f)),
                PdfCol(paragraph2, maxWidth = SizeAttr.percent(50f))
            ),
            margin = Spacing(top = 20f)
        )

        return listOf(rowTitle, rowParagraph)
    }
}