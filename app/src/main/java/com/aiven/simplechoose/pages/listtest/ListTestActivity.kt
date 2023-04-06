package com.aiven.simplechoose.pages.listtest

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aiven.simplechoose.databinding.ActivityListTestBinding
import com.aiven.simplechoose.pages.BaseActivity
import com.aiven.simplechoose.utils.setSingleClickListener
import java.util.*
import kotlin.collections.ArrayList

class ListTestActivity: BaseActivity<ActivityListTestBinding>(ActivityListTestBinding::inflate) {

    private val data = getData()
    private lateinit var layoutManager: LinearLayoutManager
    private var fixedViewHeight = 0
    private var fixedViewOldY = 0f
    private val fixedStack = Stack<MultiPosBean>()
    private val itemCreator = Stack<MultiPosBean>()
    private var currentItem: MultiPosBean? = null
    private lateinit var adapter: MultiAdapter
    private lateinit var multiPosBean: MultiPosBean

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ListTestActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun initView() {
        multiPosBean = MultiPosBean(data[0], 0)
        viewBinding.fltParentItem.postDelayed({
            fixedViewOldY = viewBinding.fltParentItem.y
        }, 300)
        viewBinding.lv1.tvTitle.text = data[0].title
        adapter = MultiAdapter(this, data)
        viewBinding.recyclerView.adapter = adapter
     //   viewBinding.recyclerView.adapter = MyAdapter(getItemData())
        layoutManager = LinearLayoutManager(this)
        viewBinding.recyclerView.layoutManager = layoutManager
        viewBinding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                fixedViewHeight = viewBinding.fltParentItem.height
            }
            /**
             * 向上滑动，dy为正数，反之为负数
             * */
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (data.size <= 1) {
                    return
                }
                val firstItemPosition = layoutManager.findFirstVisibleItemPosition()
                val firstView = layoutManager.findViewByPosition(firstItemPosition)!!
                val firstItem = data[firstItemPosition]
                val secondItemPosition = layoutManager.findFirstVisibleItemPosition() + 1
                val secondView = layoutManager.findViewByPosition(secondItemPosition)!!
                val secondItem = data[secondItemPosition]
                var topItem: MultiPosBean? = null
                if (dy > 0) {
                    if (dy < viewBinding.abc.height) {
                        if (secondItem.isFirst) {
                            val secondViewY = secondView.y
                            if (secondViewY < fixedViewHeight) {
                                viewBinding.fltParentItem.y = fixedViewOldY + (secondViewY - fixedViewHeight)
                            }
                        }
                        if (!isFixed(firstItem) && firstItem.isFirst) {
                            val secondViewY = firstView.y
                            if (secondViewY < fixedViewHeight) {
                                viewBinding.fltParentItem.y = fixedViewOldY + (secondViewY - fixedViewHeight)
                            }
                        }
                    } else {
                        viewBinding.fltParentItem.y = fixedViewOldY
                    }
                    if (isFixed(firstItem)) {
                        if (fixedStack.isEmpty() || fixedStack.peek().multiBean != firstItem) {
                            Log.d(TAG, "吸顶item，入栈：${firstItem.title}")
                            fixedStack.push(createMultiPosBean(firstItem, firstItemPosition))
                        }
                        if (firstView.y <= 0) {
                            viewBinding.fltParentItem.y = fixedViewOldY
                        }
                    }
                    topItem = null
                } else {
                    if (secondItem.isFirst) {
                        val secondViewY = secondView.y
                        if (secondViewY < fixedViewHeight) {
                            viewBinding.fltParentItem.y = recyclerView.y + (secondViewY - fixedViewHeight)
                        } else {
                            viewBinding.fltParentItem.y = fixedViewOldY
                        }
                    } else {
                        if (firstItemPosition == 0 && firstView.y == 0.00f) {
                            viewBinding.fltParentItem.y = fixedViewOldY
                        }
                    }
                    if (fixedStack.isNotEmpty() && fixedStack.peek().multiBean == firstItem) {
                        recycleMultiPosBean(fixedStack.pop())
                    }
                    topItem = if (!firstItem.isFirst && fixedStack.isNotEmpty()) {
                        fixedStack.peek()
                    } else {
                        null
                    }
                }
                if (firstItem.isFirst) {
                    viewBinding.lv1.tvTitle.text = firstItem.title
                    currentItem = multiPosBean
                    multiPosBean.multiBean = firstItem
                    multiPosBean.position = firstItemPosition
                }
                if (topItem != null) {
                    viewBinding.lv1.tvTitle.text = topItem.multiBean.title
                    currentItem = topItem
                }
            }
        })
        viewBinding.fltParentItem.setSingleClickListener {
            if (currentItem != null && currentItem!!.multiBean.isFirst) {
                if (currentItem!!.multiBean.isOpen) {
                    adapter.closeFirstItem(currentItem!!.position)
                } else {
                    adapter.openFirstItem(currentItem!!.position)
                }
            }
        }
    }

    private fun createMultiPosBean(item: MultiBean, position: Int): MultiPosBean {
        return if (itemCreator.isEmpty()) {
            MultiPosBean(
                item,
                position
            )
        } else {
            itemCreator.pop().apply {
                multiBean = item
                this.position = position
            }
        }
    }

    private fun recycleMultiPosBean(multiPosBean: MultiPosBean) {
        itemCreator.push(multiPosBean)
    }

    private fun isFixed(item: MultiBean?): Boolean {
        return if (item == null) {
            false
        } else {
            item.isFirst && item.isOpen && !item.childList.isNullOrEmpty()
        }
    }

    override fun initClick() {

    }

    override fun getDebugTAG(): String {
        return ListTestActivity::class.java.simpleName
    }


    private fun getData(): ArrayList<MultiBean> {
        val list = ArrayList<MultiBean>()
        val random = java.util.Random()
        for (i in 0 until 20) {
            val item = MultiBean(
                isFirst = true,
                isSecond = false,
                isOpen = false,
                title = "第${i + 1}条一级数据",
                childList = ArrayList()
            )
            val secondCount = random.nextInt(10)
            for (j in 0 until secondCount) {
                val subItem = MultiBean(
                    isFirst = false,
                    isSecond = true,
                    isOpen = false,
                    title = "第${j + 1}条二级数据，父级ID：${i + 1}",
                    childList = ArrayList()
                )
                item.childList!!.add(subItem)
                val thirdCount = random.nextInt(5)
                for (k in 0 until thirdCount) {
                    val thirdItem = MultiBean(
                        isFirst = false,
                        isSecond = false,
                        isOpen = false,
                        title = "第${k + 1}条三级数据，父级ID：${j + 1}, 祖父级ID：${i + 1}",
                        childList = null
                    )
                    subItem.childList!!.add(thirdItem)
                }
            }
            list.add(item)
        }
        return list
    }
}