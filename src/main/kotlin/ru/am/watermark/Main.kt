package ru.am.watermark

import java.awt.Color
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
    if (!verify("watermark", watermarkProperties)) {
        return
    }

    if (imageProperties.width != watermarkProperties.width
        || imageProperties.height != watermarkProperties.height) {
        println("The image and watermark dimensions are different.")
        return
    }

    val transparencyBehaviour: TransparencyBehaviour = transparencyBehaviour(watermarkProperties)

    val percentage = askPercentage()
    val output = askOutput()
    if (output.extension != "jpg" && output.extension != "png") {
        println("The output file extension isn't \"jpg\" or \"png\".")
        exitProcess(-1)
    }

    val outputImage = WatermarkService().addWatermark(image, watermark, percentage, transparencyBehaviour)
    ImageIO.write(outputImage, output.extension, output)

    println("The watermarked image $output has been created.")
}

private fun transparencyBehaviour(watermarkProperties: ImageProperties): TransparencyBehaviour {
    val transparencyBehaviour: TransparencyBehaviour
    if (watermarkProperties.transparency == Transparency.TRANSLUCENT) {
        transparencyBehaviour = TransparencyBehaviour(askTransparency(), null)
    } else {
        println("Do you want to set a transparency color?")
        if (askBoolean()) {
            println("Input a transparency color ([Red] [Green] [Blue]):")
            val colors = readln().split(" ")

            if (colors.size != 3
                || !colors.all { it.matches(Regex("[0-9]*")) }
                || !colors.all { it.toInt() in 0..255 }) {

                println("The transparency color input is invalid.")
                exitProcess(-1)
            }

            val intColors = colors.map { it.toInt() }
            transparencyBehaviour = TransparencyBehaviour(
                false,
                Color(intColors[0], intColors[1], intColors[2])
            )
        } else {
            transparencyBehaviour = TransparencyBehaviour(false, null)
        }
    }
    return transparencyBehaviour
}

data class TransparencyBehaviour(val alpha: Boolean, val color: Color?)

private fun askTransparency(): Boolean {
    println("Do you want to use the watermark's Alpha channel?")
    return askBoolean()
}

private fun askBoolean(): Boolean {
    var useTransparency = false
    val answer = readln()
    if ("yes".equals(answer, ignoreCase = true)) {
        useTransparency = true
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