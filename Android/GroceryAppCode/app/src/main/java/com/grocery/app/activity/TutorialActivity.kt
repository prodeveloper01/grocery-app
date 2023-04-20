package com.grocery.app.activity

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.grocery.app.R
import com.grocery.app.base.BaseActivity
import com.grocery.app.utils.Common
import com.grocery.app.utils.SharePreference.Companion.isTutorial
import com.grocery.app.utils.SharePreference.Companion.setBooleanPref
import kotlinx.android.synthetic.main.activity_tutorial.*

class TutorialActivity:BaseActivity() {
    var imagelist:ArrayList<Drawable>?=null
    override fun setLayout(): Int {
       return R.layout.activity_tutorial
    }

    override fun initView() {
        Common.getCurrentLanguage(this@TutorialActivity, false)
        setBooleanPref(this@TutorialActivity, isTutorial,true)
        imagelist = ArrayList()
        imagelist!!.add(ResourcesCompat.getDrawable(resources, R.drawable.s1, null)!!)
        imagelist!!.add(ResourcesCompat.getDrawable(resources, R.drawable.s2, null)!!)
        imagelist!!.add(ResourcesCompat.getDrawable(resources, R.drawable.s3, null)!!)
        imagelist!!.add(ResourcesCompat.getDrawable(resources, R.drawable.s4, null)!!)
        viewPager.adapter=StartScreenAdapter(this@TutorialActivity,imagelist!!)
        tabLayout.setupWithViewPager(viewPager, true)

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
            override fun onPageSelected(i: Int) {
                if (i == imagelist!!.size - 1) {
                    tvBtnSkip.text=resources.getString(R.string.start)
                }else{
                    tvBtnSkip.text=resources.getString(R.string.skip)
                }
            }

            override fun onPageScrollStateChanged(i: Int) {}
        })

        tvBtnSkip.setOnClickListener {
            openActivity(DashboardActivity::class.java)
            finish()
        }
    }

    private class StartScreenAdapter(var mContext: Context, var mImagelist: ArrayList<Drawable>) : PagerAdapter() {
        override fun instantiateItem(collection: ViewGroup, position: Int): Any {
            val inflater = LayoutInflater.from(mContext)
            val layout = inflater.inflate(R.layout.row_tutorial, collection, false) as ViewGroup
            val iv: ImageView = layout.findViewById(R.id.ivScreen)
            iv.setImageDrawable(mImagelist[position])
            collection.addView(layout)
            return layout
        }

        override fun destroyItem(
            collection: ViewGroup,
            position: Int,
            view: Any
        ) {
            collection.removeView(view as View)
        }

        override fun getCount(): Int {
            return mImagelist.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@TutorialActivity, false)
    }
}