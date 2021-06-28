package com.andres_k.lib.library.core.component

enum class ComponentTypeCode(val code: String) {
    COL("COL"),
    ROW("ROW"),
    LIST("LIST"),
    VIEW("VIEW"),
    IMAGE("IMAGE"),
    TABLE("TABLE"),
    TEXT("TEXT"),
    PARAGRAPH("PARAGRAPH"),
    PAGE("PAGE"),
    PAGE_NB("PAGE_NB"),
    PAGE_BREAK("PAGE_BREAK"),
    SHAPE("SHAPE");

    val type = ComponentType(code = this.code)
}

data class ComponentType(val code: String) {

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is ComponentTypeCode -> this.code == other.code
            else -> super.equals(other)
        }
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}
