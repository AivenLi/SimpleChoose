package com.aiven.simplechoose.pages.testPaperDetail

import com.aiven.simplechoose.bean.dto.QuestionDTO
import com.aiven.simplechoose.db.SimpleDataBase
import com.aiven.simplechoose.db.entity.TestPaperRecord
import com.aiven.simplechoose.mvp.BaseModel
import com.aiven.simplechoose.net.BaseRequest
import com.aiven.simplechoose.net.callback.RequestCallback
import com.aiven.simplechoose.bean.dto.ResultBean
import com.aiven.simplechoose.bean.enums.AnswerResult
import com.aiven.simplechoose.pages.testPaperDetail.api.TestPaperDetailApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class TestPaperDetailModelImpl: BaseModel<TestPaperDetailApi>(TestPaperDetailApi::class.java), TestPaperDetailContract.Model {

    private val type = object : TypeToken<ArrayList<QuestionDTO>>(){}.type
    private val gson = Gson()

    override fun getTestPaperDetail(
        url: String,
        requestCallback: RequestCallback<ArrayList<QuestionDTO>>
    ) {
        BaseRequest.requestWithCacheWaitNet(
            observable = service.getTestPaperDetail(url),
            key = url,
            type = type,
            requestCallback = requestCallback
        )
    }

    override fun submitTestPaper(
        title: String,
        useTime: Long,
        questionDTOList: ArrayList<QuestionDTO>,
        requestCallback: RequestCallback<ResultBean>
    ) {
        val observable = Observable.create<ResultBean> {
            var rightNum = 0
            var leftNum = 0
            var unCheckNum = 0
            val answerResultList = ArrayList<AnswerResult>()
            for (questionDTO in questionDTOList) {
                if (questionDTO.mode == 0) {
                    var checked = false
                    for (answer in questionDTO.chooseList) {
                        if (answer.selected) {
                            checked = true
                            if (answer.index == questionDTO.answer) {
                                rightNum++
                                answerResultList.add(AnswerResult.RIGHT)
                            } else {
                                leftNum++
                                answerResultList.add(AnswerResult.LEFT)
                            }
                            break
                        }
                    }
                    if (!checked) {
                        unCheckNum++
                        answerResultList.add(AnswerResult.UNCHECK)
                    }
                } else {
                    var checked = false
                    var checkRightNum = 0
                    for (answer in questionDTO.chooseList) {
                        if (answer.selected) {
                            checked = true
                            for (rightAnswer in questionDTO.answerList!!) {
                                if (answer.index == rightAnswer) {
                                    checkRightNum++
                                    break
                                }
                            }
                        }
                    }
                    if (!checked) {
                        unCheckNum++
                        answerResultList.add(AnswerResult.UNCHECK)
                    } else if (checkRightNum == questionDTO.answerList!!.size) {
                        rightNum++
                        answerResultList.add(AnswerResult.RIGHT)
                    } else {
                        leftNum++
                        answerResultList.add(AnswerResult.LEFT)
                    }
                }
            }
            val score = ((rightNum.toFloat() / (rightNum + leftNum + unCheckNum)) * 100f)
            val testPaperRecord =
                TestPaperRecord(
                    title = title,
                    timestamp = System.currentTimeMillis(),
                    score = score,
                    jsonStr = gson.toJson(questionDTOList)
                )
            SimpleDataBase.getInstance().testPaperRecordDao().insert(testPaperRecord)
            Thread.sleep(1000L)
            it.onNext(
                ResultBean(
                    title = title,
                    score = score,
                    rightNum = rightNum,
                    leftNum = leftNum,
                    unCheckNum = unCheckNum,
                    useTime = useTime,
                    answerList = answerResultList
                )
            )
        }
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ResultBean> {
                override fun onSubscribe(d: Disposable) {
                    requestCallback.onRequestStart(d)
                }

                override fun onNext(t: ResultBean) {
                    requestCallback.onSuccess(t)
                }

                override fun onError(e: Throwable) {

                }

                override fun onComplete() {
                    requestCallback.onRequestFinish()
                }
            })
    }
}