package com.example.simplechoose.pages.result

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.simplechoose.R
import com.example.simplechoose.bean.dto.QuestionDTO
import com.example.simplechoose.databinding.ActivityResultBinding
import com.example.simplechoose.pages.BaseActivity
import com.example.simplechoose.pages.result.bean.ResultBean
import com.example.simplechoose.pages.testPaperDetail.TestPaperDetailActivity
import com.example.simplechoose.utils.TimeUtils
import com.example.simplechoose.utils.setSingleClickListener

class ResultActivity : BaseActivity<ActivityResultBinding>(ActivityResultBinding::inflate) {

    private var questionDTOList: ArrayList<QuestionDTO>? = null

    companion object {

        fun start(
            context: Context,
            resultBean: ResultBean,
            questionDTOList: ArrayList<QuestionDTO>
        ) {
            Intent(context, ResultActivity::class.java).run {
                putExtra("result_bean", resultBean)
                putExtra("question_list", questionDTOList)
                context.startActivity(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        questionDTOList = intent.getParcelableArrayListExtra("question_list")
    }

    override fun initView() {
        Log.d(TAG, "考试结果：${intent.getParcelableExtra<ResultBean>("result_bean")}")
        intent.getParcelableExtra<ResultBean>("result_bean")?.let { resultBean ->
            val animator = ObjectAnimator.ofFloat(0f, resultBean.score)
            animator.addUpdateListener {
                viewBinding.tvScore.text = getString(R.string.number_2_bit, it.animatedValue as Float)
            }
            animator.duration = 1000L
            animator.start()
            viewBinding.tvTestPaperValue.text = resultBean.title
            viewBinding.tvRightValue.text = resultBean.rightNum.toString()
            viewBinding.tvLeftValue.text = resultBean.leftNum.toString()
            viewBinding.tvUncheckValue.text = resultBean.unCheckNum.toString()
            viewBinding.tvUseTimeValue.text = TimeUtils.millionToHourMinuteSecond(resultBean.useTime)
        }
    }

    override fun initClick() {
        viewBinding.imgBack.setSingleClickListener {
            finish()
        }
        viewBinding.btnShowParse.setSingleClickListener {
            TestPaperDetailActivity.start(this@ResultActivity, questionDTOList!!)
        }
    }

    override fun getDebugTAG(): String {
        return ResultActivity::class.java.simpleName
    }
}