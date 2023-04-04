package com.aiven.simplechoose.pages.home

import androidx.recyclerview.widget.LinearLayoutManager
import com.aiven.qcc.ScanActivity
import com.aiven.simplechoose.R
import com.aiven.simplechoose.adapter.OnSingleClickListener
import com.aiven.simplechoose.bean.dto.SettingBean
import com.aiven.simplechoose.bean.dto.SettingType
import com.aiven.simplechoose.bean.enums.MineAction
import com.aiven.simplechoose.databinding.FragmentMineBinding
import com.aiven.simplechoose.pages.BaseFragment
import com.aiven.simplechoose.pages.chart.ChartActivity
import com.aiven.simplechoose.pages.home.adapter.MineAdapter
import com.aiven.simplechoose.pages.imagecompress.ImageCompressActivity
import com.aiven.simplechoose.pages.landscaps.LandscapeAutoLayoutActivity
import com.aiven.simplechoose.pages.listtest.ListTestActivity
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
                title  = getString(R.string.scan_code),
                desc   = null,
                type   = SettingType.CLICK,
                icon   = R.drawable.ic_scan,
                action = MineAction.SCAN_CODE
            ),
            SettingBean(
                title  = getString(R.string.qr_create),
                desc   = null,
                type   = SettingType.CLICK,
                icon   = R.drawable.ic_baseline_qr_code_24,
                action = MineAction.QR_CREATE
            ),
            SettingBean(
                title  = getString(R.string.chart),
                desc   = null,
                type   = SettingType.CLICK,
                icon   = R.drawable.ic_baseline_insert_chart_outlined_24,
                action = MineAction.CHART_VIEW
            ),
            SettingBean(
                title  = getString(R.string.image_compress),
                desc   = null,
                type   = SettingType.CLICK,
                icon   = R.drawable.ic_image_compress,
                action = MineAction.IMAGE_COMPRESS
            ),
            SettingBean(
                title  = getString(R.string.auto_layout),
                desc   = null,
                type   = SettingType.CLICK,
                icon   = R.drawable.ic_baseline_insert_chart_outlined_24,
                action = MineAction.AUTO_LAYOUT
            ),
            SettingBean(
                title  = getString(R.string.list_test),
                desc   = null,
                type   = SettingType.CLICK,
                icon   = R.drawable.ic_baseline_insert_chart_outlined_24,
                action = MineAction.LIST_TEST
            ),
            SettingBean(
                title  = getString(R.string.setting),
                desc   = null,
                type   = SettingType.CLICK,
                icon   = R.drawable.ic_setting,
                action = MineAction.SETTING
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
                    MineAction.SCAN_CODE -> {
                        ScanActivity.start(requireActivity())
                    }
                    MineAction.CHART_VIEW -> {
                        ChartActivity.start(requireContext())
                    }
                    MineAction.IMAGE_COMPRESS -> {
                        ImageCompressActivity.start(requireContext())
                    }
                    MineAction.AUTO_LAYOUT -> {
                        LandscapeAutoLayoutActivity.start(requireContext())
                    }
                    MineAction.LIST_TEST -> {
                        ListTestActivity.start(requireContext())
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