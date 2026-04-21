package com.example.pr_5_1

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.io.File

class DiaryViewModel : ViewModel() {
    private val _entries = mutableStateListOf<DiaryEntry>()
    val entries: List<DiaryEntry> = _entries

    private lateinit var filesDir: File

    fun initialize(context: Context) {
        filesDir = context.filesDir
        loadAllEntries()
    }

    private fun loadAllEntries() {
        _entries.clear()
        val files = filesDir.listFiles { file ->
            file.isFile && file.name.endsWith(".txt")
        } ?: return

        val loadedEntries = files
            .mapNotNull { DiaryEntry.fromFile(it) }
            .sortedByDescending { it.timestamp }

        _entries.addAll(loadedEntries)
    }

    fun saveEntry(title: String, text: String) {
        val timestamp = System.currentTimeMillis()
        val fileName = DiaryEntry.createFileName(timestamp, title)
        val file = File(filesDir, fileName)

        file.writeText(text)

        val newEntry = DiaryEntry(
            fileName = fileName,
            timestamp = timestamp,
            title = title,
            preview = text.take(40),
            fullText = text
        )

        _entries.add(0, newEntry)
    }

    fun updateEntry(oldFileName: String, title: String, text: String) {
        val oldFile = File(filesDir, oldFileName)
        val oldEntry = _entries.find { it.fileName == oldFileName } ?: return

        val newFileName = DiaryEntry.createFileName(oldEntry.timestamp, title)
        val newFile = File(filesDir, newFileName)

        if (oldFileName != newFileName && oldFile.exists()) {
            oldFile.delete()
        }

        newFile.writeText(text)

        val index = _entries.indexOfFirst { it.fileName == oldFileName }
        if (index != -1) {
            _entries[index] = DiaryEntry(
                fileName = newFileName,
                timestamp = oldEntry.timestamp,
                title = title,
                preview = text.take(40),
                fullText = text
            )
        }
    }

    fun deleteEntry(fileName: String) {
        val file = File(filesDir, fileName)
        if (file.exists()) {
            file.delete()
        }

        _entries.removeAll { it.fileName == fileName }
    }

    fun getEntry(fileName: String): DiaryEntry? {
        return _entries.find { it.fileName == fileName }
    }
}
