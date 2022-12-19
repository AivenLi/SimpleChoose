package com.aiven.simplechoose.pages.home

import android.util.Log
import com.aiven.simplechoose.bean.dto.TestBinDTO
import com.aiven.simplechoose.bean.dto.TestPaperTypeDTO
import com.aiven.simplechoose.bean.dto.UpdateAppDTO
import com.aiven.simplechoose.bean.vo.TestBinVo
import com.aiven.simplechoose.db.SimpleDataBase
import com.aiven.simplechoose.db.entity.InsertUpdateTestEntity
import com.aiven.simplechoose.mvp.BaseModel
import com.aiven.simplechoose.net.BaseRequest
import com.aiven.simplechoose.net.callback.RequestCallback
import com.aiven.simplechoose.pages.home.api.HomeApi
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class HomeModelImpl: BaseModel<HomeApi>(HomeApi::class.java), HomeContract.Model {

    private val type = object : TypeToken<ArrayList<TestPaperTypeDTO>>(){}.type

    override fun getQuestionTypeList(requestCallback: RequestCallback<ArrayList<TestPaperTypeDTO>>) {
        BaseRequest.requestWithCache(
            observable = service.getQuestionTypeList(),
            key = HomeApi.CACHE_KEY_QUESTION_LIST_JSON,
            type = type,
            requestCallback = requestCallback
        )
    }

    override fun checkAppUpdate(requestCallback: RequestCallback<UpdateAppDTO>) {
        BaseRequest.request(
            service.checkAppUpdate(),
            requestCallback
        )
    }

    override suspend fun findById(id: String): InsertUpdateTestEntity? {
        Log.d("HomeFragment-Debug", "查询前，线程：${Thread.currentThread().name}")
        val insertUpdateTestEntity = SimpleDataBase.getInstance().insertUpdateTestDao().findByIdCoroutine(id)
        Log.d("HomeFragment-Debug", "查询后，线程：${Thread.currentThread().name}")
        return insertUpdateTestEntity
    }

    override fun testBin(testBinVo: TestBinVo, requestCallback: RequestCallback<Void>) {
        BaseRequest.request(
            service.testBin(
                "http://192.168.5.37:5767/school/student/insert/bin",
                testBinVo
            ),
            requestCallback
        )
    }

    override fun selectBin(callback: RequestCallback<List<TestBinDTO>>) {
        BaseRequest.request(
            service.selectBin("http://192.168.5.37:5767/school/student/select/bin"),
            callback
        )
    }


//    override fun findByIdCoroutine(id: String): InsertUpdateTestEntity {
//        val insertUpdateTestDao = SimpleDataBase.getInstance().insertUpdateTestDao()
//
//    }
}
