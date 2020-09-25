package com.sen.translatev.activity

import android.view.View
import base.activity.BaseActivity
import com.sen.translatev.R
import com.sen.translatev.databinding.ActPdfBinding
import com.sen.translatev.utils.PdfConvertToBitmap

class PdfActivity : BaseActivity<ActPdfBinding>(), View.OnClickListener  {
    override fun initView() {
        binding.image.setImageBitmap(PdfConvertToBitmap.initBitmap(this))
    }

    override fun setLayoutId(): Int {
        return R.layout.act_pdf
    }

    override fun onClick(p0: View?) {

    }
}