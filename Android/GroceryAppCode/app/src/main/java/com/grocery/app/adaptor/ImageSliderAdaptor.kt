package com.grocery.app.adaptor

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.grocery.app.R
import com.grocery.app.activity.ImageSliderActivity
import com.grocery.app.utils.Common
import java.util.*

class ImageSliderAdaptor(var context: Activity, private val arrayList: ArrayList<*>) : PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {

        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.row_viewpager_item, view, false) as ViewGroup
        val mImageView = itemView.findViewById<ImageView>(R.id.img_pager)
        Glide.with(context)
            .load(arrayList[position])
            .placeholder(R.drawable.ic_placeholder)
            .into(mImageView)
       Common.getLog("InAaptor",arrayList[position].toString());
        mImageView.setOnClickListener {
            context.startActivity(Intent(context,ImageSliderActivity::class.java).putExtra("imageList",arrayList))
        }


        (view as ViewPager).addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View?)
    }

}