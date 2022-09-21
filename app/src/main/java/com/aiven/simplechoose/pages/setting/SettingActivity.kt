package com.aiven.simplechoose.pages.setting

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.aiven.simplechoose.R
import com.aiven.simplechoose.adapter.OnSingleClickListener
import com.aiven.simplechoose.databinding.ActivitySettingBinding
import com.aiven.simplechoose.pages.BaseActivity
import com.aiven.simplechoose.pages.setting.adapter.SettingAdapter
import com.aiven.simplechoose.pages.setting.bean.SettingBean
import com.aiven.simplechoose.pages.setting.bean.SettingType
import com.aiven.simplechoose.utils.Constant
import com.aiven.simplechoose.utils.setSingleClickListener
import com.tencent.mmkv.MMKV

class SettingActivity : BaseActivity<ActivitySettingBinding>(ActivitySettingBinding::inflate) {

    private val data = ArrayList<SettingBean>()
    private lateinit var mmkv: MMKV

    companion object {
        fun start(context: Context) {
            context.startActivity(
                Intent(
                    context,
                    SettingActivity::class.java
                )
            )
        }
    }

    override fun initView() {
        mmkv = MMKV.mmkvWithID(Constant.MMKV_FILE)
        val times = mmkv.decodeLong(Constant.MMKV_TEST_PAPER_TIME_KEY, 30L * 60L)
        data.add(
            SettingBean(
                title = getString(R.string.test_paper_times),
                desc  = secondToMinuteString(times),
                type  = SettingType.CLICK
            )
        )
        viewBinding.recyclerView.adapter = SettingAdapter(this, data).apply {
            setOnSingleClickListener(object : OnSingleClickListener {
                override fun onSingleClickListener(position: Int) {

                }
            })
        }
        viewBinding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun initClick() {
        viewBinding.imgBack.setSingleClickListener {
            finish()
        }
    }

    override fun getDebugTAG(): String {
        return SettingActivity::class.java.simpleName
    }

    private fun secondToMinuteString(seconds: Long) : String {
        return getString(R.string.arg_minute, seconds / 60L)
    }
}