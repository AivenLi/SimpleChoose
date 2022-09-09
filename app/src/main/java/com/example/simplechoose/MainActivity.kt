package com.example.simplechoose

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.example.simplechoose.bean.dto.AnswerDTO
import com.example.simplechoose.bean.dto.QuestionDTO
import com.example.simplechoose.view.SimpleChooseView

class MainActivity : AppCompatActivity() {

    private val questionViewList = ArrayList<View>()
    private val questionBeanList = ArrayList<QuestionDTO>()
    private lateinit var questionAdapter: ViewPagerAdapter
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        questionAdapter = ViewPagerAdapter(questionViewList, questionBeanList)
        viewPager = findViewById(R.id.view_pager)
        viewPager.offscreenPageLimit = 1
        viewPager.adapter = questionAdapter
        questionViewList.addAll(getData())
        questionAdapter.notifyDataSetChanged()
    }

    private fun getData(): ArrayList<View> {
        val questionViewList = arrayListOf<View>(
            SimpleChooseView(this),
            SimpleChooseView(this),
            SimpleChooseView(this),
            SimpleChooseView(this)
        )
        Log.d("SimpleChooseView", "获取view")
        questionBeanList.add(
            QuestionDTO(
                "1. 下图为巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉示意图，" +
                        "请看图答题[image]图中巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉的是（）",
                arrayListOf(
                    AnswerDTO(
                        "A. 这是一个很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长的答案",
                        0
                    ),
                    AnswerDTO(
                        "B. 选B",
                        1
                    ),
                    AnswerDTO(
                        "C. 选C",
                        2
                    ),
                    AnswerDTO(
                        "D. 选D",
                        3
                    )
                ),
                0,
                2,
                null,
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Ftst.tsinghuajournals.com%2Ffileup%2F1007-0214%2FFIGURE%2F2017-22-1%2FImages%2FTST-2017-7830895-F001.jpg&refer=http%3A%2F%2Ftst.tsinghuajournals.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1665025438&t=35042ab9c2b12a5dd5e98f851efc594a"
            )
        )
        questionBeanList.add(
            QuestionDTO(
                "2. 下图为巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉示意图，" +
                        "请看图答题[image]图中巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉的是（）",
                arrayListOf(
                    AnswerDTO(
                        "A. 这是一个很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长的答案",
                        0
                    ),
                    AnswerDTO(
                        "B. 选B",
                        1
                    ),
                    AnswerDTO(
                        "C. 选C",
                        2
                    ),
                    AnswerDTO(
                        "D. 选D",
                        3
                    )
                ),
                0,
                2,
                null,
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.2qqtouxiang.com%2Fpic%2FBZ7187_09.jpg&refer=http%3A%2F%2Fimg.2qqtouxiang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1665026279&t=fba21c98425bc2e497aaee6218969f3c"
            )
        )
        questionBeanList.add(
            QuestionDTO(
                "2. [image]上图为巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉示意图，" +
                        "请看图答题图中巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉的是（）",
                arrayListOf(
                    AnswerDTO(
                        "A. 选A",
                        0
                    ),
                    AnswerDTO(
                        "B. 选B",
                        1
                    ),
                    AnswerDTO(
                        "C. 这是一个很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长的答案",
                        2
                    ),
                    AnswerDTO(
                        "D. 选D",
                        3
                    )
                ),
                0,
                2,
                null,
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Ftst.tsinghuajournals.com%2Ffileup%2F1007-0214%2FFIGURE%2F2017-22-1%2FImages%2FTST-2017-7830895-F001.jpg&refer=http%3A%2F%2Ftst.tsinghuajournals.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1665025438&t=35042ab9c2b12a5dd5e98f851efc594a"
            )
        )
        questionBeanList.add(
            QuestionDTO(
                "3. 上图为巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉示意图，" +
                        "请看图答题图中巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉巴拉的是（）[image]",
                arrayListOf(
                    AnswerDTO(
                        "A. 选A",
                        0
                    ),
                    AnswerDTO(
                        "B. 选B",
                        1
                    ),
                    AnswerDTO(
                        "C. 选C",
                        2
                    ),
                    AnswerDTO(
                        "D. 这是一个很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长的答案",
                        3
                    )
                ),
                0,
                2,
                null,
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fpubs.rsc.org%2Fservices%2Fimages%2FRSCpubs.ePlatform.Service.FreeContent.ImageService.svc%2FImageService%2FArticleimage%2F2016%2FCC%2Fc6cc02693a%2Fc6cc02693a-s2_hi-res.gif&refer=http%3A%2F%2Fpubs.rsc.org&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1665026004&t=cc2e6797c631fbdd29b9304bfe1f7612"
            )
        )
        for (i in 3 until 40) {
            questionBeanList.add(
                QuestionDTO(
                    "${i + 1}. 这是一道测试题（）。",
                    arrayListOf(
                        AnswerDTO(
                            "A. 选A",
                            0
                        ),
                        AnswerDTO(
                            "B. 选B",
                            1
                        ),
                        AnswerDTO(
                            "C. 选C",
                            2
                        ),
                        AnswerDTO(
                            "D. 选D",
                            3
                        )
                    ),
                    i % 2,
                    i % 4,
                    null,
                )
            )
        }
        return questionViewList
    }
}