package utils

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

/**
 * Created by vladstarikov on 2/10/17.
 */

fun openImage(file: File): BufferedImage? {
    try {
        return ImageIO.read(file)
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
}

fun changeType(image: BufferedImage, imageType: Int): BufferedImage {
    val result = BufferedImage(image.width, image.height, imageType)
    val graphics = result.graphics
    graphics.drawImage(image, 0, 0, image.width, image.height, null)
    graphics.dispose()
    return result
}

fun resize(image: BufferedImage, width: Int, height: Int): BufferedImage {
    val imgResized = BufferedImage(width, height, image.type)
    val graphics = imgResized.graphics
    graphics.drawImage(image, 0, 0, width, height, null)
    graphics.dispose()
    return imgResized
}