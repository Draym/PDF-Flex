PDF-Flex [![CI](https://github.com/Draym/PDF-Flex/actions/workflows/ci-workflow.yml/badge.svg)](https://github.com/Draym/PDF-Flex/actions/workflows/ci-workflow.yml) [![GitHubPackage](https://github.com/Draym/PDF-Flex/actions/workflows/deploy-github-workflow.yml/badge.svg)](https://github.com/Draym/PDF-Flex/actions/workflows/deploy-github-workflow.yml) [![MavenCentral](https://github.com/Draym/PDF-Flex/actions/workflows/deploy-maven-workflow.yml/badge.svg)](https://github.com/Draym/PDF-Flex/actions/workflows/deploy-maven-workflow.yml)
=======

PDF-Flex is a library that can be used to easily compose responsive components within a PDF. It uses [Apache pdfbox](https://github.com/apache/pdfbox) as its root while abstracting
the api into a harmonised set of tools and pre-defined components.

PDF-Flex is designed within a responsive system which allows the components within the PDF to adjust against each-others. Thanks to it, it's now easier to create complex design
with generic and adjustable, both in width & height, component within a PDF.

It allows easier usage of Paragraph, Table, Rows and support multi-page implementation.

It has been developed in an effort of creating a complete easy-to-use high-level PDF generation library which handle correctly the management of generics text / table values.

*Because it abstracts the low-level api of pdfbox, some functionalities may not have been included yet, please create an issue to request new implementation.*

# Features

- Encapsulation API of pdfbox tools into Components
- Parent-ship between container(page, view, row..) and elements(text, paragraph, image..)
- Grid layer system to define components the same way you would design a web page
- Define 14 core components (Table, Paragraph, LineText, Row & Column...) [wiki](https://github.com/Draym/pdf-flex/wiki/Core-Components)
- support generic multi-Pages: components split on page break
- Include base PDF template
    - trouble-less configuration
    - open for customization
- Include pre-defined output export support: Stream, File, Bytes
- Markdown parser and converter to PDF
    - using [Jetbrains/markdown](https://github.com/JetBrains/markdown) extensible parser
    - open to extension with custom interpreters
- Geometry components: [PdfShape](https://github.com/Draym/pdf-flex/wiki/Core-Components#pdfshape)
- Include Custom properties such as page counter or even page-break tag into Markdown parser
- Configurable Fonts & styles (font color, border color, background color)

# Maven

The library is available in both maven central and github-packages. Simply add this dependency in your pom.xml or in your gradle config.

### Maven Central

```xml
<!-- not available yet -->
<dependency>
    <groupId>?</groupId>
    <artifactId>pdf-flex</artifactId>
    <version>1.2.4</version>
</dependency>
```

### Github Packages

If you wish to use github-packages, you need to configure a global (or project based) GitHub
authentication ([docs](https://docs.github.com/en/packages/learn-github-packages/installing-a-package)).

For example, one way would be to create a read PAT token([github token](https://github.com/settings/tokens)) with **read:packages** and add the following in your .m2/settings.xml:

```xml

<server>
    <id>github</id>
    <username>{github_username}</username>
    <password>{github_token}</password>
</server>
```

then add the dependency in your project

```xml

<dependency>
    <groupId>com.andres_k.lib</groupId>
    <artifactId>pdf-flex</artifactId>
    <version>1.2.4</version>
</dependency>
```

# Documentation

The documentation is available on the [WIKI](https://github.com/Draym/pdf-flex/wiki). If you need help, please create an [issue](https://github.com/Draym/pdf-flex/issues).

# Quick Start

The most simple way of starting to generate pdf using PDF-Flex is to use the base class [PdfDocument](https://github.com/Draym/PDF-Flex/wiki/Base-Containers#pdfdocument)

A PdfDocument is an encapsulation of the pdf-box document, it will be in charge of rendering the final PDF by using the components we will provide.

Let's create a simple PDF containing a Header and a title.

```kotlin
/** Initialise a new document */
val drawer = PdfDocument()
```

```kotlin
/** create a header for our document, it will be displayed at the top of each pages */
val header = PdfHeader(content = PdfRow(PdfText("Header")))

/** create a text and set the display mode*/
val title = PdfText(text = "I am a Title!", bodyAlign = BodyAlign.CENTER_CENTER)

/** create a container that will manage our text element */
// by default a Row will occupy all its parent' width, here the page
val row = PdfRow(
    elements = listOf(title),
    borders = Borders.ALL()
)

/** create a new page containing our row */
val page1 = PdfPage(
    elements = listOf(row),
    padding = Spacing(10f, 20f, 10f, 20f)
)
```

```kotlin
val outputFile = File("test.pdf")

/** we use the AutoCloseable feature in order to properly close our document */
drawer.use { document ->

    /** Create an PDF-Flex output, you can customise your output by extending OutputBuilder */
    OutputBuilder.asFile(outputFile).use { output ->

        /** we then 'draw' the PDF into our output (file, bytes...) */
        // PdfDocument.draw will first compute all the pages and arrange the size and position of the components
        // Next it will draw the calculated components into apache/pdf-box in-memory document using text and lines
        // Then using apache/pdf-box it will render it into the output you provide
        document.draw(
            builder = output,
            header = header,
            pages = listOf(page1),
            properties = PdfProperties.DEFAULT
        )
    }
}
```

# Tutorial

#### - How to use PDF-Flex Template? [check here](https://github.com/Draym/PDF-Flex/wiki/TUTO:-use-Template)
