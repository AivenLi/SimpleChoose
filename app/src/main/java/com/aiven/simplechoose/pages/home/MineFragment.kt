package com.aiven.simplechoose.pages.home

import androidx.recyclerview.widget.LinearLayoutManager
import com.aiven.simplechoose.R
import com.aiven.simplechoose.adapter.OnSingleClickListener
import com.aiven.simplechoose.bean.dto.SettingBean
import com.aiven.simplechoose.bean.dto.SettingType
import com.aiven.simplechoose.bean.enums.MineAction
import com.aiven.simplechoose.databinding.FragmentMineBinding
import com.aiven.simplechoose.pages.BaseFragment
import com.aiven.simplechoose.pages.home.adapter.MineAdapter
import com.aiven.simplechoose.pages.qrcode.QRCodeActivity
import com.aiven.simplechoose.pages.record.RecordActivity
import com.aiven.simplechoose.pages.setting.SettingActivity

class MineFragment : BaseFragment<FragmentMineBinding>(FragmentMineBinding::inflate) {


    override fun initView() {
        val data = arrayListOf<SettingBean>(
            SettingBean(
                title  = getString(R.string.test_paper_record),
                desc   = null,
                type   = SettingType.CLICK,
                icon   = R.drawable.ic_record,
                action = MineAction.TEST_RECORD
            ),
            SettingBean(
                title  = getString(R.string.setting),
                desc   = null,
                type   = SettingType.CLICK,
                icon   = R.drawable.ic_setting,
                action = MineAction.SETTING
            ),
            SettingBean(
                title  = getString(R.string.qr_create),
                desc   = null,
                type   = SettingType.CLICK,
                icon   = R.drawable.ic_baseline_qr_code_24,
                action = MineAction.QR_CREATE
            )
        )
        viewBinding.recyclerView.adapter = MineAdapter(data).apply {
            setOnSingleClickListener {
                when (it.action) {
                    MineAction.QR_CREATE -> {
                        QRCodeActivity.start(requireContext())
                    }
                    MineAction.SETTING -> {
                        SettingActivity.start(requireContext())
                    }
                    MineAction.TEST_RECORD -> {
                        RecordActivity.start(requireContext())
                    }
                }
            }
        }
        viewBinding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun initClick() {

    }

    override fun getFTAG(): String {
        return MineFragment::class.java.simpleName
    }
}