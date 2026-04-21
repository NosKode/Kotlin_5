package com.example.pr_5_2

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PhotoItem(
    val file: File,
    val name: String
)

class PhotoGalleryViewModel(application: Application) : AndroidViewModel(application) {

    private val _photos = MutableStateFlow<List<PhotoItem>>(emptyList())
    val photos: StateFlow<List<PhotoItem>> = _photos.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val picturesDir: File
        get() = getApplication<Application>().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: File(getApplication<Application>().filesDir, "Pictures")

    init {
        loadPhotos()
    }

    fun loadPhotos() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val photosList = mutableListOf<PhotoItem>()

                if (!picturesDir.exists()) {
                    picturesDir.mkdirs()
                }

                picturesDir.listFiles()?.forEach { file ->
                    if (file.isFile && file.extension.lowercase() == "jpg") {
                        photosList.add(
                            PhotoItem(
                                file = file,
                                name = file.name
                            )
                        )
                    }
                }

                // Sort by date (newest first)
                photosList.sortByDescending { it.file.lastModified() }

                _photos.value = photosList
            }
        }
    }

    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_${timeStamp}.jpg"

        if (!picturesDir.exists()) {
            picturesDir.mkdirs()
        }

        return File(picturesDir, imageFileName)
    }

    fun exportToGallery(photoItem: PhotoItem) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val context = getApplication<Application>()

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // Use MediaStore for Android 10+
                        val contentValues = ContentValues().apply {
                            put(MediaStore.Images.Media.DISPLAY_NAME, photoItem.name)
                            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                        }

                        val uri = context.contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            contentValues
                        )

                        uri?.let {
                            context.contentResolver.openOutputStream(it)?.use { outputStream ->
                                photoItem.file.inputStream().use { inputStream ->
                                    inputStream.copyTo(outputStream)
                                }
                            }
                            _snackbarMessage.value = "Фото добавлено в галерею"
                        }
                    } else {
                        // For older versions, copy to public Pictures directory
                        val publicPicturesDir = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES
                        )
                        if (!publicPicturesDir.exists()) {
                            publicPicturesDir.mkdirs()
                        }

                        val destFile = File(publicPicturesDir, photoItem.name)
                        photoItem.file.copyTo(destFile, overwrite = true)

                        // Notify media scanner using MediaScannerConnection
                        android.media.MediaScannerConnection.scanFile(
                            context,
                            arrayOf(destFile.absolutePath),
                            arrayOf("image/jpeg"),
                            null
                        )

                        _snackbarMessage.value = "Фото добавлено в галерею"
                    }
                } catch (e: Exception) {
                    _snackbarMessage.value = "Ошибка при экспорте: ${e.message}"
                }
            }
        }
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }
}
