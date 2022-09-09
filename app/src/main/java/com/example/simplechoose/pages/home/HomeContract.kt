package com.example.simplechoose.pages.home

import com.example.simplechoose.bean.dto.TestPaperTypeDTO
import com.example.simplechoose.mvp.IModel
import com.example.simplechoose.mvp.IPresenter
import com.example.simplechoose.mvp.IView
import com.example.simplechoose.net.callback.BaseError
import com.example.simplechoose.net.callback.RequestCallback

interface HomeContract {

    interface View: IView {

        fun getQuestionListTypeSuccess(testPaperTypeDTOList: ArrayList<TestPaperTypeDTO>)
        fun getQuestionListTypeFailure(baseError: BaseError)
    }

    interface Presenter: IPresenter<View> {

        fun getQuestionTypeList()
    }

    interface Model: IModel {

        fun getQuestionTypeList(requestCallback: RequestCallback<ArrayList<TestPaperTypeDTO>>)
    }
}