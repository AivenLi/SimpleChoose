package com.example.simplechoose.pages.simpleChoose

import com.example.simplechoose.bean.dto.TestPaperDTO
import com.example.simplechoose.mvp.IModel
import com.example.simplechoose.mvp.IPresenter
import com.example.simplechoose.mvp.IView
import com.example.simplechoose.net.callback.BaseError
import com.example.simplechoose.net.callback.RequestCallback

interface TestPaperContract {

    interface View: IView {

        fun getTestPaperListSuccess(testPaperDTOList: ArrayList<TestPaperDTO>)
    }

    interface Presenter: IPresenter<View> {

        fun getTestPaperList(url: String)
    }

    interface Model: IModel {

        fun getTestPaperList(url: String, requestCallback: RequestCallback<ArrayList<TestPaperDTO>>)
    }
}