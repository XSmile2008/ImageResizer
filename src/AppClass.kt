import javafx.application.Application
import javafx.fxml.FXMLLoader.load
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import org.imgscalr.Scalr
import utils.deleteRecursive
import utils.openImage
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Created by vladstarikov on 9/25/16.
 */
class AppClass : Application() {

    val layout = "layout/main.fxml"

    override fun start(stage: Stage?) {
        stage!!.scene = Scene(load<Parent?>(javaClass.classLoader.getResource(layout)), 1280.0, 720.0)
        stage.show()
    }

    companion object {

        val samplesPath = "/Users/vladstarikov/Desktop/temp"
        val inputFolder = "/input"
        val outputFolder = "/output"

        @JvmStatic
        fun main(args: Array<String>) {
            launch(AppClass::class.java)
        }

        private fun temp() {
            val input = File(samplesPath + inputFolder)
            val output = File(samplesPath + outputFolder)
            deleteRecursive(output)
            output.mkdir()
            var img: BufferedImage? = openImage(File(input, "images.png"))
            if (img != null) {
                img = Scalr.resize(img, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.AUTOMATIC, img.width / 2, img.height / 2)
//                val converted = changeType(resized, BufferedImage.TYPE_INT_RGB)
                ImageIO.write(img, "png", File(output, "images.png"))
            }
        }
    }
}