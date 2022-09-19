package com.aiven.simplechoose.pages.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.aiven.simplechoose.R
import com.aiven.simplechoose.bean.dto.TestPaperTypeDTO
import com.aiven.simplechoose.databinding.ItemTestPaperHomeBinding
import com.aiven.simplechoose.pages.testPaper.TestPaperListActivity

class TestPaperTypeAdapter(
    private val context: Context,
    private val data: ArrayList<TestPaperTypeDTO>
) : RecyclerView.Adapter<TestPaperTypeAdapter.Companion.ViewHolder>() {

    private val density = context.resources.displayMetrics.density

    companion object {

        class ViewHolder(
            val viewBinding: ItemTestPaperHomeBinding
        ) : RecyclerView.ViewHolder(
            viewBinding.root
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTestPaperHomeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bind(holder.viewBinding, data[position], position)
    }

    private fun bind(viewBinding: ItemTestPaperHomeBinding, item: TestPaperTypeDTO, position: Int) {
        Glide.with(viewBinding.imgCover)
            .load(item.iconUrl)
            .placeholder(R.drawable.ic_error)
            .into(viewBinding.imgCover)
        viewBinding.tvTitle.text = item.title
        viewBinding.tvStatus.visibility =
            if (item.status == 0) {
                View.GONE
            } else {
                View.VISIBLE
            }
        val lp = viewBinding.root.layoutParams as ViewGroup.MarginLayoutParams
        if (position % 2 == 0) {
            lp.setMargins(0, dp2px(10f), dp2px(5f), 0)
        } else {
            lp.setMargins(dp2px(5f), dp2px(10f), 0, 0)
        }
        viewBinding.root.layoutParams = lp
        viewBinding.root.setOnClickListener {
            if (item.status == 0) {
                TestPaperListActivity.start(context, item.title, item.url)
            } else {
                Toast.makeText(context, "非常抱歉，该类型暂未开放", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun dp2px(dp: Float) : Int {
        return (density * dp).toInt()
    }
}