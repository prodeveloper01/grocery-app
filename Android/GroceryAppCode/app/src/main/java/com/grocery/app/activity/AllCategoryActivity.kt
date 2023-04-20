package com.grocery.app.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.grocery.app.R
import com.grocery.app.api.ApiClient
import com.grocery.app.base.BaseActivity
import com.grocery.app.base.BaseAdaptor
import com.grocery.app.model.CategoryItemModel
import com.grocery.app.model.ListResponse
import com.grocery.app.utils.Common
import com.grocery.app.utils.Common.isCheckNetwork
import com.grocery.app.utils.SharePreference
import kotlinx.android.synthetic.main.activity_allcategory.*
import kotlinx.android.synthetic.main.activity_allcategory.ivBack
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class AllCategoryActivity : BaseActivity() {
    override fun setLayout(): Int {
        return R.layout.activity_allcategory
    }

    override fun initView() {
        Common.getCurrentLanguage(this@AllCategoryActivity, false)
        if(SharePreference.getStringPref(this@AllCategoryActivity, SharePreference.SELECTED_LANGUAGE).equals(resources.getString(R.string.language_hindi))){
            ivBack?.rotation= 180F

        }else{
            ivBack?.rotation= 0F

        }
        ivBack.setOnClickListener {
            finish()
        }
        if (isCheckNetwork(this@AllCategoryActivity)) {
            callApiCategoryFood()
        } else {
            Common.alertErrorOrValidationDialog(this@AllCategoryActivity, resources.getString(R.string.no_internet))
        }
    }


    //:::::::::::::Category Api:::::::::::::://
    private fun callApiCategoryFood() {
        Common.showLoadingProgress(this@AllCategoryActivity)
        val call = ApiClient.getClient.getCategory()
        call.enqueue(object : Callback<ListResponse<CategoryItemModel>> {
            override fun onResponse(
                call: Call<ListResponse<CategoryItemModel>>,
                response: Response<ListResponse<CategoryItemModel>>
            ) {
                if (response.code() == 200) {
                    val restResponce: ListResponse<CategoryItemModel> = response.body()!!
                    if (restResponce.status.toString().equals("1")) {
                        Common.dismissLoadingProgress()
                        if (restResponce.data!!.size > 0) {
                            rvCategory.visibility = View.VISIBLE
                            tvNoDataFound.visibility = View.GONE
                            val foodCategoryList = restResponce.data
                            setFoodCategoryAdaptor(foodCategoryList)
                        } else {
                            rvCategory.visibility = View.GONE
                            tvNoDataFound.visibility = View.VISIBLE
                        }
                    } else if (restResponce.status.toString().equals("0")) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@AllCategoryActivity,
                            restResponce.message
                        )
                        rvCategory.visibility = View.GONE
                        tvNoDataFound.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<ListResponse<CategoryItemModel>>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@AllCategoryActivity,
                    resources.getString(R.string.error_msg)
                )
                rvCategory.visibility = View.GONE
                tvNoDataFound.visibility = View.VISIBLE
            }
        })
    }

    fun setFoodCategoryAdaptor(foodCategoryList: ArrayList<CategoryItemModel>) {
        val foodCategoryAdapter = object : BaseAdaptor<CategoryItemModel>(this@AllCategoryActivity, foodCategoryList) {
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: CategoryItemModel,
                    position: Int
                ) {
                    val tvFoodCategoryName: TextView =
                        holder!!.itemView.findViewById(R.id.tvFoodCategoryName)
                    val ivFoodCategory: ImageView =
                        holder.itemView.findViewById(R.id.ivFoodCategory)
                    val cvCart: CardView = holder.itemView.findViewById(R.id.cvCart)

                    val i = position % 5

                    if (i == 0) {
                        cvCart.backgroundTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.color1,
                                null
                            )
                        )
                    } else if (i == 1) {
                        cvCart.backgroundTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.color2,
                                null
                            )
                        )
                    } else if (i == 2) {
                        cvCart.backgroundTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.color3,
                                null
                            )
                        )
                    } else if (i == 3) {
                        cvCart.backgroundTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.color4,
                                null
                            )
                        )
                    } else if (i == 4) {
                        cvCart.backgroundTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.color5,
                                null
                            )
                        )
                    }

                    tvFoodCategoryName.text = foodCategoryList.get(position).categoryName
                    Glide.with(this@AllCategoryActivity).load(foodCategoryList.get(position).image)
                        .into(ivFoodCategory)

                    holder.itemView.setOnClickListener {
                        startActivity(
                            Intent(
                                this@AllCategoryActivity,
                                CategoryByProductActivity::class.java
                            ).putExtra("cat_id", foodCategoryList[position].id.toString())
                                .putExtra("cat_name", foodCategoryList[position].categoryName)
                        )
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_category
                }

            }

        rvCategory.adapter = foodCategoryAdapter
        rvCategory.layoutManager =
            GridLayoutManager(this@AllCategoryActivity, 3, GridLayoutManager.VERTICAL, false)
        rvCategory.itemAnimator = DefaultItemAnimator()
        rvCategory.isNestedScrollingEnabled = true

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@AllCategoryActivity, false)
    }

    override fun onPause() {
        super.onPause()
        Common.getCurrentLanguage(this@AllCategoryActivity, false)
    }

}