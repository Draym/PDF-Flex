package com.andres_k.lib.library.core.property

import com.andres_k.lib.library.core.component.PdfComponent

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */


/** PIXEL **/
data class BoxSize(val width: Float?, val height: Float?, val type: PdfComponent.Type)

data class SizeResult(val width: Float, val height: Float)


/** GENERIC **/

data class SizeAttr(val v: Float, val isPercentage: Boolean = false) {
    companion object {
        fun percent(v: Float): SizeAttr = SizeAttr(v, true)
        fun pixel(v: Float): SizeAttr = SizeAttr(v, false)
        fun full(): SizeAttr = percent(100f)
    }
}

open class Size(val width: SizeAttr? = null, val height: SizeAttr? = null) {
    constructor(width: Float?, height: Float?) : this(get(width), get(height))

    companion object {
        val FULL = Size(SizeAttr.full(), SizeAttr.full())
        val NULL = Size()
        val NONE = Size(get(0f), get(0f))
        fun get(value: Float?, isPercentage: Boolean = false): SizeAttr? = if (value != null) SizeAttr(value, isPercentage) else null
    }
}

class ReqSize(width: SizeAttr, height: SizeAttr) : Size(width, height)
class ReqWidthSize(width: SizeAttr, height: SizeAttr? = null) : Size(width, height)
class ReqHeightSize(width: SizeAttr? = null, height: SizeAttr) : Size(width, height)


