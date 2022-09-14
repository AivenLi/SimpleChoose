package com.example.simplechoose.app

import android.app.Application
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator

class App : Application() {

    init {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(DefaultRefreshHeaderCreator { context, _ ->
            ClassicsHeader(context)
        })
        SmartRefreshLayout.setDefaultRefreshFooterCreator(DefaultRefreshFooterCreator { context, _ ->
            ClassicsFooter(context)
        })
    }
}