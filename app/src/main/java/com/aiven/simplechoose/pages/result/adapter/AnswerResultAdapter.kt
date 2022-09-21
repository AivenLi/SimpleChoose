package com.aiven.simplechoose.pages.result.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aiven.simplechoose.R
import com.aiven.simplechoose.adapter.TestPaperCheckAdapter
import com.aiven.simplechoose.databinding.ItemChooseAnswerBinding
import com.aiven.simplechoose.databinding.ItemTestPaperCheckBinding
import com.aiven.simplechoose.databinding.ItemTestPaperCheckSmallBinding
import com.aiven.simplechoose.pages.result.bean.enums.AnswerResult
import com.aiven.simplechoose.utils.ThemeUtils

class AnswerResultAdapter(
    private val context: Context,
    private val data: ArrayList<AnswerResult>
) : RecyclerView.Adapter<AnswerResultAdapter.ViewHolder>() {

    private val isDark = ThemeUtils.isDarkMode(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTestPaperCheckSmallBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvIndex.text = (position + 1).toString()
        holder.binding.tvIndex.setBackgroundColor(
            when (data[position]) {
                AnswerResult.RIGHT -> {
                    if (isDark) {
                        getColor(R.color.night_checked)
                    } else {
                        getColor(R.color.light_checked)
                    }
                }
                AnswerResult.LEFT -> {
                    if (isDark) {
                        getColor(R.color.night_un_check)
                    } else {
                        getColor(R.color.light_un_check)
                    }
                }
                else -> {
                    if (isDark) {
                        getColor(R.color.night_item_desc)
                    } else {
                        getColor(R.color.light_item_desc)
                    }
                }
            }
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun getColor(@ColorRes id: Int) : Int {
        return ContextCompat.getColor(context, id)
    }

    class ViewHolder(
        val binding: ItemTestPaperCheckSmallBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    )
}