package com.aiven.simplechoose.view

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.aiven.simplechoose.R
import com.aiven.simplechoose.adapter.ChooseAnswerAdapter
import com.aiven.simplechoose.bean.dto.AnswerDTO
import com.aiven.simplechoose.bean.dto.QuestionDTO
import com.aiven.simplechoose.databinding.ItemChooseBinding
import com.aiven.simplechoose.utils.ThemeUtils

/**
 * @author AivenLi
 * @since 2022-09-05 15:02
 *
 * 选择题view，适用于单选、多选。
 * 其中题目支持插图，在需要插图的位置加上“[image]”即可，例如：
 *      请看下图[image]，请根据图片选择答案
 * */
class SimpleChooseView : FrameLayout {
    /**
     * 答案列表适配器
     * */
    private val chooseAnswerAdapter: ChooseAnswerAdapter
    /**
     * 答案列表数据
     * */
    private val chooseList = ArrayList<AnswerDTO>()

    private val isDark: Boolean

    private val binding: ItemChooseBinding =
        ItemChooseBinding.inflate(LayoutInflater.from(context), this, true)

    companion object {
        /**
         * 当题目中包含图片时，在图片的位置加上该标志。
         * */
        const val IMAGE_FLAG = "[image]"
        const val SINGLE_MODE_STR = "【单选题】"
        const val MULTI_MODE_STR  = "【多选题】"
        const val TYPE_SINGLE = 0
        const val TYPE_MULTI = 1
        const val MODE_TEST = 0
        const val MODE_PARSE = 1
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        isDark = ThemeUtils.isDarkMode(context)
        chooseAnswerAdapter = ChooseAnswerAdapter(
            context,
            chooseList,
            false
        )
        binding.recyclerView.adapter = chooseAnswerAdapter
        binding.recyclerView.isNestedScrollingEnabled = false
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
    }

    fun initData(
        questionDTO: QuestionDTO,
        position: Int,
        mode: Int
    ) {
        parseTitle(
            context.getString(
                R.string.arg_3_string_number_string,
                if (questionDTO.mode == TYPE_SINGLE) {
                    SINGLE_MODE_STR
                } else {
                    MULTI_MODE_STR
                },
                position + 1,
                questionDTO.title
            ),
            questionDTO.mode
        )
        if (questionDTO.imageUrl.isNullOrEmpty()) {
            binding.imageView.visibility = View.GONE
        } else {
            Glide.with(binding.imageView)
                .load(questionDTO.imageUrl)
                .placeholder(R.drawable.ic_error)
                .into(binding.imageView)
            binding.imageView.visibility = View.VISIBLE
        }
        chooseList.clear()
        chooseList.addAll(questionDTO.chooseList)
        chooseAnswerAdapter.multiSelect = questionDTO.mode != 0
        chooseAnswerAdapter.setAnswerEnable(mode)
        if (mode == MODE_PARSE) {
            binding.tvParse.visibility = View.VISIBLE
            binding.tvParse.text = questionDTO.parse ?: context.getString(R.string.no_parse)
        } else {
            binding.tvParse.visibility = View.GONE
        }
    }

    private fun parseTitle(title: String, mode: Int) {
        if (title.isEmpty()) {
            return
        }
        if (title.contains(IMAGE_FLAG)) {
            val index = title.indexOf(IMAGE_FLAG)
            if (index == 0) {
                binding.tvFirstTitle.visibility = View.GONE
            } else {
                binding.tvFirstTitle.visibility = View.VISIBLE
                binding.tvFirstTitle.text = getFirstTitle(title.substring(0, index), mode)
            }
            if (title.length > index + IMAGE_FLAG.length) {
                binding.tvSecondTitle.text = title.substring(index + IMAGE_FLAG.length, title.length)
                binding.tvSecondTitle.visibility = View.VISIBLE
            } else {
                binding.tvSecondTitle.visibility = View.GONE
            }
        } else {
            binding.tvFirstTitle.visibility = View.VISIBLE
            binding.tvFirstTitle.text = getFirstTitle(title, mode)
            binding.tvSecondTitle.visibility = View.GONE
        }
    }

    private fun getFirstTitle(title: String, mode: Int) : SpannableString {
        return SpannableString(title).apply {
            setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        context,
                        if (mode == TYPE_SINGLE) {
                            if (isDark) {
                                R.color.night_main
                            } else {
                                R.color.light_main
                            }
                        } else {
                            if (isDark) {
                                R.color.night_warning
                            } else {
                                R.color.light_warning
                            }
                        }
                    )
                ),
                0,
                if (mode == TYPE_SINGLE) {
                    SINGLE_MODE_STR.length
                } else {
                    MULTI_MODE_STR.length
                },
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}