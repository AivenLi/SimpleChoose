package com.aiven.simplechoose.pages.listtest

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.aiven.simplechoose.databinding.ActivityListTestBinding
import com.aiven.simplechoose.pages.BaseActivity

class ListTestActivity: BaseActivity<ActivityListTestBinding>(ActivityListTestBinding::inflate) {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ListTestActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun initView() {
        viewBinding.recyclerView.adapter = MultiAdapter(this, getData())
        viewBinding.recyclerView.layoutManager = LinearLayoutManager(this)
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
                    title = "第${i + 1}条二级数据",
                    childList = ArrayList()
                )
                item.childList!!.add(subItem)
                val thirdCount = random.nextInt(5)
                for (k in 0 until thirdCount) {
                    val thirdItem = MultiBean(
                        isFirst = false,
                        isSecond = false,
                        isOpen = false,
                        title = "第${i + 1}条三级数据",
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