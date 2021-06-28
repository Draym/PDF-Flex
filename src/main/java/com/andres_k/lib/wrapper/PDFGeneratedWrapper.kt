package com.andres_k.lib.wrapper

import com.andres_k.lib.parser.PdfExplorer

data class PDFGeneratedWrapper<T>(
    val pdf: T,
    val explorer: PdfExplorer,
)
