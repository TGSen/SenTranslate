package com.sen.translatev

import android.view.View
import base.activity.BaseActivity
import com.sen.translatev.databinding.ActHomeBinding
import utils.go

class HomeActivity  : BaseActivity<ActHomeBinding>(), View.OnClickListener {
    override fun setLayoutId(): Int {
        return R.layout.act_home
    }


    override fun initView() {

        binding.apply {
            tranlateImage.setOnClickListener(this@HomeActivity)
            pdfOption.setOnClickListener(this@HomeActivity)
//            test.setOnClickListener(this@MainActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tranlateImage -> {
//                go(this@HomeActivity, ImagePickerActivity::class.java)
            }
            R.id.pdfOption -> {
//                go(this@MainActivity, LocalVideoActivity::class.java)

            }
        }
    }
}
