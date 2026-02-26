package pw.coding.util

import io.ktor.http.content.*
import java.io.File
import java.util.*


fun PartData.FileItem.save(path: String): String? {
    val original = originalFileName ?: return null
    val extension = original.substringAfterLast('.', "")
    if (extension.isBlank()) return null

    val fileName = "${UUID.randomUUID()}.$extension"
    val folder = File(path)
        folder.mkdirs()

    streamProvider().use { input ->
        File(folder, fileName).outputStream().use { output ->
            input.copyTo(output)
        }
    }

    return fileName
}
