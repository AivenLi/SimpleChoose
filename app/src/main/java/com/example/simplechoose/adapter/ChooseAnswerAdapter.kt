package com.example.simplechoose.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.simplechoose.R
import com.example.simplechoose.bean.dto.AnswerDTO
import com.example.simplechoose.databinding.ItemChooseAnswerBinding
import com.example.simplechoose.utils.ThemeUtils

/**
 * @author AivenLi
 * @since 2022-09-05 15:59
 *
 * 选择题答案列表适配器
 *
 * @param context
 * @param data
 * @param multiSelect 是否为多选模式，默认是单选
 * @param tSize 字体大小，默认12dp
 * @param tColor 字体颜色，默认0xff999999
 * @param unCheckedIcon 未选中图标，默认灰色圈圈，具体请看xml
 * @param checkedIcon 选中时的图标，默认蓝色圈圈，具体请看xml
 * */
class ChooseAnswerAdapter(
    private val context: Context,
    private val data: ArrayList<AnswerDTO>,
    var multiSelect: Boolean = false,
) : RecyclerView.Adapter<ChooseAnswerAdapter.ViewHolder>()
{
    private val isDark = ThemeUtils.isDarkMode(context)
    private var preSelectedPos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemChooseAnswerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bind(holder.binding, data[position], position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun bind(binding: ItemChooseAnswerBinding, item: AnswerDTO, position: Int) {
        if (position != 0) {
            binding.viewTopLine.visibility = View.GONE
        } else {
            binding.viewTopLine.visibility = View.VISIBLE
        }
        binding.tvTitle.text = item.title
        if (item.selected) {
            binding.imgCheckBox.setImageResource(R.drawable.ic_checked)
            binding.imgCheckBox.setColorFilter(
                ContextCompat.getColor(
                    context,
                    if (isDark) R.color.night_main else R.color.light_main
                )
            )
        } else {
            binding.imgCheckBox.setImageResource(R.drawable.ic_unchecked)
            binding.imgCheckBox.setColorFilter(
                ContextCompat.getColor(
                    context,
                    if (isDark) R.color.night_item_desc else R.color.light_item_desc
                )
            )
        }
        binding.root.setOnClickListener {
            if (multiSelect) {
                handleMultiSelect(item, position)
            } else {
                handleSingleSelect(item, position)
            }
        }
    }

    private fun handleMultiSelect(item: AnswerDTO, position: Int) {
        item.selected = !item.selected
        notifyItemChanged(position)
    }

    private fun handleSingleSelect(item: AnswerDTO, position: Int) {
        if (position == preSelectedPos) {
            return
        }
        item.selected = true
        notifyItemChanged(position)
        if (preSelectedPos != -1) {
            data[preSelectedPos].selected = false
            notifyItemChanged(preSelectedPos)
        }
        preSelectedPos = position
    }

    class ViewHolder(val binding: ItemChooseAnswerBinding) : RecyclerView.ViewHolder(binding.root)
}