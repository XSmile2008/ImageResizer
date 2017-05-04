package widgets

import javafx.scene.image.ImageView
import javafx.scene.layout.Pane

/**
 * Created by vladstarikov on 5/3/17.
 */

class ImagePreview : Pane() {

    private val imageView = ImageView()

    init {
        children.add(imageView)
    }
}