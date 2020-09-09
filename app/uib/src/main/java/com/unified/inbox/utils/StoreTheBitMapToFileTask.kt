package com.unified.inbox.utils

import android.graphics.Bitmap
import android.os.AsyncTask
import android.util.Log
import com.unified.inbox.interfaces.CheckImageFileAvailability
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*


class StoreTheBitMapToFileTask(
    imageBitmap: Bitmap,
    checkImageFileAvailability: CheckImageFileAvailability,
    filePath: String
) : AsyncTask<Bitmap, File, File>() {
    private var imageSize: Double?=null

    //var context: Context? = null
    private var bitmap: Bitmap? = null
    private var checkImageFileAvailability: CheckImageFileAvailability? = null

    //private var dirName: String? = null
    private var mFilePath: String? = null
    private var fileName: String? = null

    init {
        //dirName = "/experts_snaps"
        this.mFilePath = filePath
        fileName = "expert_profile_photo.jpg"

        //this.context = context
        this.bitmap = imageBitmap
        this.checkImageFileAvailability = checkImageFileAvailability
    }

    override fun doInBackground(vararg params: Bitmap?): File {
        val storeAt = UUID.randomUUID().toString().subSequence(0, 5).toString() + fileName
        //val file=   File(context?.cacheDir,"uib_photo.png")
        /*  val file=   File(filePath,storeAt)
          file.createNewFile()*/

        val fileA = File(mFilePath!!)
        if (!fileA.exists()) {
            fileA.mkdir()
        }
        val snapDirectory = File(mFilePath!!)
        snapDirectory.mkdirs()
        val outputFile = File(snapDirectory, storeAt)
        val bos = ByteArrayOutputStream()
        val bosCompressed = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bos)

        val byteArray = bos.toByteArray()
        when {
            byteArray.size > 5000000 -> {
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, bosCompressed)
            }
            byteArray.size > 3000000 -> {
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, bosCompressed)
            }
            else -> {
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bosCompressed)
            }
        }
        val byteArrayCompressed = bosCompressed.toByteArray()
        //Log.d("imageSizeInMB",((byteArray.size.toString())))
        imageSize= (byteArrayCompressed.size/1024.0)/1024.0

        Log.d("imageSizeInMB",""+imageSize)
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(outputFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        fos?.write(byteArrayCompressed)
        fos?.flush()
        fos?.close()
        return outputFile
    }

    override fun onPostExecute(result: File?) {
        super.onPostExecute(result)
        checkImageFileAvailability!!.fileImageIsReadyForUpload(file = result!!,imageSizeInMB = imageSize!!)

    }


}