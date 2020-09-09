package com.unified.inbox.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.unified.inbox.R
import com.unified.inbox.beans.UIBContactObject
import com.unified.inbox.interfaces.RemoveTopAdapter
import kotlinx.android.synthetic.main.contact_to_be_sent.view.*
import kotlin.collections.ArrayList

class ContactsToBeSentAdapter(context: Context,removeTopAdapter: RemoveTopAdapter): RecyclerView.Adapter<ContactsToBeSentAdapter.ViewHolder>() {


    private var myContext:Context?=null
    private var contactsToSend:ArrayList<UIBContactObject>?=null
    private var removeTopAdapter: RemoveTopAdapter?=null

    init {
        this.removeTopAdapter=removeTopAdapter
        myContext=context
        contactsToSend= ArrayList()
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        val container:RelativeLayout=itemView.vR_Container
        val initial:ImageView=itemView.vT_ContactInitial
        val number:TextView=itemView.vT_ContactNumber
        val deSelector:ImageView=itemView.vI_DeSelector
        val cardDeSelector:FrameLayout=itemView.vF_DeSelector
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val mainView=LayoutInflater.from(parent.context).inflate(R.layout.contact_to_be_sent,parent,false)
        return ViewHolder(
            mainView
        )
    }

    fun addContentsToSend(list: UIBContactObject){
        contactsToSend?.add(list)
        notifyDataSetChanged()
    }
    fun addContentsToRemove(list: UIBContactObject){
        contactsToSend?.remove(list)
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
       return contactsToSend?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {







        holder.number.text=   contactsToSend?.get(position)?.contactName.toString()

        holder.cardDeSelector.setOnClickListener {
            removeTopAdapter?.onRemoveAdapter(contactsToSend?.get(position)!!)
            contactsToSend?.clear()
            notifyDataSetChanged()
        }
        holder.deSelector.setOnClickListener {
            removeTopAdapter?.onRemoveAdapter(contactsToSend?.get(position)!!)
            contactsToSend?.clear()
            notifyDataSetChanged()
        }
    }

    fun getItem() : UIBContactObject {
        return contactsToSend?.get(0)!!
    }
}