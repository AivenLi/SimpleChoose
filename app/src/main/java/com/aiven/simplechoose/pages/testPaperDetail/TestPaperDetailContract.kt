package com.aiven.simplechoose.pages.testPaperDetail

import com.aiven.simplechoose.bean.dto.QuestionDTO
import com.aiven.simplechoose.mvp.IModel
import com.aiven.simplechoose.mvp.IPresenter
import com.aiven.simplechoose.mvp.IView
import com.aiven.simplechoose.net.callback.RequestCallback
import com.aiven.simplechoose.pages.result.bean.ResultBean

interface TestPaperDetailContract {

    interface View: IView {

        fun getTestPaperDetailSuccess(questionDTOList: ArrayList<QuestionDTO>)

        fun getTestPaperResultSuccess(resultBean: ResultBean)
    }

    interface Presenter: IPresenter<View> {

        fun getTestPaperDetail(url: String)

        fun submitTestPaper(
            title: String,
            useTime: Long,
            questionDTOList: ArrayList<QuestionDTO>
        )
    }

    interface Model: IModel {

        fun getTestPaperDetail(url: String, requestCallback: RequestCallback<ArrayList<QuestionDTO>>)

        fun submitTestPaper(
            title: String,
            useTime: Long,
            questionDTOList: ArrayList<QuestionDTO>,
            requestCallback: RequestCallback<ResultBean>
        )
    }
}