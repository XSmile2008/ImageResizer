package entities

import java.awt.image.BufferedImage

/**
 * Created by vladstarikov on 3/25/17.
 */
class ImageEntity(var img: BufferedImage, var name: String) {

    var sizePx = ImageSize(img.width, img.height)
    var sizeDp = ImageSize(img.width, img.height)

    fun getAspectRatio(): Double = img.width.toDouble() / img.height.toDouble()
}