package com.andres_k.lib.library.utils.config

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
class PdfPageProperties(
    val pageNumber: Int,
    val pageTotal: Int
) {
    companion object {
        val ORIGIN = PdfPageProperties(0, 1)
    }
}
