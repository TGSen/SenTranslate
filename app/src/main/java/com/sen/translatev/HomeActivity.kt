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
            createNew.setOnClickListener(this@HomeActivity)
            eidtVideo.setOnClickListener(this@HomeActivity)
//            test.setOnClickListener(this@MainActivity)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.createNew -> {
//                go(this@HomeActivity, ImagePickerActivity::class.java)
            }
            R.id.eidtVideo -> {
//                go(this@MainActivity, LocalVideoActivity::class.java)

            }
        }
    }
}
