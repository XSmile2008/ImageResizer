package controllers

import com.mortennobel.imagescaling.experimental.ResampleOpSingleThread
import entities.ImageEntity
import enum.Algorithm
import enum.JavaImageScalingFilters
import javafx.beans.value.ChangeListener
import javafx.embed.swing.SwingFXUtils
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import net.coobird.thumbnailator.Thumbnailator
import org.imgscalr.Scalr
import utils.openImage
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import java.util.*
import javax.imageio.ImageIO

/**
 * Created by vladstarikov on 2/9/17.
 * MainController
 */

class MainController : Initializable {

    val LDPI = 0.75
    val MDPI = 1.00
    val HDPI = 1.50
    val XHDPI = 2.00
    val XXHDPI = 3.00
    val XXXHDPI = 4.00

    @FXML lateinit var containerRight: Parent

    @FXML lateinit var btnOpen: Button

    @FXML lateinit var imgPreview: ImageView

    @FXML lateinit var fWith: TextField
    @FXML lateinit var fHeight: TextField
    @FXML lateinit var chbxLock: CheckBox

    @FXML lateinit var tggOriginSize: ToggleGroup
    @FXML lateinit var tgOriginSizeCustom: RadioButton
    @FXML lateinit var tgOriginSizeLDPI: RadioButton
    @FXML lateinit var tgOriginSizeMDPI: RadioButton
    @FXML lateinit var tgOriginSizeHDPI: RadioButton
    @FXML lateinit var tgOriginSizeXHDPI: RadioButton
    @FXML lateinit var tgOriginSizeXXHDPI: RadioButton
    @FXML lateinit var tgOriginSizeXXXHDPI: RadioButton

    @FXML lateinit var chbxDestinationSizeLDPI: CheckBox
    @FXML lateinit var chbxDestinationSizeMDPI: CheckBox
    @FXML lateinit var chbxDestinationSizeHDPI: CheckBox
    @FXML lateinit var chbxDestinationSizeXHDPI: CheckBox
    @FXML lateinit var chbxDestinationSizeXXHDPI: CheckBox
    @FXML lateinit var chbxDestinationSizeXXXHDPI: CheckBox

    @FXML lateinit var cobxAlgorithm: ComboBox<Algorithm>
    @FXML lateinit var cobxMethod: ComboBox<String>

    @FXML lateinit var fName: TextField
    @FXML lateinit var btnSave: Button

    private var image: ImageEntity? = null

    private var fWithListener = ChangeListener<String> { observable, oldValue, newValue ->
        if (!newValue.matches(Regex("\\d*")) || newValue.isEmpty()) {
            if (!newValue.isEmpty()) {
                fWith.text = newValue.replace(Regex("[^\\d]"), "")
            }
        } else if (newValue.toInt() == 0) {
            fWith.text = ""
        } else {
            image!!.sizeDp.width = newValue.toInt()
            if (chbxLock.isSelected) {
                disableSizesListeners()
                image!!.sizeDp.height = (image!!.sizeDp.width / image!!.getAspectRatio()).toInt()
                fHeight.text = image!!.sizeDp.height.toString()
                enableSizesListeners()
            }
            tgOriginSizeCustom.isSelected = true
            updateDestinationSizes()
        }
    }

