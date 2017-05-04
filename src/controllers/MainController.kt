package controllers

import entities.ImageEntity
import enum.Algorithm
import enum.AndroidScale
import enum.JavaImageScalingFilter
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
import org.imgscalr.Scalr
import utils.formatDestinationSize
import utils.formatOriginSize
import utils.openImage
import utils.save
import widgets.ImagePreview
import java.net.URL
import java.util.*

/**
 * Created by vladstarikov on 2/9/17.
 * MainController
 */

class MainController : Initializable {

    @FXML lateinit var containerRight: Parent

    @FXML lateinit var btnOpen: Button

    @FXML lateinit var imgPreview: ImagePreview

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
    @FXML lateinit var cobxMethod: ComboBox<Enum<*>>

    @FXML lateinit var fDirectoryPrefix: TextField
    @FXML lateinit var fName: TextField
    @FXML lateinit var btnSave: Button

    private var image: ImageEntity? = null

    private val fWithListener = ChangeListener<String> { observable, oldValue, newValue ->
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

    private val fHeightListener = ChangeListener<String> { observable, oldValue, newValue ->
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
                tgOriginSizeCustom -> image!!.setOriginScale(AndroidScale.MDPI.ratio)
                tgOriginSizeLDPI -> image!!.setOriginScale(AndroidScale.LDPI.ratio)
                tgOriginSizeMDPI -> image!!.setOriginScale(AndroidScale.MDPI.ratio)
                tgOriginSizeHDPI -> image!!.setOriginScale(AndroidScale.HDPI.ratio)
                tgOriginSizeXHDPI -> image!!.setOriginScale(AndroidScale.XHDPI.ratio)
                tgOriginSizeXXHDPI -> image!!.setOriginScale(AndroidScale.XXHDPI.ratio)
                tgOriginSizeXXXHDPI -> image!!.setOriginScale(AndroidScale.XXXHDPI.ratio)
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
                    cobxMethod.items.addAll(JavaImageScalingFilter.values())
                    cobxMethod.selectionModel.select(JavaImageScalingFilter.Lanczos3)
                }
                Algorithm.Scalr -> {
                    cobxMethod.isDisable = false
                    cobxMethod.items.clear()
                    cobxMethod.items.addAll(Scalr.Method.values())
                    cobxMethod.selectionModel.select(Scalr.Method.ULTRA_QUALITY)
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
                val file = FileChooser().showOpenDialog(btnOpen.scene.window)
                val rawImage = openImage(file)
                if (rawImage != null) {
                    image = ImageEntity(rawImage, file.nameWithoutExtension)
                    imgPreview.setImage(SwingFXUtils.toFXImage(rawImage, null))
                    fName.text = image!!.name
                    updateSizesFields()
                    updateOriginSizes()
                    updateDestinationSizes()
                    containerRight.isDisable = false
                } else if (file != null) {
                    Alert(Alert.AlertType.ERROR, "Can't open file").show()
                }
            }
            btnSave -> {
                val chooser = DirectoryChooser()
                val directory = chooser.showDialog(btnSave.scene.window)
                if (directory != null) {
                    val scales = ArrayList<AndroidScale>()
                    if (chbxDestinationSizeLDPI.isSelected) {
                        scales.add(AndroidScale.LDPI)
                    }
                    if (chbxDestinationSizeMDPI.isSelected) {
                        scales.add(AndroidScale.MDPI)
                    }
                    if (chbxDestinationSizeHDPI.isSelected) {
                        scales.add(AndroidScale.HDPI)
                    }
                    if (chbxDestinationSizeXHDPI.isSelected) {
                        scales.add(AndroidScale.XHDPI)
                    }
                    if (chbxDestinationSizeXXHDPI.isSelected) {
                        scales.add(AndroidScale.XXHDPI)
                    }
                    if (chbxDestinationSizeXXXHDPI.isSelected) {
                        scales.add(AndroidScale.XXXHDPI)
                    }
                    save(directory, fDirectoryPrefix.text, image!!, scales, cobxAlgorithm.value, cobxMethod.value)
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
        tgOriginSizeLDPI.text = formatOriginSize(image!!.sizePx, AndroidScale.LDPI)
        tgOriginSizeMDPI.text = formatOriginSize(image!!.sizePx, AndroidScale.MDPI)
        tgOriginSizeHDPI.text = formatOriginSize(image!!.sizePx, AndroidScale.HDPI)
        tgOriginSizeXHDPI.text = formatOriginSize(image!!.sizePx, AndroidScale.XHDPI)
        tgOriginSizeXXHDPI.text = formatOriginSize(image!!.sizePx, AndroidScale.XXHDPI)
        tgOriginSizeXXXHDPI.text = formatOriginSize(image!!.sizePx, AndroidScale.XXXHDPI)
    }

    private fun updateDestinationSizes() {
        chbxDestinationSizeLDPI.text = formatDestinationSize(image!!, AndroidScale.LDPI)
        chbxDestinationSizeMDPI.text = formatDestinationSize(image!!, AndroidScale.MDPI)
        chbxDestinationSizeHDPI.text = formatDestinationSize(image!!, AndroidScale.HDPI)
        chbxDestinationSizeXHDPI.text = formatDestinationSize(image!!, AndroidScale.XHDPI)
        chbxDestinationSizeXXHDPI.text = formatDestinationSize(image!!, AndroidScale.XXHDPI)
        chbxDestinationSizeXXXHDPI.text = formatDestinationSize(image!!, AndroidScale.XXXHDPI)
    }
}