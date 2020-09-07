package com.sen.translatev

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.view.View
import base.activity.BaseActivity
import com.blankj.utilcode.util.ToastUtils
import com.cocosw.bottomsheet.BottomSheet
import com.sen.translatev.activity.ImagePickerActivity
import com.sen.translatev.databinding.ActHomeBinding
import imagepicker.MediaFile
import permission.SPermission
import utils.SenDto
import utils.go
import java.io.File


class HomeActivity : BaseActivity<ActHomeBinding>(), View.OnClickListener {
    val TAG_TAKE_PHOTO = 0
    val TAG_LOCAL_IMAGE = 1
    private var dialog: Dialog? = null
    private var cameraSavePath:File ?= null
    private var uri: Uri? = null
    private var photoPath:String? = null
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
        if (dialog != null && dialog!!.isShowing) {
            dialog?.dismiss()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tranlateImage -> {
//                go(this@HomeActivity, ImagePickerActivity::class.java)
                dialog = BottomSheet.Builder(MainActivity@ this, R.style.BottomSheet_StyleDialog)
                    .title("图片选择")
                    .sheet(R.menu.dialog_menu)
                    .listener { dialog, which ->
                        when (which) {
                            R.id.take_photo -> {
                                SPermission.getInstance().hasPermissions(
                                    HomeActivity@ this,
                                    SPermission.CAMERA,
                                    object : SPermission.OnPermissionListener {
                                        override fun onSuccess() {
                                            goCamera()

                                        }

                                        override fun onFaild() {
                                            ToastUtils.showShort("Camera is get onFaild")
                                        }

                                    })
                            }
                            R.id.loacl_image -> {
                                go(HomeActivity@this, ImagePickerActivity::class.java)
                            }

                        }
                    }.show()
            }
            R.id.pdfOption -> {


            }
        }
    }

    private fun goCamera() {

        cameraSavePath  = File(
            Environment.getExternalStorageDirectory().absolutePath
                .toString() + "/" + System.currentTimeMillis() + ".jpg"
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //第二个参数为 包名.fileprovider
            uri = FileProvider.getUriForFile(
                HomeActivity@ this,
                "com.sen.translatev.fileProvider",
                cameraSavePath!!
            )
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            uri = Uri.fromFile(this.cameraSavePath)
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            photoPath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                cameraSavePath?.absolutePath
            } else {
                uri?.encodedPath
            }
            var mediaFile = MediaFile()
            mediaFile.path = photoPath.orEmpty()
            go(HomeActivity@this,TranlateActivity::class.java,dto= SenDto(list = arrayListOf(mediaFile)))
        }
    }


}
