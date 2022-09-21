package com.aiven.simplechoose.pages.setting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aiven.simplechoose.adapter.OnSingleClickListener
import com.aiven.simplechoose.databinding.ItemSettingBinding
import com.aiven.simplechoose.pages.setting.bean.SettingBean
import com.aiven.simplechoose.utils.setSingleClickListener

class SettingAdapter(
    private val context: Context,
    private val data: ArrayList<SettingBean>
) : RecyclerView.Adapter<SettingAdapter.ViewHolder>() {

    private var onSingleClickListener: OnSingleClickListener? = null
    fun setOnSingleClickListener(listener: OnSingleClickListener?) {
        onSingleClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSettingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.viewBinding.tvTitle.text = data[position].title
        holder.viewBinding.tvDesc.text = data[position].desc
        holder.viewBinding.root.setSingleClickListener {
            onSingleClickListener?.onSingleClickListener(position)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }


    class ViewHolder(
        val viewBinding: ItemSettingBinding
    ) : RecyclerView.ViewHolder(
        viewBinding.root
    )
}