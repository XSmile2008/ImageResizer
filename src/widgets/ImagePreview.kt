package widgets

import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane

/**
 * Created by vladstarikov on 5/3/17.
 */

class ImagePreview : Pane() {

    private val imageView = ImageView()

    init {
        children.add(imageView)
        imageView.imageProperty().addListener { observable, oldValue, newValue -> centerImage() }
        heightProperty().addListener { observable, oldValue, newValue -> centerImage() }
        widthProperty().addListener { observable, oldValue, newValue -> centerImage() }
    }

    fun setImage(image: Image) {
        imageView.image = image
    }

    private fun centerImage() {
        if (imageView.image != null) {
            imageView.x = width / 2 - imageView.image.width / 2
            imageView.y = height / 2 - imageView.image.height / 2
        }
    }
}