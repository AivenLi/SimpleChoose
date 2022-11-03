package com.aiven.simplechoose.pages.home

import androidx.recyclerview.widget.LinearLayoutManager
import com.aiven.simplechoose.R
import com.aiven.simplechoose.adapter.OnSingleClickListener
import com.aiven.simplechoose.databinding.FragmentMineBinding
import com.aiven.simplechoose.pages.BaseFragment
import com.aiven.simplechoose.pages.home.adapter.MineAdapter
import com.aiven.simplechoose.pages.record.RecordActivity
import com.aiven.simplechoose.pages.setting.SettingActivity
import com.aiven.simplechoose.pages.setting.adapter.SettingAdapter
import com.aiven.simplechoose.pages.setting.bean.SettingBean
import com.aiven.simplechoose.pages.setting.bean.SettingType

class MineFragment : BaseFragment<FragmentMineBinding>(FragmentMineBinding::inflate) {


    override fun initView() {
        val data = arrayListOf<SettingBean>(
            SettingBean(
                title = getString(R.string.test_paper_record),
                desc  = null,
                type  = SettingType.CLICK,
                icon  = R.drawable.ic_record
            ),
            SettingBean(
                title = getString(R.string.setting),
                desc  = null,
                type  = SettingType.CLICK,
                icon  = R.drawable.ic_setting
            )
        )
        viewBinding.recyclerView.adapter = MineAdapter(data).apply {
            setOnSingleClickListener(object : OnSingleClickListener {
                override fun onSingleClickListener(position: Int) {
                    when (position) {
                        0 -> {
                            RecordActivity.start(requireContext())
                        }
                        1 -> {
                            SettingActivity.start(requireContext())
                        }
                    }
                }
            })
        }
        viewBinding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun initClick() {

    }

    override fun getFTAG(): String {
        return MineFragment::class.java.simpleName
    }
}