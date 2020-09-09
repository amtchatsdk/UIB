package com.unified.inbox.views

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.unified.inbox.views.MenuGridDecoration


class MenuRecyclerView : RecyclerView {

    private val spanCount = 3

   /* private val manager = GridLayoutManager(context, spanCount)
    private val adapter = GridMenuAdapter()*/

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    init {
        setHasFixedSize(true)
        //layoutManager = manager
        //setAdapter(adapter)
        addItemDecoration(MenuGridDecoration())
    }

    /*fun addMenuClickListener(listener: GridMenuAdapter.GridMenuListener) {
        adapter.listener = listener
    }

    fun addFileSelectedListener(fileSelectedListener: GridMenuAdapter.FileSelectListener) {
        adapter.fileSelectListener = fileSelectedListener
    }*/
}