    private var fHeightListener = ChangeListener<String> { observable, oldValue, newValue ->
        if (!newValue.matches(Regex("\\d*")) || newValue.isEmpty()) {
            if (!newValue.isEmpty()) {
                fHeight.text = newValue.replace(Regex("[^\\d]"), "")
            }
        } else if (newValue.toInt() == 0) {
            fHeight.text = ""
        } else {
            image!!.sizeDp.height = newValue.toInt()
            if (chbxLock.isSelected) {
                disableSizesListeners()
                image!!.sizeDp.width = (image!!.sizeDp.height * image!!.getAspectRatio()).toInt()
                fWith.text = image!!.sizeDp.width.toString()
                enableSizesListeners()
            }
            tgOriginSizeCustom.isSelected = true
            updateDestinationSizes()
        }
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        enableSizesListeners()

        tggOriginSize.selectedToggleProperty().addListener({ observable, oldValue, newValue ->
            when (newValue) {
                tgOriginSizeCustom -> image!!.setOriginScale(MDPI)
                tgOriginSizeLDPI -> image!!.setOriginScale(LDPI)
                tgOriginSizeMDPI -> image!!.setOriginScale(MDPI)
                tgOriginSizeHDPI -> image!!.setOriginScale(HDPI)
                tgOriginSizeXHDPI -> image!!.setOriginScale(XHDPI)
                tgOriginSizeXXHDPI -> image!!.setOriginScale(XXHDPI)
                tgOriginSizeXXXHDPI -> image!!.setOriginScale(XXXHDPI)
            }
            updateSizesFields()
            updateDestinationSizes()
        })

        fName.textProperty().addListener({ observable, oldValue, newValue ->
            image!!.name = newValue
            btnSave.isDisable = newValue.isEmpty()
        })

        cobxAlgorithm.items.addAll(Algorithm.values())
        cobxAlgorithm.selectionModel.selectedItemProperty().addListener { observable, oldValue, newValue ->
            @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
            when (newValue) {
                Algorithm.Thumbnailator -> {
                    cobxMethod.isDisable = true
                    cobxMethod.items.clear()
                }
                Algorithm.JavaImageScaling -> {
                    cobxMethod.isDisable = false
                    cobxMethod.items.clear()
                    cobxMethod.items.addAll(JavaImageScalingFilters.values().map { it -> it.name })
                    cobxMethod.selectionModel.select(JavaImageScalingFilters.Lanczos3.name)
                }
                Algorithm.Scalr -> {
                    cobxMethod.isDisable = false
                    cobxMethod.items.clear()
                    cobxMethod.items.addAll(Scalr.Method.values().map { it -> it.name })
                    cobxMethod.selectionModel.select(Scalr.Method.ULTRA_QUALITY.name)
                }
            }
        }
        cobxAlgorithm.selectionModel.select(Algorithm.Thumbnailator)
    }

    private fun enableSizesListeners() {
        fWith.textProperty().addListener(fWithListener)
        fHeight.textProperty().addListener(fHeightListener)
    }

    private fun disableSizesListeners() {
        fWith.textProperty().removeListener(fWithListener)
        fHeight.textProperty().removeListener(fHeightListener)
    }

    @FXML
    private fun onClick(event: ActionEvent) {
        when (event.source) {
            btnOpen -> {
                val file = FileChooser().showOpenDialog(null)
                val rawImage = openImage(file)
                if (rawImage != null) {
                    image = ImageEntity(rawImage, file.nameWithoutExtension)
                    imgPreview.image = SwingFXUtils.toFXImage(rawImage, null)
                    fName.text = image!!.name
                    updateSizesFields()
                    updateOriginSizes()
                    updateDestinationSizes()
                    containerRight.isDisable = false
                } else if (file != null) {
                    Alert(Alert.AlertType.ERROR, "Cant open file").show()
                }
            }
            btnSave -> {
                val chooser = DirectoryChooser()
                val directory = chooser.showDialog(null)
                if (directory != null) {
                    save(directory)
                }
            }
        }
    }

    private fun updateSizesFields() {
        disableSizesListeners()
        fWith.text = image!!.sizeDp.width.toString()
        fHeight.text = image!!.sizeDp.height.toString()
        enableSizesListeners()
    }

