package entities

import java.awt.image.BufferedImage

/**
 * Created by vladstarikov on 3/25/17.
 */
class ImageEntity(var img: BufferedImage, var name: String) {

    var sizePx = ImageSize(img.width, img.height)
    var sizeDp = ImageSize(img.width, img.height)

    private var originScale = 1.0

    fun getAspectRatio(): Double = img.width.toDouble() / img.height.toDouble()

    fun setOriginScale(scale: Double) {
        originScale = scale
        if (originScale != 1.0) {
            sizeDp = sizePx.scale(1 / scale)
        }
    }

    fun getOriginScale(): Double {
        return originScale
    }

    fun getScaledSize(ratio: Double): ImageSize {
        return if (originScale == 1.0) sizeDp.scale(ratio) else sizePx.scale(ratio / originScale)
    }
}