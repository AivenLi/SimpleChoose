package com.example.simplechoose.pages.simpleChoose.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplechoose.bean.dto.TestPaperDTO
import com.example.simplechoose.databinding.ItemTestPaperBinding

class TestPaperAdapter(
    private val data: ArrayList<TestPaperDTO>
) : RecyclerView.Adapter<TestPaperAdapter.Companion.ViewHolder>() {


    companion object {

        class ViewHolder(val viewBinding: ItemTestPaperBinding) : RecyclerView.ViewHolder(viewBinding.root)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTestPaperBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bind(holder.viewBinding, data[position], position)
    }

    private fun bind(viewBinding: ItemTestPaperBinding, item: TestPaperDTO, position: Int) {
        viewBinding.tvTitle.text = item.title
    }

    override fun getItemCount(): Int {
        return data.size
    }
}