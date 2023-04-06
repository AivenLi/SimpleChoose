package com.aiven.simplechoose.pages.listtest

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aiven.simplechoose.databinding.ItemLv1Binding
import com.aiven.simplechoose.databinding.ItemLv2Binding
import com.aiven.simplechoose.databinding.ItemLv3Binding
import com.aiven.simplechoose.utils.setSingleClickListener

class MultiAdapter(
    private val context: Context,
    private val data: ArrayList<MultiBean>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_V1 = 0
        const val TYPE_V2 = 1
        const val TYPE_V3 = 2
        private const val TAG = "MultiAdapter-Debug"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return when (viewType) {
           TYPE_V1 -> {
               Lv1ViewHolder(
                   ItemLv1Binding.inflate(
                       LayoutInflater.from(parent.context),
                       parent,
                       false
                   )
               )
           }
           TYPE_V2 -> {
               Lv2ViewHolder(
                   ItemLv2Binding.inflate(
                       LayoutInflater.from(parent.context),
                       parent,
                       false
                   )
               )
           }
           else -> {
               Lv3ViewHolder(
                   ItemLv3Binding.inflate(
                       LayoutInflater.from(parent.context),
                       parent,
                       false
                   )
               )
           }
       }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_V1 -> {
                bindLv1((holder as Lv1ViewHolder).viewBinding, data[position], position)
            }
            TYPE_V2 -> {
                bindLv2((holder as Lv2ViewHolder).viewBinding, data[position], position)
            }
            else -> {
                bindLv3((holder as Lv3ViewHolder).viewBinding, data[position], position)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (data[position].isFirst) {
            TYPE_V1
        } else if (data[position].isSecond) {
            TYPE_V2
        } else {
            TYPE_V3
        }
    }

    fun closeFirstItem(position: Int) {
        val item = data[position]
        item.isOpen = false
        if (!item.childList.isNullOrEmpty()) {
            val pos = position + 1
            val beforeSize = data.size
            var count = 0
            while (pos < data.size) {
                if (data[pos].isFirst) {
                    break
                }
                data.removeAt(pos)
                count++
            }
            notifyItemRangeRemoved(pos, count)
            notifyItemRangeChanged(pos, beforeSize - count - pos)
        }
    }

    fun openFirstItem(position: Int) {
        val pos = position + 1
        val item = data[pos]
        if (!item.childList.isNullOrEmpty()) {
            data.addAll(pos, item.childList!!)
            notifyItemRangeInserted(pos, item.childList!!.size)
            notifyItemRangeChanged(pos, data.size - pos)
        }
    }

    private fun bindLv1(viewBinding: ItemLv1Binding, item: MultiBean, position: Int) {
        viewBinding.tvTitle.text = item.title
        viewBinding.root.setSingleClickListener {
            item.isOpen = !item.isOpen
            if (!item.childList.isNullOrEmpty()) {
                //val pos = position + 1
                if (item.isOpen) {
                    openFirstItem(position)
                } else {
                    closeFirstItem(position)
//                    val beforeSize = data.size
//                    var count = 0
//                    while (pos < data.size) {
//                        if (data[pos].isFirst) {
//                            break
//                        }
//                        data.removeAt(pos)
//                        count++
//                    }
//                    notifyItemRangeRemoved(pos, count)
//                    notifyItemRangeChanged(pos, beforeSize - count - pos)
                }
            }
        }
    }

    private fun bindLv2(viewBinding: ItemLv2Binding, item: MultiBean, position: Int) {
        viewBinding.tvTitle.text = item.title
        viewBinding.root.setSingleClickListener {
            item.isOpen = !item.isOpen
            if (!item.childList.isNullOrEmpty()) {
                val pos = position + 1
                if (item.isOpen) {
                    data.addAll(pos, item.childList!!)
                    notifyItemRangeInserted(pos, item.childList!!.size)
                    notifyItemRangeChanged(pos, data.size - pos)
                } else {
                    val beforeSize = data.size
                    var count = 0
                    while (pos < data.size) {
                        if (!data[pos].isFirst && !data[pos].isSecond) {
                            count++
                            data.removeAt(pos)
                        } else {
                            break
                        }
                    }
                    notifyItemRangeRemoved(pos, count)
                    notifyItemRangeChanged(pos, beforeSize - count - pos)
                }
            }
        }
    }

    private fun bindLv3(viewBinding: ItemLv3Binding, item: MultiBean, position: Int) {
        viewBinding.tvTitle.text = item.title
    }

    private fun getFirstItemPos(position: Int): Int {
        var count = 0
        for (i in 0 until data.size) {
            count++
            if (data[i].isOpen && !data[i].childList.isNullOrEmpty()) {
                for (secondItem in data[i].childList!!) {
                    count++
                    if (secondItem.isOpen && !secondItem.childList.isNullOrEmpty()) {
                        count += secondItem.childList!!.size
                    }
                }
            }
            if (position < count) {
                return i
            }
        }
        return count
    }

    private fun getSecondItemPos(position: Int): Int {
        var count = 0
        for (firstItem in data) {
            count++
            if (firstItem.isOpen && !firstItem.childList.isNullOrEmpty()) {
                for (secondItem in firstItem.childList!!) {
                    count++
                    if (secondItem.isOpen && !secondItem.childList.isNullOrEmpty()) {
                        count += secondItem.childList!!.size
                    }
                    if (position < count) {
                        return (count - position)
                    }
                }
            }
        }
        return count
    }

    private fun getThirdItemPos(position: Int): Int {
        var count = 0
        for (firstItem in data) {
            count++
            if (firstItem.isOpen && !firstItem.childList.isNullOrEmpty()) {
                for (secondItem in firstItem.childList!!) {
                    count++
                    if (secondItem.isOpen && !secondItem.childList.isNullOrEmpty()) {
                        count += secondItem.childList!!.size
                        if (position < count) {
                            return secondItem.childList!!.size - (count - position)
                        }
                    }
                }
            }
        }
        return count
    }

    private fun isFirstItemPos(position: Int): Boolean {
        var count = 0
        for (i in 0 until data.size) {
            if (count == position) {
                return true
            }
            count++
            if (data[i].isOpen) {
                if (!data[i].childList.isNullOrEmpty()) {
                    count += data[i].childList!!.size
                }
            }
        }
        return false
    }

    private fun isSecondItemPos(position: Int): Boolean {
        if (position == 0) {
            return false
        }
        var count = 1
        for (firstItem in data) {
            count++
            val secondList = firstItem.childList
            if (firstItem.isOpen && !secondList.isNullOrEmpty()) {
                if (count == position) {
                    return true
                }
                for (secondItem in secondList) {
                    if (secondItem.isOpen && !secondItem.childList.isNullOrEmpty()) {
                        count += secondItem.childList!!.size
                    }
                    count++
                }
            }
        }
        return false
    }

    class Lv1ViewHolder(
        val viewBinding: ItemLv1Binding
    ): RecyclerView.ViewHolder(
        viewBinding.root
    )

    class Lv2ViewHolder(
        val viewBinding: ItemLv2Binding
    ): RecyclerView.ViewHolder(
        viewBinding.root
    )

    class Lv3ViewHolder(
        val viewBinding: ItemLv3Binding
    ): RecyclerView.ViewHolder(
        viewBinding.root
    )
}