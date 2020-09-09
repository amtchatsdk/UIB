package com.unified.inbox.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unified.inbox.*
import com.unified.inbox.adapters.ContactsAdapter
import com.unified.inbox.adapters.ContactsToBeSentAdapter
import com.unified.inbox.beans.UIBContactObject
import com.unified.inbox.interfaces.AddTopAdapterView
import com.unified.inbox.interfaces.RemoveTopAdapter
import com.unified.inbox.interfaces.UIBContactBookInterface
import com.unified.inbox.utils.ContactReadTask
import kotlinx.android.synthetic.main.fragment_uib_contact.view.*

class ContactFragment(context: FragmentActivity,uibContactBookInterface: UIBContactBookInterface) : Fragment(),
    ContactReadTask.ContactsAvailable,
    RemoveTopAdapter,
    AddTopAdapterView {

    private var uibContactBookInterface: UIBContactBookInterface?=null
    private var contactsToBeSentAdapter: ContactsToBeSentAdapter?=null
    private var myContext: Context? = null

    private var contactsAdapter: ContactsAdapter? = null
    private var contactRecycler: RecyclerView? = null
    private var contactSelectedRecycler: RecyclerView? = null
    private var loadingText: TextView? = null
    private var linerView: TextView? = null
    private var sendContact: ImageView? = null


    init {
          this.uibContactBookInterface=uibContactBookInterface
         this.myContext = context

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mView = inflater.inflate(R.layout.fragment_uib_contact, container, false)
        linerView=mView.vI_Liner
        sendContact=mView.vI_Send_Contact
        contactRecycler = mView.vR_Contact
        contactSelectedRecycler = mView.vR_ContactsSelected
        loadingText = mView.vT_Loading
        contactRecycler?.visibility = View.GONE
        loadingText?.visibility = View.VISIBLE
        contactsAdapter = ContactsAdapter(
            this.requireContext(),
            contactSelectedRecycler!!,
            this,
            this
        )
        ContactReadTask(myContext!!, this).execute()
        sendContact?.setOnClickListener {
            if (contactSelectedRecycler?.visibility==View.VISIBLE){
                println( "Contact To Be Shared ${contactsToBeSentAdapter?.getItem().toString()}")
                Toast.makeText(myContext,"Contact shared",Toast.LENGTH_SHORT).show()
                this.uibContactBookInterface?.shareContacts(contactsToBeSentAdapter?.getItem()!!)
                activity?.onBackPressed()
            }
        }

        return mView
    }


    override fun onContactsAvailable(list: ArrayList<UIBContactObject>) {
        loadingText?.visibility = View.GONE
        contactRecycler?.visibility = View.VISIBLE
        contactRecycler?.layoutManager=LinearLayoutManager(myContext)
        contactRecycler?.adapter=contactsAdapter
        contactRecycler?.hasFixedSize()
        contactsAdapter?.addContacts(list)


    }

    override fun onRemoveAdapter(uibContactObject: UIBContactObject) {
        contactsAdapter?.removeSelectedItem(uibContactObject)
        contactsToBeSentAdapter?.notifyItemRemoved(0)
        contactSelectedRecycler?.visibility=View.GONE
        sendContact?.visibility=View.GONE
        linerView?.visibility=View.GONE
    }

    override fun makeItVisible(uibContactObject: UIBContactObject) {
        sendContact?.visibility=View.VISIBLE
        linerView?.visibility=View.VISIBLE
        contactSelectedRecycler?.visibility=View.VISIBLE
        contactSelectedRecycler?.layoutManager=LinearLayoutManager(this.requireContext(),LinearLayoutManager.HORIZONTAL,false)
        contactsToBeSentAdapter=
            ContactsToBeSentAdapter(
                this.requireContext(),
                this
            )
        contactSelectedRecycler?.adapter=contactsToBeSentAdapter
        contactsToBeSentAdapter?.addContentsToSend(uibContactObject)

    }
}