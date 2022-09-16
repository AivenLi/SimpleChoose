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
import com.example.simplechoose.view.SimpleChooseView

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
    var multiSelect: Boolean = false
) : RecyclerView.Adapter<ChooseAnswerAdapter.ViewHolder>()
{
    private val isDark = ThemeUtils.isDarkMode(context)
    private var preSelectedPos = -1
    private var mode = SimpleChooseView.MODE_TEST

    companion object {
        val indexMap = mapOf<Int, String>(
            Pair(0, "A"),
            Pair(1, "B"),
            Pair(2, "C"),
            Pair(3, "D"),
            Pair(4, "E"),
            Pair(5, "F"),
            Pair(6, "G"),
            Pair(7, "H"),
            Pair(8, "I"),
            Pair(9, "J"),
            Pair(10, "K"),
            Pair(11, "L"),
            Pair(12, "M"),
            Pair(13, "N"),
            Pair(14, "O"),
            Pair(15, "P"),
            Pair(16, "Q"),
            Pair(17, "R"),
            Pair(18, "S"),
            Pair(19, "T"),
            Pair(20, "U"),
            Pair(21, "V"),
            Pair(22, "W"),
            Pair(23, "X"),
            Pair(24, "Y"),
            Pair(25, "Z")
        )
    }

    fun setAnswerEnable(mode: Int) {
        this.mode = mode
        notifyDataSetChanged()
    }

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
        binding.tvTitle.text = context.getString(R.string.arg_2_string_string, indexMap[position], item.title)
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
            if (mode == SimpleChooseView.MODE_PARSE) {
                return@setOnClickListener
            }
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