package com.example.simplechoose.pages.testPaperDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.simplechoose.R
import com.example.simplechoose.adapter.ViewPagerAdapter
import com.example.simplechoose.bean.dto.AnswerDTO
import com.example.simplechoose.bean.dto.QuestionDTO
import com.example.simplechoose.databinding.ActivityTestPaperDetailBinding
import com.example.simplechoose.mvp.MVPActivity
import com.example.simplechoose.net.callback.BaseError
import com.example.simplechoose.pages.result.ResultActivity
import com.example.simplechoose.pages.result.bean.ResultBean
import com.example.simplechoose.utils.setSingleClickListener
import com.example.simplechoose.view.SimpleChooseView
import com.kennyc.view.MultiStateView
import io.reactivex.rxjava3.disposables.Disposable
import kotlin.math.roundToInt

class TestPaperDetailActivity :
    MVPActivity<ActivityTestPaperDetailBinding, TestPaperDetailContract.View, TestPaperDetailContract.Presenter>(
        ActivityTestPaperDetailBinding::inflate
    ), TestPaperDetailContract.View {

    private var startTime = 0L
    private lateinit var title: String
    private lateinit var url: String
    private lateinit var questionViewList: ArrayList<View>
    private val questionBeanList = ArrayList<QuestionDTO>()
    private lateinit var questionAdapter: ViewPagerAdapter
    private val onPagerChangeListener: ViewPager.OnPageChangeListener by lazy {
        object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                if (questionBeanList.size > 1) {
                    viewBinding.pgbPercent.progress =
                        ((position.toFloat() / (questionBeanList.size - 1)) * 100).toInt()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        }
    }

    companion object {

        fun start(context: Context, url: String, title: String) {
            Intent(context, TestPaperDetailActivity::class.java).run {
                putExtra("url", url)
                putExtra("title", title)
                context.startActivity(this)
            }
        }
    }

    override fun createPresenter(): TestPaperDetailContract.Presenter {
        return TestPaperDetailPresenter()
    }

    override fun initView() {
        /**
         * 当点击“开始考试后”才应开始计时
         * */
        startTime = System.currentTimeMillis()
        questionViewList = arrayListOf(
            SimpleChooseView(this),
            SimpleChooseView(this),
            SimpleChooseView(this),
            SimpleChooseView(this)
        )
        questionAdapter = ViewPagerAdapter(questionViewList, questionBeanList)
        viewBinding.viewPager.offscreenPageLimit = 1
        viewBinding.viewPager.adapter = questionAdapter
        url = intent.getStringExtra("url") ?: ""
        title = intent.getStringExtra("title") ?: ""
    }

    override fun initClick() {
        viewBinding.imgBack.setSingleClickListener {
            finish()
        }
        viewBinding.smartRefresh.setOnRefreshListener {
            mPresenter.getTestPaperDetail(url)
        }
        viewBinding.viewPager.addOnPageChangeListener(onPagerChangeListener)
        viewBinding.btnPrev.setSingleClickListener {
            val ct = viewBinding.viewPager.currentItem
            if (ct > 0) {
                viewBinding.viewPager.currentItem = ct - 1
            }
        }
        viewBinding.btnNext.setSingleClickListener {
            val ct = viewBinding.viewPager.currentItem
            if (ct < questionBeanList.size - 1) {
                viewBinding.viewPager.currentItem = ct + 1
            }
        }
        viewBinding.btnDetail.setSingleClickListener {

        }
        viewBinding.btnSubmit.setSingleClickListener {
            mPresenter.submitTestPaper(
                title,
                System.currentTimeMillis() - startTime,
                questionBeanList
            )
        }
    }

    override fun initData() {
        viewBinding.multiStatView.viewState = MultiStateView.VIEW_STATE_LOADING
        mPresenter.getTestPaperDetail(url)
    }

    override fun getTestPaperDetailSuccess(questionDTOList: ArrayList<QuestionDTO>) {
        questionBeanList.addAll(questionDTOList)
        questionAdapter.notifyDataSetChanged()
        viewBinding.smartRefresh.postDelayed(
            {
                viewBinding.smartRefresh.setEnableRefresh(false)
            },
            500
        )
        viewBinding.lytOptContainer.visibility = View.VISIBLE
        viewBinding.pgbPercent.visibility = View.VISIBLE
        viewBinding.multiStatView.viewState = MultiStateView.VIEW_STATE_CONTENT
    }

    override fun getTestPaperResultSuccess(resultBean: ResultBean) {
        ResultActivity.start(this, resultBean)
    }

    override fun onRequestError(baseError: BaseError) {
        baseError.msg?.let {
            toast(it)
        }
        viewBinding.lytOptContainer.visibility = View.GONE
        viewBinding.pgbPercent.visibility = View.GONE
        viewBinding.multiStatView.viewState = MultiStateView.VIEW_STATE_ERROR
    }

    override fun onRequestFinish() {
        if (viewBinding.smartRefresh.isRefreshing) {
            viewBinding.smartRefresh.finishRefresh()
        }
    }

    override fun getDebugTAG(): String {
        return TestPaperDetailActivity::class.java.simpleName
    }

    override fun onDestroy() {
        viewBinding.viewPager.removeOnPageChangeListener(onPagerChangeListener)
        super.onDestroy()
    }
}