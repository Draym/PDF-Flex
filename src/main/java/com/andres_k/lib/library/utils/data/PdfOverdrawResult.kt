package com.andres_k.lib.library.utils.data

import com.andres_k.lib.library.core.component.PdfComponent

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
data class PdfOverdrawResult(
    val main: PdfComponent? = null,
    val overdraw: PdfComponent? = null
)
