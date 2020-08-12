package com.sen.translatev

import android.app.Dialog
import android.view.View
import android.widget.Toast
import base.activity.BaseActivity
import com.cocosw.bottomsheet.BottomSheet
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet.BottomListSheetBuilder
import com.sen.translatev.databinding.ActHomeBinding


class HomeActivity : BaseActivity<ActHomeBinding>(), View.OnClickListener {
    val TAG_TAKE_PHOTO = 0
    val TAG_LOCAL_IMAGE = 1
    private var dialog :Dialog?=null
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

    override fun onPause() {
        super.onPause()
        if(dialog!=null &&dialog!!.isShowing){
            dialog?.dismiss()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tranlateImage -> {
//                go(this@HomeActivity, ImagePickerActivity::class.java)
                dialog =  BottomSheet.Builder(MainActivity@this, R.style.BottomSheet_StyleDialog).title("图片选择")
                    .sheet(R.menu.dialog_menu)
                    .listener { dialog, which ->
                        when (which) {
                            R.id.take_photo -> Toast.makeText(MainActivity@this, "拍照", Toast.LENGTH_SHORT)
                                .show()
                            R.id.loacl_image -> Toast.makeText(MainActivity@this, "本地图片", Toast.LENGTH_SHORT)
                                .show()

                        }
                    }.show()
            }
            R.id.pdfOption -> {


            }
        }
    }
}
