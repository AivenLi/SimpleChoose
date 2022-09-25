package com.aiven.simplechoose.pages.setting

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.aiven.simplechoose.R
import com.aiven.simplechoose.adapter.OnSingleClickListener
import com.aiven.simplechoose.databinding.ActivitySettingBinding
import com.aiven.simplechoose.pages.BaseActivity
import com.aiven.simplechoose.pages.setting.adapter.SettingAdapter
import com.aiven.simplechoose.bean.dto.SettingBean
import com.aiven.simplechoose.bean.dto.SettingType
import com.aiven.simplechoose.utils.Constant
import com.aiven.simplechoose.utils.setSingleClickListener
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.tencent.mmkv.MMKV

class SettingActivity : BaseActivity<ActivitySettingBinding>(ActivitySettingBinding::inflate) {

    private val data = ArrayList<SettingBean>()
    private lateinit var mmkv: MMKV
    private var testPaperTime = 30L * 60L
    private val settingAdapter by lazy {
        SettingAdapter(
            this,
            data
        )
    }

    private val pickerView: OptionsPickerView<Int> by lazy {
        val pickerview: OptionsPickerView<Int> = OptionsPickerBuilder(
            this
        ) { options1, _, _, _ ->
            testPaperTime = (options1 + 1) * 5L * 60L
            data[0].desc = secondToMinuteString(testPaperTime)
            settingAdapter.notifyItemChanged(0)
        }
            .setTextColorCenter(ContextCompat.getColor(this, R.color.main))
            .setContentTextSize(16)
            .setTitleColor(ContextCompat.getColor(this, R.color.deep_title))
            .setTitleSize(16)
            .setTitleText(getString(R.string.choose_time))
            .setCancelColor(ContextCompat.getColor(this, R.color.item_desc))
            .setCancelText(getString(R.string.cancel))
            .setSubmitColor(ContextCompat.getColor(this, R.color.main))
            .setSubmitText(getString(R.string.yes))
            .build()
        pickerview.setNPicker(
            getTestPaperTimeList(),
            null,
            null
        )
        pickerview
    }

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
        initSettingList()
        viewBinding.recyclerView.adapter = settingAdapter
        settingAdapter.setOnSingleClickListener(object : OnSingleClickListener {
            override fun onSingleClickListener(position: Int) {
                when (position) {
                    0 -> {
                        pickerView.show()
                    }
                }
            }
        })
        viewBinding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun initClick() {
        viewBinding.imgBack.setSingleClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "保存时间：$testPaperTime")
        mmkv.encode(Constant.MMKV_TEST_PAPER_TIME_KEY, testPaperTime)
        mmkv.encode(Constant.MMKV_CLCIK_GOTO_NEXT_QUESTION_KEY, data[1].switch)
        super.onDestroy()
    }

    override fun getDebugTAG(): String {
        return SettingActivity::class.java.simpleName
    }

    private fun secondToMinuteString(seconds: Long) : String {
        return getString(R.string.arg_minute, seconds / 60L)
    }

    private fun initSettingList() {
        mmkv = MMKV.mmkvWithID(Constant.MMKV_FILE)
        testPaperTime = mmkv.decodeLong(Constant.MMKV_TEST_PAPER_TIME_KEY, 30L * 60L)
        data.add(
            SettingBean(
                title = getString(R.string.test_paper_times),
                desc  = secondToMinuteString(testPaperTime),
                type  = SettingType.CLICK
            )
        )
        data.add(
            SettingBean(
                title = getString(R.string.click_item_choose_goto_next),
                desc  = getString(R.string.multi_choose_no_goto_next),
                type  = SettingType.SWITCH,
                switch = mmkv.decodeBool(Constant.MMKV_CLCIK_GOTO_NEXT_QUESTION_KEY, true)
            )
        )
    }

    private fun getTestPaperTimeList() : List<Int> {
        return listOf(
            5,
            10,
            15,
            20,
            25,
            30,
            35,
            40
        )
    }
}