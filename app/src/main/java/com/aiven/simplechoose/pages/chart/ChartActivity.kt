package com.aiven.simplechoose.pages.chart

import android.content.Context
import android.content.Intent
import com.aiven.simplechoose.databinding.ActivityLinearChartBinding
import com.aiven.simplechoose.pages.BaseActivity
import com.aiven.simplechoose.view.LinearChartView
import java.util.ArrayList

class ChartActivity : BaseActivity<ActivityLinearChartBinding>(ActivityLinearChartBinding::inflate) {

    companion object {
        fun start(context: Context) {
            Intent(context, ChartActivity::class.java).let {
                context.startActivity(it)
            }
        }
    }

    override fun initView() {
        viewBinding.linearChartView.setAxisYList(getAxisYList())
        viewBinding.linearChartView.setAxisXList(getAxisXList())
        viewBinding.speedWatchView.setCurValue(240)
    }

    override fun initClick() {

    }

    override fun getDebugTAG(): String {
        return ChartActivity::class.java.simpleName
    }

    private fun getAxisYList(): List<LinearChartView.AxisY> {
        val axisYList: MutableList<LinearChartView.AxisY> = ArrayList<LinearChartView.AxisY>()
        axisYList.add(LinearChartView.AxisY(25f, "正常"))
        axisYList.add(LinearChartView.AxisY(50f, "较高"))
        axisYList.add(LinearChartView.AxisY(75f, "非常高"))
        axisYList.add(LinearChartView.AxisY(100f, "爆表"))
        return axisYList
    }

    private fun getAxisXList(): List<LinearChartView.AxisX> {
        val axisXList: MutableList<LinearChartView.AxisX> = ArrayList<LinearChartView.AxisX>()
        axisXList.add(LinearChartView.AxisX(10f, 25f, "12:00"))
        axisXList.add(LinearChartView.AxisX(20f, 50f, "13:00"))
        axisXList.add(LinearChartView.AxisX(30f, 100f, "14:00"))
        axisXList.add(LinearChartView.AxisX(40f, 75f, "15:00"))
        axisXList.add(LinearChartView.AxisX(10f, 10f, "16:00"))
        axisXList.add(LinearChartView.AxisX(20f, 20f, "17:00"))
        axisXList.add(LinearChartView.AxisX(30f, 30f, "18:00"))
        axisXList.add(LinearChartView.AxisX(40f, 40f, "19:00"))
        axisXList.add(LinearChartView.AxisX(10f, 50f, "20:00"))
        axisXList.add(LinearChartView.AxisX(20f, 60f, "21:00"))
        axisXList.add(LinearChartView.AxisX(30f, 70f, "22:00"))
        axisXList.add(LinearChartView.AxisX(40f, 80f, "23:00"))
        return axisXList
    }
}