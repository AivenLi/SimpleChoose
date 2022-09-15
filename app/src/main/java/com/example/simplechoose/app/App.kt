package com.example.simplechoose.app

import android.app.Application
import androidx.core.content.ContextCompat
import com.example.simplechoose.R
import com.example.simplechoose.utils.ThemeUtils
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator

class App : Application() {

    init {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(DefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.night_main, R.color.night_page_title)
            ClassicsHeader(context)
        })
        SmartRefreshLayout.setDefaultRefreshFooterCreator(DefaultRefreshFooterCreator { context, _ ->
            ClassicsFooter(context)
        })
    }
}