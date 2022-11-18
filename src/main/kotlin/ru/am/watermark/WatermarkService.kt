package ru.am.watermark

import java.awt.Color
import java.awt.image.BufferedImage

class WatermarkService {

    fun addWatermark(
        image: BufferedImage, watermark: BufferedImage,
        percentage: Int, transparencyBehaviour: TransparencyBehaviour
    ): BufferedImage {

        val result = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
        for (i in 0 until image.width) {
            for (j in 0 until image.height) {
                val imageColor = Color(image.getRGB(i, j))

                val color = if (!transparencyBehaviour.alpha) {
                    val watermarkColor = Color(watermark.getRGB(i, j))
                    if (transparencyBehaviour.color == null) {
                        colorWithoutTransparency(percentage, watermarkColor, imageColor)
                    } else {
                        colorWithTransparencyColor(percentage, watermarkColor, imageColor, transparencyBehaviour.color)
                    }
                } else {
                    val watermarkColor = Color(watermark.getRGB(i, j), true)
                    colorWithTransparency(percentage, watermarkColor, imageColor)
                }
                result.setRGB(i, j, color.rgb)
            }
        }
        return result
    }

    private fun colorWithTransparencyColor(
        percentage: Int,
        watermarkColor: Color,
        imageColor: Color,
        transparencyColor: Color
    ): Color {
        return if (watermarkColor == transparencyColor)
            imageColor
        else
            colorWithoutTransparency(percentage, watermarkColor, imageColor)
    }

    private fun colorWithTransparency(percentage: Int, watermarkColor: Color, imageColor: Color): Color {
        return if (watermarkColor.alpha == 0)
            imageColor
        else
            colorWithoutTransparency(percentage, watermarkColor, imageColor)
    }

    private fun colorWithoutTransparency(percentage: Int, watermarkColor: Color, imageColor: Color) = Color(
        (percentage * watermarkColor.red + (100 - percentage) * imageColor.red) / 100,
        (percentage * watermarkColor.green + (100 - percentage) * imageColor.green) / 100,
        (percentage * watermarkColor.blue + (100 - percentage) * imageColor.blue) / 100
    )
}