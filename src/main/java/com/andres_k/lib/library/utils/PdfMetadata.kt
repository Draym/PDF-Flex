package com.andres_k.lib.library.utils

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
data class PdfMetadata(
    val creator: String? = null,
    val author: String? = null,
    val title: String? = null,
    val subject: String? = null,
    val custom: List<Pair<String, String>> = emptyList()
) {
    companion object {
        val NONE = PdfMetadata()
    }
}
