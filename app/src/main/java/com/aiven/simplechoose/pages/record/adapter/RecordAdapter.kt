package com.aiven.simplechoose.pages.record.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aiven.simplechoose.R
import com.aiven.simplechoose.bean.dto.QuestionDTO
import com.aiven.simplechoose.databinding.ItemRecordBinding
import com.aiven.simplechoose.db.entity.TestPaperRecord
import com.aiven.simplechoose.pages.testPaperDetail.TestPaperDetailActivity
import com.aiven.simplechoose.utils.setSingleClickListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RecordAdapter(
    private val context: Context
) : RecyclerView.Adapter<RecordAdapter.ViewHolder>() {

    private val mData = arrayListOf<TestPaperRecord>()
    private val gson by lazy {
        Gson()
    }
    private val type by lazy {
        object : TypeToken<ArrayList<QuestionDTO>>(){}.type
    }

    fun updateData(list: List<TestPaperRecord>) {
        mData.clear()
        appendData(list)
    }

    fun appendData(list: List<TestPaperRecord>) {
        if (list.isNotEmpty()) {
            mData.addAll(list)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRecordBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        onBind(holder.viewBinding, mData[position], position)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    private fun onBind(viewBinding: ItemRecordBinding, item: TestPaperRecord, position: Int) {
        viewBinding.tvName.text = item.title
        viewBinding.tvScore.text = context.getString(R.string.number_2_bit, item.score)
        viewBinding.tvScore.setTextColor(getScoreColor(item.score))
        viewBinding.root.setSingleClickListener {
            TestPaperDetailActivity.start(
                context,
                gson.fromJson<ArrayList<QuestionDTO>>(item.jsonStr, type),
                item.title
            )
        }
    }

    @ColorInt
    private fun getScoreColor(score: Float): Int {
        return when {
            score >= 90.000f -> {
                ContextCompat.getColor(context, R.color.score_is_a)
            }
            score >= 80.000f -> {
                ContextCompat.getColor(context, R.color.score_is_b)
            }
            score >= 70.000f -> {
                ContextCompat.getColor(context, R.color.score_is_c)
            }
            score >= 60.000f -> {
                ContextCompat.getColor(context, R.color.score_is_d)
            }
            else -> {
                ContextCompat.getColor(context, R.color.score_is_other)
            }
        }
    }

    class ViewHolder(
        val viewBinding: ItemRecordBinding
    ) : RecyclerView.ViewHolder(
        viewBinding.root
    )
}