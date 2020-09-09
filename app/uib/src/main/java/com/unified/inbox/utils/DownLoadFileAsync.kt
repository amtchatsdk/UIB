package com.unified.inbox.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLConnection


class DownloadFileAsync(mContext: Context, fileUri: String, docName: String) :
    AsyncTask<String, String, File>() {


    private var mContext: Context? = null

    private var fileUri: String? = null

    private var docName: String? = null
    private var yourArray: Array<String>? = null

    init {
        this.mContext = mContext
        this.fileUri = fileUri
        yourArray = docName.split(".").toTypedArray()
        this.docName = yourArray?.get(0)

    }


    override fun doInBackground(vararg aurl: String?): File? {

        val handler = Handler(mContext?.getMainLooper())
        handler.post(Runnable {
            Toast.makeText(
                mContext,
                "Please wait , it will take a moment",
                Toast.LENGTH_SHORT
            ).show()
        })
        try {
            val extStorageDirectory: String =
                "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath}"
            val dir = File(extStorageDirectory, "pdf")
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val directory = File(dir, "$docName.pdf")
            try {
                if (!directory.exists()) directory.createNewFile()
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
            val url = URL(fileUri)
            val conexion: URLConnection = url.openConnection()
            val lenghtOfFile: Int = conexion.contentLength
            conexion.connect()
            conexion.setReadTimeout(10000)
            conexion.setConnectTimeout(15000) // millis
            val f = FileOutputStream(directory)
            val `in`: InputStream = conexion.getInputStream()
            val buffer = ByteArray(1024)
            var len1 = 0
            while (`in`.read(buffer).also({ len1 = it }) > 0) {
                f.write(buffer, 0, len1)
            }
            f.flush()
            f.close()
            `in`.close()

            return directory;

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null

    }

    override fun onPostExecute(result: File?) {
        super.onPostExecute(result)
        showPdfFromSdCard(mContext, result)
    }

    private fun showPdfFromSdCard(ctx: Context?, result: File?) {
        val file =
            result
        file?.setReadable(true)
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        var uri: Uri? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(
                ctx!!,
                ctx.applicationContext.packageName + ".provider",
                file!!
            );

        } else {
            uri = Uri.fromFile(file)
        }
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            ctx?.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                ctx,
                "No Application Available to View PDF",
                Toast.LENGTH_SHORT
            ).show()
        }


    }


}