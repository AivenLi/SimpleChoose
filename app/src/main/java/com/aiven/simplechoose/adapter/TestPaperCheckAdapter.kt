package com.aiven.simplechoose.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aiven.simplechoose.R
import com.aiven.simplechoose.databinding.ItemTestPaperCheckBinding
import com.aiven.simplechoose.utils.ThemeUtils
import com.aiven.simplechoose.utils.setSingleClickListener

/**
 * @author  : AivenLi
 * @date    : 2022/9/20 20:37
 * @desc    :
 * */
class TestPaperCheckAdapter(
    private val context: Context,
    private val data: HashMap<Int, Boolean>
) : RecyclerView.Adapter<TestPaperCheckAdapter.ViewHolder>() {

    private var onSingleClickListener: OnSingleClickListener? = null
    fun setOnSingleClickListener(listener: OnSingleClickListener?) {
        onSingleClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTestPaperCheckBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (data[position] == true) {
            holder.binding.tvIndex.setBackgroundColor(
                ContextCompat.getColor(context, R.color.checked)
            )
        } else {
            holder.binding.tvIndex.setBackgroundColor(
                ContextCompat.getColor(context, R.color.un_check)
            )
        }
        holder.binding.tvIndex.text = (position + 1).toString()
        holder.binding.root.setSingleClickListener {
            onSingleClickListener?.onSingleClickListener(position)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(
        val binding: ItemTestPaperCheckBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    )
}