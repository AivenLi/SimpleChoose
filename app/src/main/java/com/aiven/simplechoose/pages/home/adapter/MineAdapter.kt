package com.aiven.simplechoose.pages.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aiven.simplechoose.adapter.OnSingleClickListener
import com.aiven.simplechoose.bean.dto.SettingBean
import com.aiven.simplechoose.databinding.ItemMineBinding
import com.aiven.simplechoose.utils.setSingleClickListener

class MineAdapter(
    private val data: ArrayList<SettingBean>
) : RecyclerView.Adapter<MineAdapter.ViewHolder>() {

    private var onSingleClickListener: ((item: SettingBean) -> Unit)? = null
    fun setOnSingleClickListener(listener: ((item: SettingBean) -> Unit)?) {
        onSingleClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMineBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.viewBinding) {
            data[position].let { item ->
                tvTitle.text = item.title
                tvDesc.text = item.desc
                imgLeftIcon.setImageResource(item.icon)
            }
            root.setSingleClickListener {
                onSingleClickListener?.invoke(data[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(
        val viewBinding: ItemMineBinding
    ) : RecyclerView.ViewHolder(
        viewBinding.root
    )
}