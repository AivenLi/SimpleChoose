package com.aiven.simplechoose.pages.setting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aiven.simplechoose.adapter.OnSingleClickListener
import com.aiven.simplechoose.databinding.ItemSettingClickBinding
import com.aiven.simplechoose.databinding.ItemSettingSwitchBinding
import com.aiven.simplechoose.bean.dto.SettingBean
import com.aiven.simplechoose.bean.dto.SettingType
import com.aiven.simplechoose.utils.setSingleClickListener

class SettingAdapter(
    private val context: Context,
    private val data: ArrayList<SettingBean>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onSingleClickListener: OnSingleClickListener? = null
    fun setOnSingleClickListener(listener: OnSingleClickListener?) {
        onSingleClickListener = listener
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].type.value
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SettingType.CLICK.value -> {
                ClickViewHolder(
                    ItemSettingClickBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                SwitchViewHolder(
                    ItemSettingSwitchBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (data[position].type) {
            SettingType.CLICK -> {
                clickBind((holder as ClickViewHolder).viewBinding, position)
            }
            SettingType.SWITCH -> {
                switchBind((holder as SwitchViewHolder).viewBinding, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun clickBind(viewBinding: ItemSettingClickBinding, position: Int) {
        viewBinding.tvTitle.text = data[position].title
        viewBinding.tvDesc.text = data[position].desc
        viewBinding.root.setSingleClickListener {
            onSingleClickListener?.onSingleClickListener(position)
        }
    }

    private fun switchBind(viewBinding: ItemSettingSwitchBinding, position: Int) {
        viewBinding.tvTitle.text = data[position].title
        viewBinding.tvDesc.text = data[position].desc
        viewBinding.switchCompat.isChecked = data[position].switch
        viewBinding.switchCompat.setOnCheckedChangeListener { _, isChecked ->
            if (data[position].switch != isChecked) {
                data[position].switch = isChecked
                notifyItemChanged(position)
            }
        }
    }

    class ClickViewHolder(
        val viewBinding: ItemSettingClickBinding
    ) : RecyclerView.ViewHolder(
        viewBinding.root
    )

    class SwitchViewHolder(
        val viewBinding: ItemSettingSwitchBinding
    ) : RecyclerView.ViewHolder(
        viewBinding.root
    )
}