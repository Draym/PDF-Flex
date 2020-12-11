package com.andres_k.lib.library.utils.data

/**
 * Created on 2020/12/11.
 *
 * @author Kevin Andres
 */
data class PdfDrawnPage(
    val index: Int,
    val width: Float,
    val height: Float,
    val drawnElements: List<PdfDrawnElement>,
)
