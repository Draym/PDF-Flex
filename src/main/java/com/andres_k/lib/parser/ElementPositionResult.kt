package com.andres_k.lib.parser

/**
 * Created on 2020/12/10.
 *
 * @author Kevin Andres
 */

/**
 * Element position within a PDF page
 * default origin(0,0) is upper left
 */
data class ElementPositionResult(
    val page: Int,
    val line: Int,
    private val x: Float,
    private val y: Float,
    val pageWidth: Float,
    val pageHeight: Float,
    val identifier: String? = null
) {
    /**
     * Text position where origin(0,0) is upper left
     * @return position expressed in pdf unit
     */
    fun getPositionFromTop(): Pair<Float, Float> {
        return x to y
    }

    /**
     * Text position where origin(0,0) is bottom left
     * @return position expressed in pdf unit
     */
    fun getPositionFromBottom(): Pair<Float, Float> {
        return x to (pageHeight - y)
    }

    /**
     * Text position where origin(0,0) is upper left
     * @return position expressed in mm
     */
    fun getPositionFromTopAsMM(): Pair<Float, Float> {
        return x * MM_PER_POINT to y * MM_PER_POINT
    }

    /**
     * Text position where origin(0,0) is bottom left
     * @return position expressed in mm
     */
    fun getPositionFromBottomAsMM(): Pair<Float, Float> {
        return x * MM_PER_POINT to (pageHeight - y) * MM_PER_POINT
    }

    companion object {
        /** Taken from PDF-Box - user space units per inch  */
        private const val POINTS_PER_INCH = 72f
        private const val POINTS_PER_CM = POINTS_PER_INCH / 2.54f

        /** Taken from PDF-Box - user space units per millimeter  */
        private const val MM_PER_POINT =  1 / (POINTS_PER_CM / 10)
    }
}
