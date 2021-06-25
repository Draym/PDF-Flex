PDF-Flex
=======

PDF-Flex is a library that can be used to easily create generic components within a PDF. It uses [Apache pdfbox](https://github.com/apache/pdfbox) as it's root while abstracting
the api into a harmonised set of tools and pre-defined components.

PDF-Flex is designed within a grid system which allows the components within the PDF to adjust against each-others. Thanks to it, it's now easier to create complex design with
generic and adjustable, both in width & height, component within a PDF.

It allows easier usage of Paragraph, Table, Rows and support multi-page implementation.

It has been developed in an effort of creating a complete easy-to-use high-level PDF generation library which handle correctly the management of generics text / table values.

*Because it abstracts the low-level api of pdfbox, some functionalities may not have been included yet, please create an issue to request new implementation.*

# Features

- Encapsulation API of pdfbox tools into Components
- Parent-ship between container(page, view, row..) and elements(text, paragraph, image..)
- Grid layer system to define components the same way you would design a web page
- Define 14 core components (Table, Paragraph, LineText, Row & Column...) [wiki](https://github.com/Draym/pdf-flex/wiki/Core-Components)
- support generic Multi-Page: configurable components split on page break
- Include base PDF template
    - trouble-less configuration
    - open for customization
- Include pre-define output export support: Stream, File, Bytes
- Markdown parser and converter to PDF
    - using [Jetbrains/markdown](https://github.com/JetBrains/markdown) extensible parser
    - open to extension with custom interpreters
- Geometry components: [PdfShape](https://github.com/Draym/pdf-flex/wiki/Core-Components#pdfshape)
- Include Custom properties such as page counter or even page-break tag into Markdown parser
- Configurable Fonts & styles (font color, border color, background color)

# Maven

TODO

```xml

<dependency>
    <groupId></groupId>
    <artifactId></artifactId>
    <version>1.1.0</version>
</dependency>
```

# Documentation

The documentation is available on the [WIKI](https://github.com/Draym/pdf-flex/wiki). If you need help, please create an [issue](https://github.com/Draym/pdf-flex/issues).

# Tutorial

The default way to generate PDF using PDF-Flex is to follow the following steps:

- create a template by extend the abstract base template **PDFBaseTemplate**
- override the necessary methods to define the comportment of your template
- build the template
- provide an output using the provided factory **OutputBuilder** or use a custom one
- you can now physically generate your PDF or upload it online

### Create a template

Extend the default Template which define the overall structure.

````kolin
class MyTemplate() : PdfBaseTemplate() {
    override fun getFontToLoad(): Map<EFont, TrueTypeFont>
    override fun createHeader(): PdfHeader?
    override fun createFooter(): PdfFooter?
    override fun createPages(): List<PdfPage>
    override fun getPdfDefaultProperties(): PdfProperties
    override fun getPdfDefaultDebugSettings(): PdfContextDebug
}
````

#### Define the font you wish to use

- A fond is defined by a code **BaseFont** which support default, bold and italic, you will send use the code when creating a new component. This will allow the font to be saved
  only once in the builder and be use only when the builder will write out the PDF.
- PDF-Flex use TrueTypeFont type, you can use the utility tools [FontUtils](https://github.com/Draym/pdf-flex/wiki/Tools#fontutil) to load your own font

````kolin
    override fun getFontToLoad(): Map<EFont, TrueTypeFont> {
        return mapOf(
            BaseFont.DEFAULT to FontUtils.loadTTCFont(YAHEI_CLASSIC.fontPath, YAHEI_CLASSIC.fontName),
            BaseFont.BOLD to FontUtils.loadTTCFont(YAHEI_BOLD.fontPath, YAHEI_BOLD.fontName)
        )
    }
````

#### Define the Header & Footer of your PDF: it will be displayed on each pages

- a PdfHeader is simply a base container which hold a component. The most common way would be to put a Row component inside.
- within the Row component you can then put anything you wish your header show.

Let's see an example on how to create a logo header. Here we import an image as PDImageXObject thanks
to [PdfImageLoader](https://github.com/Draym/pdf-flex/wiki/Tools#pdfimageloader) with an image file from the /resources folder.

````kolin
    override fun createHeader(): PdfHeader {
        val logo: PDImageXObject = PdfImageLoader.loadFrom(document.document, "/images/logo.png")
        
        // create a Row component
        val headerContent = PdfRow(
            elements = listOf(
              // add an Image to the Row, the bodyAlign attribute will center the image into the row
              PdfImage(logo, respectParent = true, bodyAlign = BodyAlign.CENTER_LEFT)
            ),
            maxHeight = SizeAttr.pixel(32f)
        )
        return PdfHeader(headerContent)
    }
````

#### Construct your pages

Let's construct a PDF which contains a title, and a list of messages. Visit the [components wiki](https://github.com/Draym/pdf-flex/wiki/Core-Components) for more examples.

````kolin
   override fun createPages(): List<PdfPage> {
        val fontB = BaseFont.BOLD.code

        /** Title **/
        // lets define a reusable configuration, you can specify this data at the component level aswell
        val rtTxt = FConf(font = fontB, bodyAlign = BodyAlign.CENTER_CENTER, color = Color(90, 43, 129))
        
        // let's define a title
        val title = PdfText("Hello world", fontSize = 17f).conf(rtTxt)
        
        // packup the title into the first row
        val rowTitle = PdfRow(
            elements = listOf(myTitle),
            margin = Spacing(top = 20f)
        )
        
        /** Paragraph **/
        // now let's create a paragraph to hold some generic text
        val messages = listOf("This library is awesome", "let's try every available components")
        val paragraphe = PdfParagraph(
                       lines = messages
                            .map { text ->
                                text.lines().map { PdfTextLine(PdfText(text = it, bodyAlign = BodyAlign.LEFT).conf(rtTxt)) }
                            }.flatten(),
                       bodyAlign = BodyAlign.TOP_LEFT
                      )
                      
        // packup the paragraph into the second row
        val rowParagraph = PdfRow(
            elements = listOf(myTitle),
            margin = Spacing(top = 20f)
        )
        
        /** Pages **/
        // create a page with all my rows
        val page1 = PdfPage(
            elements = listOfNotNull(
                rowTitle,
                rowParagraph
            ),
            padding = Spacing(10, 10, 10, 10)
        )
        return listOf(page1)
   }
````

#### Define the settings and default variables

You can define some settings to send to the Builder or keep the default one.

````kolin
    override fun getPdfDefaultProperties(): PdfProperties {
        return PdfProperties(
            defaultFontSize = 11f,
            defaultInterline= 2f, // the size between each lines
            debugOn = true, // if true it will draw additional lines such as container borders to help you debug your design, you can customize 
            color: Color = Color.BLACK, // default component colors (font, border..)
            drawOverflowX = true, // do not cut the components if they happens to draw on each other
            drawOverflowY = true, // do not cut the components if they happens to draw on each other
            createPageOnOverdraw: = true // automatically create new pages
        )
    }
````

You can also define how the visual DebugContext works by modifying the way it prints borders and background. Only the elements given into the Map will have additional printing. You
must set debugOn = false into PdfProperties for final rendering.

````kotlin
    override fun getPdfDefaultDebugSettings(): PdfContextDebug {
    return PdfContextDebug(
        borders = mapOf(
            Type.ROW to Border(Color.RED.withAlpha(0.5f)),
            Type.COL to Border(Color.BLUE.withAlpha(0.5f)),
            Type.TABLE to Border(Color.PINK.withAlpha(0.7f))
        ),
        background = mapOf(
            Type.COL to Color.CYAN.withAlpha(0.05f),
            Type.TEXT to Color.LIGHT_GRAY.withAlpha(0.3f),
            Type.PAGE_NB to Color.ORANGE.withAlpha(0.3f),
            Type.PAGE_BREAK to Color.GREEN.withAlpha(0.3f)
        )
    )
}
````

### Build your template

```kotlin
myTemplate.use { builder ->

    OutputBuilder.asByteArray().use { output ->

        // build the PDF in memory
        // explorer will contains information on what has been actually created
        val explorer = builder.build(output)

        // write the PDF into a byte array
        val pdfAsBytes = output.get()

        // return the generated result data
        return PDFGeneratedWrapper(
            bytes = pdfAsBytes,
            explorer = explorer
        )
    }

}
```
