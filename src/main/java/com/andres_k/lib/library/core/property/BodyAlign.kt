package com.andres_k.lib.library.core.property

import com.andres_k.lib.library.utils.bigger

/**
 * Created on 2020/07/20.
 *
 * @author Kevin Andres
 */
data class BodyAlign(
    val horizontal: AlignHorizontal = AlignHorizontal.LEFT,
    val vertical: AlignVertical = AlignVertical.TOP
) {
    companion object {
        val CENTER = BodyAlign(AlignHorizontal.CENTER, AlignVertical.CENTER)
        val CENTER_RIGHT = BodyAlign(AlignHorizontal.RIGHT, AlignVertical.CENTER)
        val CENTER_LEFT = BodyAlign(AlignHorizontal.LEFT, AlignVertical.CENTER)
        val TOP_CENTER = BodyAlign(AlignHorizontal.CENTER, AlignVertical.TOP)
        val TOP_RIGHT = BodyAlign(AlignHorizontal.RIGHT, AlignVertical.TOP)
        val TOP_LEFT = BodyAlign(AlignHorizontal.LEFT, AlignVertical.TOP)
        val BOTTOM_CENTER = BodyAlign(AlignHorizontal.CENTER, AlignVertical.BOTTOM)
        val BOTTOM_RIGHT = BodyAlign(AlignHorizontal.RIGHT, AlignVertical.BOTTOM)
        val BOTTOM_LEFT = BodyAlign(AlignHorizontal.LEFT, AlignVertical.BOTTOM)
    }
}

enum class AlignHorizontal {
    LEFT {
        override fun transform(child: Box2d, margin: Spacing, parent: Box2d): Float {
            return margin.left
        }
    },
    CENTER {
        override fun transform(child: Box2d, margin: Spacing, parent: Box2d): Float {
            val t = (parent.width - (child.width + margin.spacingX())) / 2
            return if (t.bigger(0f)) t else margin.left
        }
    },
    RIGHT {
        override fun transform(child: Box2d, margin: Spacing, parent: Box2d): Float {
            val t = parent.width - child.width - margin.right
            return if (t.bigger(0f)) t else 0f
        }
    };

    abstract fun transform(child: Box2d, margin: Spacing, parent: Box2d): Float
}

enum class AlignVertical {
    TOP {
        override fun transform(child: Box2d, margin: Spacing, parent: Box2d): Float {
            return margin.top
        }
    },
    CENTER {
        override fun transform(child: Box2d, margin: Spacing, parent: Box2d): Float {
            val t = (parent.height - (child.height + margin.spacingY())) / 2
            return if (t.bigger(0f)) t else margin.top
        }
    },
    BOTTOM {
        override fun transform(child: Box2d, margin: Spacing, parent: Box2d): Float {
            val t = parent.height - child.height - margin.bottom
            return if (t.bigger(0f)) t else 0f
        }
    };

    abstract fun transform(child: Box2d, margin: Spacing, parent: Box2d): Float
}
