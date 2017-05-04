import javafx.application.Application
import javafx.fxml.FXMLLoader.load
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

/**
 * Created by vladstarikov on 9/25/16.
 */
class AppClass : Application() {

    @Suppress("unused")
    val APP_VERSION = "0.2"

    private val layout = "layout/main.fxml"

    override fun start(stage: Stage?) {
        stage!!.scene = Scene(load<Parent?>(javaClass.classLoader.getResource(layout)))
        stage.title = "ImageResizer"
        stage.show()
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            launch(AppClass::class.java)
        }
    }
}