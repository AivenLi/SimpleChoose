package com.aiven.simplechoose.pages.home

import com.aiven.simplechoose.bean.dto.TestPaperTypeDTO
import com.aiven.simplechoose.bean.dto.UpdateAppDTO
import com.aiven.simplechoose.mvp.IModel
import com.aiven.simplechoose.mvp.IPresenter
import com.aiven.simplechoose.mvp.IView
import com.aiven.simplechoose.net.callback.BaseError
import com.aiven.simplechoose.net.callback.RequestCallback

interface HomeContract {

    interface View: IView {

        fun getQuestionListTypeSuccess(testPaperTypeDTOList: ArrayList<TestPaperTypeDTO>)
        fun getQuestionListTypeFailure(baseError: BaseError)

        fun checkAppUpdateSuccess(updateAppDTO: UpdateAppDTO)
    }

    interface Presenter: IPresenter<View> {

        fun getQuestionTypeList()

        fun checkAppUpdate()
    }

    interface Model: IModel {

        fun getQuestionTypeList(requestCallback: RequestCallback<ArrayList<TestPaperTypeDTO>>)

        fun checkAppUpdate(requestCallback: RequestCallback<UpdateAppDTO>)
    }
}