package com.example.simplechoose.pages.result

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.simplechoose.databinding.ActivityResultBinding
import com.example.simplechoose.pages.BaseActivity
import com.example.simplechoose.pages.result.bean.ResultBean
import com.example.simplechoose.utils.setSingleClickListener

class ResultActivity : BaseActivity<ActivityResultBinding>(ActivityResultBinding::inflate) {

    companion object {

        fun start(context: Context, resultBean: ResultBean) {
            Intent(context, ResultActivity::class.java).run {
                putExtra("result_bean", resultBean)
                context.startActivity(this)
            }
        }
    }

    override fun initView() {
        Log.d(TAG, "考试结果：${intent.getParcelableExtra<ResultBean>("result_bean")}")
    }

    override fun initClick() {
        viewBinding.imgBack.setSingleClickListener {
            finish()
        }
    }

    override fun getDebugTAG(): String {
        return ResultActivity::class.java.simpleName
    }
}