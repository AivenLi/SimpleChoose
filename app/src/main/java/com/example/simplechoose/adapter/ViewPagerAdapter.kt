package com.example.simplechoose.adapter;

import android.util.Log
import android.view.View;
import android.view.ViewGroup

import androidx.viewpager.widget.PagerAdapter;
import com.example.simplechoose.bean.dto.QuestionDTO
import com.example.simplechoose.view.SimpleChooseView

class ViewPagerAdapter(
    private val viewList: ArrayList<View>,
    private val dataList: ArrayList<QuestionDTO>
) : PagerAdapter() {

    companion object {
        private const val MAX_PAGE_SIZE = 4
    }

    override fun getCount(): Int {
        return dataList.size;
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        Log.d("SimpleChooseView", "Add Position: ${position}")
        val viewIndex = position % MAX_PAGE_SIZE
        container.addView((viewList[viewIndex] as SimpleChooseView).apply {
            initData(dataList[position])
        })
        return viewList[viewIndex]
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        Log.d("SimpleChooseView", "Remove Position $position")
        container.removeView(viewList[position % MAX_PAGE_SIZE])
    }
}
