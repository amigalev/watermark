package ru.am.watermark

data class ImageProperties(
    val name: String,
    val width: Int,
    val height: Int,
    val numComponents: Int,
    val numColorComponents: Int,
    val pixelSize: Int,
    val transparency: Transparency,
)