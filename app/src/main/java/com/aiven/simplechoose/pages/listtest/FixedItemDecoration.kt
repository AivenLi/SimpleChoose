package com.aiven.simplechoose.pages.listtest

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * <p>
 *
 * </p>
 * @author  : AivenLi
 * @date    : 2023/4/5 12:56
 * */
class FixedItemDecoration: RecyclerView.ItemDecoration() {

    companion object {
        private const val TAG = "FixedItemDecoration-Debug"
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        parent.adapter?.let {
            val itemViewType = it.getItemViewType(parent.getChildAdapterPosition(view))
            if (itemViewType == MultiAdapter.TYPE_V1) {
                Log.d(TAG, "itemY: ${view.y}")
            }
        }
        super.getItemOffsets(outRect, view, parent, state)
    }
}