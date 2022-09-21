package com.aiven.simplechoose.pages.testPaperDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.aiven.simplechoose.R
import com.aiven.simplechoose.adapter.OnSingleClickListener
import com.aiven.simplechoose.adapter.TestPaperCheckAdapter
import com.aiven.simplechoose.adapter.ViewPagerAdapter
import com.aiven.simplechoose.bean.dto.AnswerDTO
import com.aiven.simplechoose.bean.dto.QuestionDTO
import com.aiven.simplechoose.databinding.ActivityTestPaperDetailBinding
import com.aiven.simplechoose.databinding.DialogLoadingBinding
import com.aiven.simplechoose.databinding.DialogShowTestPaperCheckListBinding
import com.aiven.simplechoose.databinding.DialogYesNoBinding
import com.aiven.simplechoose.mvp.MVPActivity
import com.aiven.simplechoose.net.callback.BaseError
import com.aiven.simplechoose.pages.CustomDialog
import com.aiven.simplechoose.pages.result.ResultActivity
import com.aiven.simplechoose.pages.result.bean.ResultBean
import com.aiven.simplechoose.utils.ThemeUtils
import com.aiven.simplechoose.utils.TimeUtils
import com.aiven.simplechoose.utils.setSingleClickListener
import com.aiven.simplechoose.view.SimpleChooseView
import com.bumptech.glide.Glide
import com.kennyc.view.MultiStateView
import io.reactivex.rxjava3.disposables.Disposable
import kotlin.math.roundToInt

