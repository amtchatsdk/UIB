package com.unified.inbox.utils

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Environment
import android.os.Handler
import android.widget.Toast
import com.unified.inbox.beans.UIBContactObject
import java.io.File
import java.io.FileWriter
import java.io.IOException


class StoreVcfContactAsync(fileUri: String, uibContactObject: UIBContactObject, context: Context) :
    AsyncTask<File, Void, File>() {

    private var file: String? = null
    private var context: Context? = null

    private var uibContactObject: UIBContactObject? = null


    init {
        this.file = fileUri
        this.context = context
        this.uibContactObject = uibContactObject


    }


    override fun doInBackground(vararg params: File?): File? {
        try {
            val extStorageDirectory: String =
                "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath}"
            val dir = File(extStorageDirectory, "myContacts")
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val directory = File(dir, "${uibContactObject?.contactName}.vcf")
            try {
                if (!directory.exists()) directory.createNewFile()
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
            var fw: FileWriter? = null
            fw = FileWriter(directory)
            fw.write("BEGIN:VCARD\r\n")
            fw.write("VERSION:3.0\r\n")
            fw.write("N:${uibContactObject?.contactName} \r\n");
            fw.write(
                """
            FN:${uibContactObject?.contactName}
            
            """.trimIndent()
            )
            //  fw.write("ORG:" + p.getCompanyName() + "\r\n");
            //  fw.write("TITLE:" + p.getTitle() + "\r\n");
            fw.write("TEL;TYPE=HOME,VOICE: ${uibContactObject?.phoneNumber}" + "\r\n");
            //   fw.write("ADR;TYPE=WORK:;;" + p.getStreet() + ";" + p.getCity() + ";" + p.getState() + ";" + p.getPostcode() + ";" + p.getCountry() + "\r\n");
            fw.write("END:VCARD\r\n")
            fw.close()
            val handler = Handler(context?.getMainLooper())
            handler.post(Runnable {
                Toast.makeText(
                    context,
                    "Created",
                    Toast.LENGTH_SHORT
                ).show()
            })
            return directory

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null

    }


    override fun onPostExecute(result: File?) {
        super.onPostExecute(result)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(
            intent.data,
            "text/x-vcard"
        )
        context?.startActivity(intent);

    }


}