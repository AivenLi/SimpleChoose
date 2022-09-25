package com.aiven.simplechoose.pages.result

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.aiven.simplechoose.R
import com.aiven.simplechoose.bean.dto.QuestionDTO
import com.aiven.simplechoose.databinding.ActivityResultBinding
import com.aiven.simplechoose.pages.BaseActivity
import com.aiven.simplechoose.pages.result.adapter.AnswerResultAdapter
import com.aiven.simplechoose.bean.dto.ResultBean
import com.aiven.simplechoose.pages.testPaperDetail.TestPaperDetailActivity
import com.aiven.simplechoose.utils.TimeUtils
import com.aiven.simplechoose.utils.setSingleClickListener


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
            viewBinding.recyclerView.layoutManager = GridLayoutManager(this@ResultActivity, 8)
            viewBinding.recyclerView.adapter = AnswerResultAdapter(this@ResultActivity, resultBean.answerList)
        }
    }

    override fun initClick() {
        viewBinding.imgBack.setSingleClickListener {
            finish()
        }
        viewBinding.btnShowParse.setSingleClickListener {
            TestPaperDetailActivity.start(
                this@ResultActivity,
                questionDTOList!!,
                viewBinding.tvTestPaperValue.text.toString()
            )
        }
    }

    override fun getDebugTAG(): String {
        return ResultActivity::class.java.simpleName
    }
}