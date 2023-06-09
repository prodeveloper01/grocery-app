package com.grocery.app.activity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.grocery.app.R
import com.grocery.app.base.BaseActivity
import com.grocery.app.utils.Common
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_imagepager.*


class ImageSliderActivity:BaseActivity() {
    private var imgList:ArrayList<String>?=null
    override fun setLayout(): Int {
        return R.layout.activity_imagepager
    }

    override fun initView() {
        Common.getCurrentLanguage(this@ImageSliderActivity, false)
        imgList=intent.getStringArrayListExtra("imageList")
        val imageSlider=ImageSlider(this@ImageSliderActivity,imgList!!)
        pager.adapter=imageSlider

        ivCancle.setOnClickListener {
            finish()
        }
    }


    class ImageSlider(var mContext: Context, var imageList: ArrayList<String>) : PagerAdapter() {
        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun getCount(): Int {
            return imageList.size
        }

        override fun instantiateItem(view: ViewGroup, position: Int): Any {
            val inflater = LayoutInflater.from(mContext)
            val itemView = inflater.inflate(R.layout.row_imageviewpager_item, view, false) as ViewGroup
            val mImageView = itemView.findViewById<ImageView>(R.id.img_pager)
            Glide.with(mContext).load(imageList.get(position))
                .placeholder(R.drawable.ic_placeholder)
                .into(mImageView)

            (view as ViewPager).addView(itemView)
            return itemView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            (container as ViewPager).removeView(`object` as View?)
        }

    }

    override fun onBackPressed() {
        finish()
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@ImageSliderActivity, false)
    }
}