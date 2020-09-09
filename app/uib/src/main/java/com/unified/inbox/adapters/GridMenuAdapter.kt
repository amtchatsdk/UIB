package com.unified.inbox.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.unified.inbox.R
import com.unified.inbox.beans.UIBAttachmentMenu
import kotlinx.android.synthetic.main.item_menu.view.*

class GridMenuAdapter(private var menuList: ArrayList<UIBAttachmentMenu>) :
    RecyclerView.Adapter<GridMenuAdapter.MenuViewHolder>() {

    var listener: GridMenuListener? = null
    var fileSelectListener: FileSelectListener? = null

    /*private val menus = arrayListOf(
        Menu(
            "Document",
            R.drawable.ic_document
        ),
        Menu(
            "Camera",
            R.drawable.ic_camera
        ),
        Menu(
            "Gallery",
            R.drawable.ic_gallery
        ),
        Menu(
            "Audio",
            R.drawable.ic_volume
        ),
        Menu(
            "Location",
            R.drawable.ic_location
        ),
        Menu(
            "Contact",
            R.drawable.ic_contact
        )
    )*/

    interface GridMenuListener {
        fun dismissPopup()
    }


    interface FileSelectListener {
        fun imageSelect(type: String)
    }

    private val data = ArrayList<UIBAttachmentMenu>().apply {
        addAll(menuList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        return MenuViewHolder.create(
            parent,
            viewType
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(data[position], listener, fileSelectListener!!)
    }

    class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
            menu: UIBAttachmentMenu,
            listener: GridMenuListener?, fileSelectListener: FileSelectListener
        ) {
            with(itemView) {
                tvTitle.text = menu.name
                ivIcon.setImageDrawable(ContextCompat.getDrawable(context, menu.drawable))
                itemView.setOnClickListener {
                    fileSelectListener.imageSelect(menu.name)
                    listener?.dismissPopup()
                }
            }
        }

        companion object {
            val LAYOUT = R.layout.item_menu

            fun create(parent: ViewGroup, viewType: Int): MenuViewHolder {
                return MenuViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        LAYOUT,
                        parent,
                        false
                    )
                )
            }
        }
    }

    data class Menu(val name: String, @DrawableRes val drawable: Int)
}