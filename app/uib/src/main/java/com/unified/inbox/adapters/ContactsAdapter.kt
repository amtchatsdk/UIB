package com.unified.inbox.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.unified.inbox.R
import com.unified.inbox.beans.UIBContactObject
import com.unified.inbox.interfaces.AddTopAdapterView
import com.unified.inbox.interfaces.RemoveTopAdapter
import kotlinx.android.synthetic.main.contact_item.view.*
import java.util.*
import kotlin.collections.ArrayList

class ContactsAdapter(context: Context, contactSelectedRecycler:RecyclerView, removeTopAdapter: RemoveTopAdapter, addTopAdapterView: AddTopAdapterView) :
    RecyclerView.Adapter<ContactsAdapter.ViewHolder>(),
    RemoveTopAdapter {
    private var addTopAdapterView: AddTopAdapterView?=null

    private var recyclerViewTop:RecyclerView?=null
    private var removeTopAdapter: RemoveTopAdapter?=null
    private var selectedPos: Int=-1
    private var myContext: Context? = null
    private var myContacts: ArrayList<UIBContactObject>? = null

    init {
        this.removeTopAdapter=removeTopAdapter
        this.addTopAdapterView=addTopAdapterView
        recyclerViewTop=contactSelectedRecycler
        myContext = context
        myContacts = ArrayList()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val initial: ImageView = itemView.vT_ContactInitial
        val contactCard: CardView = itemView.vC_Contact_Card
        val name: TextView = itemView.vT_ContactName
        val number: TextView = itemView.vT_ContactNumber
        val selector: ImageView = itemView.vI_Selector

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val mainView =
            LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return ViewHolder(mainView)


    }

    fun addContacts(list: ArrayList<UIBContactObject>) {
        myContacts?.clear()
        list.sortBy { it.contactName }
        myContacts?.addAll(list)

    }

    override fun getItemCount(): Int {
        return myContacts?.size!!
    }

    @ExperimentalStdlibApi
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (selectedPos==position){
            holder.selector.visibility=View.VISIBLE

        }else{
            holder.selector.visibility=View.GONE
        }


        holder.name.text = myContacts?.get(position)?.contactName?.capitalize(Locale.ROOT)
        holder.number.text = myContacts?.get(position)?.phoneNumber
        holder.contactCard.setOnClickListener {
            selectedPos=position
            addTopAdapterView?.makeItVisible(myContacts?.get(position)!!)
            notifyDataSetChanged()
        }

    }

    override fun onRemoveAdapter(uibContactObject: UIBContactObject) {


        this.removeTopAdapter?.onRemoveAdapter(uibContactObject)



    }

    fun removeSelectedItem(uibContactObject: UIBContactObject) {
        selectedPos=-1
        notifyItemRemoved(selectedPos)
        notifyDataSetChanged()
    }


}