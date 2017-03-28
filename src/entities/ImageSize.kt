package entities

/**
 * Created by vladstarikov on 3/25/17.
 */
class ImageSize(var width: Int, var height: Int) {

    fun scale(ratio: Double): ImageSize = ImageSize((width * ratio).toInt(), (height * ratio).toInt())

    override fun toString(): String = "ImageSize(width=$width, height=$height)"
}