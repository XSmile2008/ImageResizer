package utils

import java.io.File

/**
 * Created by vladstarikov on 3/25/17.
 */

fun deleteRecursive(file: File) {
    if (file.isDirectory) {
        file.listFiles().forEach(::deleteRecursive)
    }
    file.delete()
}