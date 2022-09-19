package com.aiven.simplechoose.pages.testPaper

import com.aiven.simplechoose.bean.dto.TestPaperDTO
import com.aiven.simplechoose.mvp.IModel
import com.aiven.simplechoose.mvp.IPresenter
import com.aiven.simplechoose.mvp.IView
import com.aiven.simplechoose.net.callback.RequestCallback

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