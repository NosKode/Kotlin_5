package com.example.pr_5_1

import java.io.File

data class DiaryEntry(
    val fileName: String,
    val timestamp: Long,
    val title: String,
    val preview: String,
    val fullText: String
) {
    companion object {
        fun fromFile(file: File): DiaryEntry? {
            if (!file.exists() || !file.isFile) return null

            val fileName = file.name
            val parts = fileName.removeSuffix(".txt").split("_", limit = 2)
            val timestamp = parts.getOrNull(0)?.toLongOrNull() ?: return null
            val title = parts.getOrNull(1)?.replace("_", " ") ?: ""

            val fullText = file.readText()
            val preview = fullText.take(40)

            return DiaryEntry(
                fileName = fileName,
                timestamp = timestamp,
                title = title,
                preview = preview,
                fullText = fullText
            )
        }

        fun createFileName(timestamp: Long, title: String): String {
            val sanitizedTitle = title
                .trim()
                .replace(Regex("[^a-zA-Zа-яА-Я0-9\\s]"), "")
                .replace(Regex("\\s+"), "_")
                .take(30)

            return if (sanitizedTitle.isNotEmpty()) {
                "${timestamp}_${sanitizedTitle}.txt"
            } else {
                "${timestamp}.txt"
            }
        }
    }
}