    private fun updateOriginSizes() {
        val ldpi = image!!.sizePx.scale(1 / LDPI)
        tgOriginSizeLDPI.text = "ldpi(0.75x) - ${ldpi.width}x${ldpi.height} dp"

        val mdpi = image!!.sizePx.scale(1 / MDPI)
        tgOriginSizeMDPI.text = "mdpi(1.00x) - ${mdpi.width}x${mdpi.height} dp"

        val hdpi = image!!.sizePx.scale(1 / HDPI)
        tgOriginSizeHDPI.text = "hdpi(1.50x) - ${hdpi.width}x${hdpi.height} dp"

        val xhdpi = image!!.sizePx.scale(1 / XHDPI)
        tgOriginSizeXHDPI.text = "xhdpi(2.00x) - ${xhdpi.width}x${xhdpi.height} dp"

        val xxhdpi = image!!.sizePx.scale(1 / XXHDPI)
        tgOriginSizeXXHDPI.text = "xxhdpi(3.00x) - ${xxhdpi.width}x${xxhdpi.height} dp"

        val xxxhdpi = image!!.sizePx.scale(1 / XXXHDPI)
        tgOriginSizeXXXHDPI.text = "xxxhdpi(4.00x) - ${xxxhdpi.width}x${xxxhdpi.height} dp"
    }

    private fun updateDestinationSizes() {
        val ldpi = image!!.getScaledSize(LDPI)
        chbxDestinationSizeLDPI.text = "ldpi(0.75x) - ${ldpi.width}x${ldpi.height} px"

        val mdpi = image!!.getScaledSize(MDPI)
        chbxDestinationSizeMDPI.text = "mdpi(1.00x) - ${mdpi.width}x${mdpi.height} px"

        val hdpi = image!!.getScaledSize(HDPI)
        chbxDestinationSizeHDPI.text = "hdpi(1.50x) - ${hdpi.width}x${hdpi.height} px"

        val xhdpi = image!!.getScaledSize(XHDPI)
        chbxDestinationSizeXHDPI.text = "xhdpi(2.00x) - ${xhdpi.width}x${xhdpi.height} px"

        val xxhdpi = image!!.getScaledSize(XXHDPI)
        chbxDestinationSizeXXHDPI.text = "xxhdpi(3.00x) - ${xxhdpi.width}x${xxhdpi.height} px"

        val xxxhdpi = image!!.getScaledSize(XXXHDPI)
        chbxDestinationSizeXXXHDPI.text = "xxxhdpi(4.00x) - ${xxxhdpi.width}x${xxxhdpi.height} px"
    }

    private fun save(directory: File) {
        if (chbxDestinationSizeLDPI.isSelected) {
            save(File(directory, "drawable-ldpi"), LDPI)
        }
        if (chbxDestinationSizeMDPI.isSelected) {
            save(File(directory, "drawable-mdpi"), MDPI)
        }
        if (chbxDestinationSizeHDPI.isSelected) {
            save(File(directory, "drawable-hdpi"), HDPI)
        }
        if (chbxDestinationSizeXHDPI.isSelected) {
            save(File(directory, "drawable-xhdpi"), XHDPI)
        }
        if (chbxDestinationSizeXXHDPI.isSelected) {
            save(File(directory, "drawable-xxhdpi"), XXHDPI)
        }
        if (chbxDestinationSizeXXXHDPI.isSelected) {
            save(File(directory, "drawable-xxxhdpi"), XXXHDPI)
        }
    }

    private fun save(directory: File, ratio: Double) {
        if (directory.exists() || directory.mkdir()) {
            val size = image!!.getScaledSize(ratio)
            var outputImg: BufferedImage? = null

            @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
            when (cobxAlgorithm.selectionModel.selectedItem) {
                Algorithm.Thumbnailator -> {
                    outputImg = Thumbnailator.createThumbnail(image!!.img, size.width, size.height)
                }
                Algorithm.JavaImageScaling -> {
                    val op = ResampleOpSingleThread(size.width, size.height)
                    op.filter = JavaImageScalingFilters.valueOf(cobxMethod.value).filter
                    outputImg = op.filter(image!!.img, null)
                }
                Algorithm.Scalr -> {
                    val method = Scalr.Method.valueOf(cobxMethod.value)
                    outputImg = Scalr.resize(image!!.img, method, Scalr.Mode.AUTOMATIC, size.width, size.width)
                }
            }

            ImageIO.write(outputImg, "png", File(directory, "${image!!.name}.png"))
        }
    }
}