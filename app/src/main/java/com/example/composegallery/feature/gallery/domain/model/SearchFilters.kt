package com.example.composegallery.feature.gallery.domain.model

data class SearchFilters(
    val query: String = "",
    val orientation: Orientation? = null,
    val color: ColorFilter? = null,
    val orderBy: OrderBy = OrderBy.RELEVANT
)

enum class Orientation(val value: String) {
    LANDSCAPE("landscape"),
    PORTRAIT("portrait"),
    SQUARISH("squarish")
}

enum class ColorFilter(val value: String) {
    BLACK_AND_WHITE("black_and_white"),
    BLACK("black"),
    WHITE("white"),
    YELLOW("yellow"),
    ORANGE("orange"),
    RED("red"),
    PURPLE("purple"),
    MAGENTA("magenta"),
    GREEN("green"),
    TEAL("teal"),
    BLUE("blue")
}

enum class OrderBy(val value: String) {
    RELEVANT("relevant"),
    LATEST("latest")
}
