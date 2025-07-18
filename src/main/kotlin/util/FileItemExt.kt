package pw.coding.util

import io.ktor.http.content.*
import pw.coding.util.Constants.PROFILE_PICTURE_PATH
import java.io.File
import java.util.*

fun PartData.FileItem.save(path: String): String{
    val fileBytes = streamProvider().use { it.readAllBytes() }
    val fileExtension = originalFileName?.takeLastWhile { it != '.' }
    val fileName= UUID.randomUUID().toString()+ "." + fileExtension
    val folder = File(path)
    folder.mkdirs()
    File("$path$fileName").writeBytes(fileBytes)
    return fileName
}