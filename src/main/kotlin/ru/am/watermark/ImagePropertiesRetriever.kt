package ru.am.watermark

import java.awt.image.BufferedImage

class ImagePropertiesRetriever {

    fun retrieveProperties(name: String, image: BufferedImage): ImageProperties {
        return ImageProperties(
            name,
            image.width,
            image.height,
            image.colorModel.numComponents,
            image.colorModel.numColorComponents,
            image.colorModel.pixelSize,
            getTransparencyName(image.transparency)
        )
    }

    private fun getTransparencyName(transparency: Int): Transparency {
        return when (transparency) {
            1 -> Transparency.OPAQUE
            2 -> Transparency.BITMASK
            else -> Transparency.TRANSLUCENT
        }
    }
}