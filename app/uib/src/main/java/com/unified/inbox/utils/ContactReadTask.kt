package com.unified.inbox.utils

import android.content.Context
import android.os.AsyncTask
import android.provider.ContactsContract
import com.unified.inbox.beans.UIBContactObject


class ContactReadTask(var context: Context, contactsAvailable: ContactsAvailable): AsyncTask<Void, ArrayList<UIBContactObject>, ArrayList<UIBContactObject>>() {

    var contactsAvailableIs: ContactsAvailable?=null
    interface ContactsAvailable{
        fun onContactsAvailable(list:ArrayList<UIBContactObject>)
    }

    var myList:ArrayList<UIBContactObject>?=null
    init {
        contactsAvailableIs=contactsAvailable
        myList= ArrayList()
    }
    override fun doInBackground(vararg params: Void?): ArrayList<UIBContactObject>? {
        val contentResolver = context.contentResolver
        val cursor =
            contentResolver?.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)



        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {

                val myContact = UIBContactObject()


                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                /*    val my_contact_Uri: Uri = Uri.withAppendedPath(
                    ContactsContract.Contacts.CONTENT_URI,
                    id
                )
                val photo_stream: InputStream =
                    ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, my_contact_Uri, true)
                val buf = BufferedInputStream(photo_stream)
                val my_btmp = BitmapFactory.decodeStream(buf)
                myContact.contactPhoto=my_btmp
                buf.close()*/
                myContact.contactName = name

                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    val cursorPhone = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                                ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                        arrayOf(id), null
                    )

                    while (cursorPhone?.moveToNext()!!) {
                        val phoneNumber =
                            cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        myContact.phoneNumber = phoneNumber

                    }
                    cursorPhone.close()


                }
                myList?.add(myContact)
            }

        }

        cursor?.close()


        //Log.d("ALL_CONTACTS", myList.toString())

        return myList as ArrayList<UIBContactObject>
    }

    override fun onPostExecute(result: ArrayList<UIBContactObject>?) {
        super.onPostExecute(result)
        contactsAvailableIs?.onContactsAvailable(result !!)

    }
}