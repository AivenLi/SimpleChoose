package com.aiven.simplechoose.pages.testPaperDetail

import com.aiven.simplechoose.bean.dto.QuestionDTO
import com.aiven.simplechoose.mvp.BaseModel
import com.aiven.simplechoose.net.callback.RequestCallback
import com.aiven.simplechoose.net.request.BaseRequest
import com.aiven.simplechoose.pages.result.bean.ResultBean
import com.aiven.simplechoose.pages.testPaperDetail.api.TestPaperDetailApi
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlin.math.roundToInt

class TestPaperDetailModelImpl: BaseModel<TestPaperDetailApi>(TestPaperDetailApi::class.java), TestPaperDetailContract.Model {

    override fun getTestPaperDetail(
        url: String,
        requestCallback: RequestCallback<ArrayList<QuestionDTO>>
    ) {
        BaseRequest.request(service.getTestPaperDetail(url), requestCallback)
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
            for (questionDTO in questionDTOList) {
                if (questionDTO.mode == 0) {
                    var checked = false
                    for (answer in questionDTO.chooseList) {
                        if (answer.selected) {
                            checked = true
                            if (answer.index == questionDTO.answer) {
                                rightNum++
                            } else {
                                leftNum++
                            }
                            break
                        }
                    }
                    if (!checked) {
                        unCheckNum++
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
                    } else if (checkRightNum == questionDTO.answerList!!.size) {
                        rightNum++
                    } else {
                        leftNum++
                    }
                }
            }
            Thread.sleep(1000L)
            it.onNext(
                ResultBean(
                    title = title,
                    score = ((rightNum.toFloat() / (rightNum + leftNum + unCheckNum)) * 100f),
                    rightNum = rightNum,
                    leftNum = leftNum,
                    unCheckNum = unCheckNum,
                    useTime = useTime
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