class TestPaperDetailActivity :
    MVPActivity<ActivityTestPaperDetailBinding, TestPaperDetailContract.View, TestPaperDetailContract.Presenter>(
        ActivityTestPaperDetailBinding::inflate
    ), TestPaperDetailContract.View {

    private var isTest = false
    private var uncheckSubmit = false
    private var startTime = 0L
    private lateinit var title: String
    private lateinit var url: String
    private lateinit var questionViewList: ArrayList<View>
    private val questionBeanList = ArrayList<QuestionDTO>()
    private lateinit var questionAdapter: ViewPagerAdapter
    private var onSingleClickListener: OnSingleClickListener? = null
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

    private var handler: Handler? = null

    private val dialogYesNo by lazy {
        CustomDialog<DialogYesNoBinding>(
            this,
            DialogYesNoBinding::inflate
        )
    }

    private val dialogLoading by lazy {
        CustomDialog<DialogLoadingBinding>(
            this,
            DialogLoadingBinding::inflate
        )
    }

    private lateinit var checkAnswerMap: HashMap<Int, Boolean>

    private val testPaperCheckAdapter by lazy {
        TestPaperCheckAdapter(
            this,
            checkAnswerMap
        )
    }

    private val dialogCheckDetail by lazy {
        CustomDialog<DialogShowTestPaperCheckListBinding>(
            this,
            DialogShowTestPaperCheckListBinding::inflate
        ).apply {
            binding.recyclerView.layoutManager = GridLayoutManager(this@TestPaperDetailActivity, 5)
            binding.recyclerView.adapter = testPaperCheckAdapter
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

        fun start(context: Context, questionDTOList: ArrayList<QuestionDTO>, title: String) {
            Intent(context, TestPaperDetailActivity::class.java).run {
                putExtra("question_list", questionDTOList)
                putExtra("title", title)
                context.startActivity(this)
            }
        }
        /**
         * 30分钟
         * */
        const val DEFAULT_TIME_COUNT = 30L * 60L

        const val DIALOG_YES_NO_TEST = 0
        const val DIALOG_YES_NO_UN_CHECK = 1
        const val DIALOG_YES_NO_BACK = 2
    }

    override fun createPresenter(): TestPaperDetailContract.Presenter {
        return TestPaperDetailPresenter()
    }

    override fun initView() {
        questionViewList = arrayListOf(
//            SimpleChooseView(this),
//            SimpleChooseView(this),
//            SimpleChooseView(this),
//            SimpleChooseView(this)
        )
        val questionDTOList = intent.getParcelableArrayListExtra<QuestionDTO>("question_list")
        if (!questionDTOList.isNullOrEmpty()) {
            isTest = false
            viewBinding.tvPageTitle.text = getString(R.string.show_parse)
            viewBinding.tvTimeCountTitle.visibility = View.GONE
            viewBinding.tvTimeCountValue.visibility = View.GONE
            questionBeanList.addAll(questionDTOList)
            createSimpleChooseView(questionDTOList.size)
            initCheckAnswerList()
        } else {
            isTest = true
            viewBinding.tvTimeCountTitle.visibility = View.VISIBLE
            viewBinding.tvTimeCountValue.visibility = View.VISIBLE
            viewBinding.tvTimeCountValue.text = "---"
        }
        questionAdapter =
            ViewPagerAdapter(
                questionViewList,
                questionBeanList,
                if (questionBeanList.isEmpty()) {
                    Log.d(TAG, "考试模式")
                    SimpleChooseView.MODE_TEST
                } else {
                    Log.d(TAG, "解析模式")
                    SimpleChooseView.MODE_PARSE
                }
            )
        viewBinding.viewPager.offscreenPageLimit = 1
        viewBinding.viewPager.adapter = questionAdapter
        url = intent.getStringExtra("url") ?: ""
        title = intent.getStringExtra("title") ?: ""
        viewBinding.tvSubTitle.text = title
    }

    override fun initClick() {
        viewBinding.imgBack.setSingleClickListener {
            if (isTest) {
                showExitDialog()
            } else {
                finish()
            }
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
        viewBinding.btnNext.setSingleClickListener(timeout = 500) {
            val ct = viewBinding.viewPager.currentItem
            if (ct < questionBeanList.size - 1) {
                viewBinding.viewPager.currentItem = ct + 1
            }
        }
        viewBinding.btnDetail.setSingleClickListener {
            testPaperCheckAdapter.notifyDataSetChanged()
            dialogCheckDetail.show()
        }
        viewBinding.btnSubmit.setSingleClickListener {
            submitTestPaper(false)
        }
        dialogYesNo.binding.tvYesNoYes.setSingleClickListener {
            dialogYesNo.hide()
            when (dialogYesNo.binding.tvYesNoTitle.text.toString()) {
                getString(R.string.exit_test_paper_title) -> {
                    finish()
                }
                getString(R.string.click_yes_to_test_paper) -> {
                    initHandler()
                    handler?.let {
                        it.sendMessage(
                            it.obtainMessage(
                                1234,
                                DEFAULT_TIME_COUNT
                            )
                        )
                    }
                    startTime = System.currentTimeMillis()
                }
                getString(R.string.has_un_check_title) -> {
                    uncheckSubmit = true
                    submitTestPaper(false)
                }
            }
        }
        dialogYesNo.binding.tvYesNoCancel.setSingleClickListener {
            dialogYesNo.hide()
            when (dialogYesNo.binding.tvYesNoTitle.text.toString()) {
                getString(R.string.click_yes_to_test_paper) -> {
                    finish()
                }
            }
        }
    }

    override fun initData() {
        if (questionBeanList.isEmpty()) {
            viewBinding.multiStatView.viewState = MultiStateView.VIEW_STATE_LOADING
            mPresenter.getTestPaperDetail(url)
        } else {
            viewBinding.smartRefresh.setEnableRefresh(false)
            viewBinding.lytOptContainer.visibility = View.VISIBLE
            viewBinding.pgbPercent.visibility = View.VISIBLE
            //viewBinding.lytOptBottomContainer.visibility = View.GONE
            viewBinding.btnSubmit.visibility = View.GONE
            questionAdapter.notifyDataSetChanged()
        }
    }

    override fun getTestPaperDetailSuccess(questionDTOList: ArrayList<QuestionDTO>) {
        if (questionBeanList.isEmpty() && questionDTOList.isEmpty()) {
            viewBinding.multiStatView.viewState = MultiStateView.VIEW_STATE_EMPTY
            return
        }
        createSimpleChooseView(questionDTOList.size)
        questionBeanList.clear()
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
        dialogYesNo.binding.tvYesNoTitle.text = getString(R.string.click_yes_to_test_paper)
        dialogYesNo.binding.tvContent.visibility = View.GONE
        initCheckAnswerList()
        dialogYesNo.show(false)
    }

    override fun getTestPaperResultSuccess(resultBean: ResultBean) {
        ResultActivity.start(this, resultBean, questionBeanList)
        finish()
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

    override fun onBackPressed() {
        if (isTest) {
            showExitDialog()
        } else {
            super.onBackPressed()
        }
    }

    private fun showExitDialog() {
        dialogYesNo.binding.tvYesNoTitle.text = getString(R.string.exit_test_paper_title)
        dialogYesNo.binding.tvContent.text = getString(R.string.exit_test_paper_desc)
        dialogYesNo.binding.tvContent.visibility = View.VISIBLE
        dialogYesNo.show(false)
    }

    override fun onDestroy() {
        handler?.removeCallbacksAndMessages(null)
        handler = null
        viewBinding.viewPager.removeOnPageChangeListener(onPagerChangeListener)
        super.onDestroy()
    }

    private fun initCheckAnswerList() {
        if (onSingleClickListener != null) {
            return
        }
        checkAnswerMap = HashMap()
        if (isTest) {
            onSingleClickListener = object : OnSingleClickListener {
                override fun onSingleClickListener(position: Int) {
                    Log.d(TAG, "Position: $position")
                    checkAnswerMap[viewBinding.viewPager.currentItem] = true
                    val ct = viewBinding.viewPager.currentItem
                    if (ct < questionBeanList.size - 1) {
                        viewBinding.viewPager.currentItem = ct + 1
                    }
                }
            }
            for (i in 0 until questionBeanList.size) {
                checkAnswerMap[i] = false
            }
            for (simpleChooseView in questionViewList) {
                (simpleChooseView as SimpleChooseView).setOnSingleClickListener(onSingleClickListener)
            }
        } else {
            for ((index, value) in questionBeanList.withIndex()) {
                var checked = false
                for (answer in value.chooseList) {
                    if (answer.selected) {
                        checked = true
                        break
                    }
                }
                checkAnswerMap[index] = checked
            }
        }
        testPaperCheckAdapter.setOnSingleClickListener(object : OnSingleClickListener {
            override fun onSingleClickListener(position: Int) {
                viewBinding.viewPager.setCurrentItem(position, false)
                viewBinding.viewPager.setCurrentItem(position, true)
                dialogCheckDetail.hide()
            }
        })
    }

    private lateinit var stringBuffer: StringBuffer
    private var isDark = false
    private fun initHandler() {
        if (handler != null) {
            return
        }
        isDark = ThemeUtils.isDarkMode(this@TestPaperDetailActivity)
        stringBuffer = StringBuffer()
        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                if (msg.what == 1234) {
                    val timecount = msg.obj as Long
                    stringBuffer.delete(0, stringBuffer.length)
                    val minutes = timecount / 60
                    if (minutes < 10) {
                        stringBuffer.append("0")
                    }
                    stringBuffer.append(minutes)
                    stringBuffer.append(":")
                    val seconds = timecount % 60
                    if (seconds < 10) {
                        stringBuffer.append("0")
                    }
                    stringBuffer.append(seconds)
                    viewBinding.tvTimeCountValue.text = stringBuffer.toString()
                    if (timecount < 5 * 60) {
                        viewBinding.tvTimeCountValue.setTextColor(
                            if (isDark) {
                                ContextCompat.getColor(this@TestPaperDetailActivity, R.color.night_warning)
                            } else {
                                ContextCompat.getColor(this@TestPaperDetailActivity, R.color.light_warning)
                            }
                        )
                    } else {
                        viewBinding.tvTimeCountValue.setTextColor(
                            if (isDark) {
                                ContextCompat.getColor(this@TestPaperDetailActivity, R.color.night_page_title)
                            } else {
                                ContextCompat.getColor(this@TestPaperDetailActivity, R.color.light_page_title)
                            }
                        )
                    }
                    if (timecount == 0L) {
                        submitTestPaper(true)
                    } else {
                        sendMessageDelayed(
                            obtainMessage(
                                1234,
                                timecount - 1
                            ),
                            1000L
                        )
                    }
                }
            }
        }
    }

    private var isSubmit = false
    private fun submitTestPaper(timeout: Boolean) {
        if (isSubmit) {
            return
        }
        isSubmit = true
        if (!timeout && !uncheckSubmit) {
            for (questionBean in questionBeanList) {
                var checked = false
                for (answerDTO in questionBean.chooseList) {
                    if (answerDTO.selected) {
                        checked = true
                        break
                    }
                }
                if (!checked) {
                    dialogYesNo.binding.tvYesNoTitle.text = getString(R.string.has_un_check_title)
                    dialogYesNo.binding.tvContent.text = getString(R.string.has_un_check_desc)
                    dialogYesNo.binding.tvContent.visibility = View.VISIBLE
                    dialogYesNo.show(false)
                    isSubmit = false
                    return
                }
            }
        }
        dialogYesNo.hide()
        dialogLoading.binding.tvTitle.text = getString(R.string.calculate_score)
        dialogLoading.show(false)
        mPresenter.submitTestPaper(
            title,
            System.currentTimeMillis() - startTime,
            questionBeanList
        )
    }

    private fun createSimpleChooseView(size: Int) {
        if (questionViewList.size < size) {
            for (i in 0 until (size - questionViewList.size)) {
                questionViewList.add(
                    SimpleChooseView(this@TestPaperDetailActivity)
                )
            }
        }
    }
}