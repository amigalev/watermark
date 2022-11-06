package ru.am.watermark

import java.io.File
import javax.imageio.ImageIO
import kotlin.system.exitProcess

fun main() {
    println("Input the image filename:")
    val imageFile = askFile()
    val image = ImageIO.read(imageFile)

    val retriever = ImagePropertiesRetriever()
    val imageProperties = retriever.retrieveProperties(imageFile.name, image)

    if (!verify("image", imageProperties)) {
        return
    }

    println("Input the watermark image filename:")
    val watermarkFile = askFile()
    val watermark = ImageIO.read(watermarkFile)

    val watermarkProperties = retriever.retrieveProperties(watermarkFile.name, watermark)
    val useTransparency = askTransparencyIfNeeded(watermarkProperties)

    if (!verify("watermark", watermarkProperties)) {
        return
    }

    if (imageProperties.width != watermarkProperties.width
        || imageProperties.height != watermarkProperties.height) {
        println("The image and watermark dimensions are different.")
        return
    }

    val percentage = askPercentage()
    val output = askOutput()
    if (output.extension != "jpg" && output.extension != "png") {
        println("The output file extension isn't \"jpg\" or \"png\".")
        exitProcess(-1)
    }

    val outputImage = WatermarkService().addWatermark(image, watermark, percentage, useTransparency)
    ImageIO.write(outputImage, output.extension, output)

    println("The watermarked image $output has been created.")
}

private fun askTransparencyIfNeeded(watermarkProperties: ImageProperties): Boolean {
    var useTransparency = false
    if (watermarkProperties.transparency == Transparency.TRANSLUCENT) {
        println("Do you want to use the watermark's Alpha channel?")
        val answer = readln()
        if ("yes".equals(answer, ignoreCase = true)) {
            useTransparency = true
        }
    }
    return useTransparency
}

fun askOutput(): File {
    println("Input the output image filename (jpg or png extension):")
    val output = readln()

    return File(output)
}

fun askPercentage(): Int {
    println("Input the watermark transparency percentage (Integer 0-100):")
    val input = readln()
    if (!input.matches(Regex("[0-9]*"))) {
        println("The transparency percentage isn't an integer number.")
        exitProcess(-1)
    }

    val percentage = input.toInt()
    if (percentage !in 0..100) {
        println("The transparency percentage is out of range.")
        exitProcess(-1)
    }
    return percentage
}

fun askFile(): File {
    val path = readln()
    val imageFile = File(path)
    if (!imageFile.exists()) {
        println("The file $path doesn't exist.")
        exitProcess(-1)
    }
    return imageFile
}

fun verify(type: String, properties: ImageProperties): Boolean {
    if (properties.transparency == Transparency.TRANSLUCENT) {
        if (properties.numComponents != 4) {
            println("The number of $type color components isn't 4.")
            return false
        }
    } else {
        if (properties.numComponents != 3) {
            println("The number of $type color components isn't 3.")
            return false
        }
    }

    if (properties.pixelSize != 24 && properties.pixelSize != 32) {
        println("The $type isn't 24 or 32-bit.")
        return false
    }

    return true
}

