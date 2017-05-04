package utils

import com.mortennobel.imagescaling.experimental.ResampleOpSingleThread
import entities.ImageEntity
import entities.ImageSize
import enum.Algorithm
import enum.AndroidScale
import enum.JavaImageScalingFilter
import net.coobird.thumbnailator.Thumbnailator
import org.imgscalr.Scalr
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

fun save(directory: File, prefix: String, image: ImageEntity, scales: List<AndroidScale>, algorithm: Algorithm, method: Enum<*>?) {
    scales.forEach {
        val subDirectory = File(directory, "$prefix-${it.name.toLowerCase()}")
        save(subDirectory, image, it.ratio, algorithm, method)
    }
}

fun save(directory: File, image: ImageEntity, ratio: Double, algorithm: Algorithm, method: Enum<*>?) {
    if (directory.exists() || directory.mkdir()) {
        val size = image.getScaledSize(ratio)
        var outputImg: BufferedImage? = null

        @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
        when (algorithm) {
            Algorithm.Thumbnailator -> {
                outputImg = Thumbnailator.createThumbnail(image.img, size.width, size.height)
            }
            Algorithm.JavaImageScaling -> if (method is JavaImageScalingFilter) {
                val op = ResampleOpSingleThread(size.width, size.height)
                op.filter = method.filter
                outputImg = op.filter(image.img, null)
            }
            Algorithm.Scalr -> if (method is Scalr.Method) {
                outputImg = Scalr.resize(image.img, method, Scalr.Mode.AUTOMATIC, size.width, size.width)
            }
        }

        ImageIO.write(outputImg, "png", File(directory, "${image.name}.png"))
    } else {
        //TODO: handle that
    }
}

fun formatOriginSize(imageSizePx: ImageSize, scale: AndroidScale): String {
    val scaled = imageSizePx.scale(scale.ratio)
    return "${scale.name.toLowerCase()}(${"%.2f".format(scale.ratio)}x) - ${scaled.width}x${scaled.height} dp"
}

fun formatDestinationSize(image: ImageEntity, scale: AndroidScale): String {
    val scaled = image.getScaledSize(scale.ratio)
    return "${scale.name.toLowerCase()}(${"%.2f".format(scale.ratio)}x) - ${scaled.width}x${scaled.height} px"
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