package com.aiven.simplechoose.pages.home


import android.view.MenuItem
import android.view.View
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.aiven.simplechoose.R
import com.aiven.simplechoose.adapter.ViewPager2FragmentAdapter
import com.aiven.simplechoose.databinding.ActivityHomeBinding
import com.aiven.simplechoose.pages.BaseActivity
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.navigation.NavigationBarView

class HomeActivity : BaseActivity<ActivityHomeBinding>(
    ActivityHomeBinding::inflate
), NavigationBarView.OnItemSelectedListener {

    private var curIndex = 0
    private var exitTime = 0L

    override fun initView() {
        val fragments = arrayListOf<Fragment>(
            HomeFragment(),
            MineFragment()
        )
        viewBinding.viewPager2.adapter = ViewPager2FragmentAdapter(
            supportFragmentManager,
            lifecycle,
            fragments
        )
        viewBinding.navigationBottom.setOnItemSelectedListener(this)
        viewBinding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setBottomNavigationSelected(position)
            }
        })
        removeNavigationBottomLongClickToast()
    }

    override fun initClick() {
    }

    override fun getDebugTAG(): String {
        return HomeActivity::class.java.simpleName
    }

    override fun onBackPressed() {
        exit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var index = curIndex
        when (item.itemId) {
            R.id.item_home -> index = 0
            R.id.item_mine -> index = 1
        }
        if (index == curIndex) {
            return false
        }
        curIndex = index
        viewBinding.viewPager2.currentItem = index
        return true
    }

    private fun removeNavigationBottomLongClickToast() {
        val bottomNavigationView =  viewBinding.navigationBottom.getChildAt(0)
        bottomNavigationView.findViewById<View>(R.id.item_home).setOnLongClickListener { true }
        bottomNavigationView.findViewById<View>(R.id.item_mine).setOnLongClickListener { true }
    }

    private fun setBottomNavigationSelected(position: Int) {
        if (position == 0) {
            viewBinding.navigationBottom.selectedItemId = R.id.item_home
        } else {
            viewBinding.navigationBottom.selectedItemId = R.id.item_mine
        }
    }

    private fun exit() {
        val interval = System.currentTimeMillis() - exitTime
        if (interval > 2000L) {
            toast(getString(R.string.enter_to_exit_again))
            exitTime = System.currentTimeMillis()
        } else {
            finish()
        }
    }
}