package com.example.simplechoose.pages.testPaperDetail

import com.example.simplechoose.bean.dto.QuestionDTO
import com.example.simplechoose.mvp.IModel
import com.example.simplechoose.mvp.IPresenter
import com.example.simplechoose.mvp.IView
import com.example.simplechoose.net.callback.RequestCallback
import com.example.simplechoose.pages.result.bean.ResultBean

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