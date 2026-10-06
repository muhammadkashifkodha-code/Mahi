package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

object ProjectExportHelper {

    private const val ZIP_ASSET_NAME = "mahi_ai_sathi_project.zip"
    private const val EXPORT_FILE_NAME = "Mahi_AI_Sathi_Complete_Project.zip"

    fun shareProjectZip(context: Context) {
        try {
            val cacheFile = File(context.cacheDir, EXPORT_FILE_NAME)
            copyAssetToFile(context, ZIP_ASSET_NAME, cacheFile)

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                cacheFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/zip"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Mahi AI Sathi Complete Android Project ZIP")
                putExtra(Intent.EXTRA_TEXT, "Mahi AI Sathi Android Project Source Code (Kotlin & Jetpack Compose).")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Project ZIP Share / Export Karein")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Export error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun saveZipToDownloads(context: Context) {
        try {
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, EXPORT_FILE_NAME)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/zip")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    inputStream = context.assets.open(ZIP_ASSET_NAME)
                    outputStream = context.contentResolver.openOutputStream(uri)
                    if (outputStream != null) {
                        inputStream.copyTo(outputStream)
                        Toast.makeText(context, "ZIP Downloads folder mein save ho gayi! 📥", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val targetFile = File(downloadsDir, EXPORT_FILE_NAME)
                inputStream = context.assets.open(ZIP_ASSET_NAME)
                outputStream = FileOutputStream(targetFile)
                inputStream.copyTo(outputStream)
                Toast.makeText(context, "ZIP Downloads folder mein save ho gayi! 📥", Toast.LENGTH_LONG).show()
            }

            inputStream?.close()
            outputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to share dialog
            shareProjectZip(context)
        }
    }

    private fun copyAssetToFile(context: Context, assetName: String, destination: File) {
        context.assets.open(assetName).use { input ->
            FileOutputStream(destination).use { output ->
                input.copyTo(output)
            }
        }
    }
}
