package com.aiven.simplechoose.pages.home

import androidx.lifecycle.LifecycleCoroutineScope
import com.aiven.simplechoose.bean.dto.TestBinDTO
import com.aiven.simplechoose.bean.dto.TestPaperTypeDTO
import com.aiven.simplechoose.bean.dto.UpdateAppDTO
import com.aiven.simplechoose.bean.vo.TestBinVo
import com.aiven.simplechoose.db.entity.InsertUpdateTestEntity
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

        fun updateFindById(insertUpdateTestEntity: InsertUpdateTestEntity)

        fun getLifecycleScope(): LifecycleCoroutineScope
    }

    interface Presenter: IPresenter<View> {

        fun getQuestionTypeList()

        fun checkAppUpdate()

        fun findById(id: String)

        fun testBin(testBinVo: TestBinVo)

        fun selectBin()
    }

    interface Model: IModel {

        fun getQuestionTypeList(requestCallback: RequestCallback<ArrayList<TestPaperTypeDTO>>)

        fun checkAppUpdate(requestCallback: RequestCallback<UpdateAppDTO>)

        suspend fun findById(id: String): InsertUpdateTestEntity?

      //  fun findByIdCoroutine(id: String): InsertUpdateTestEntity

        fun testBin(testBinVo: TestBinVo, requestCallback: RequestCallback<Void>)

        fun selectBin(callback: RequestCallback<List<TestBinDTO>>)
    }
}