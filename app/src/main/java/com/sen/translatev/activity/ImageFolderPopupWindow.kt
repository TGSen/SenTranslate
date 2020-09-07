package com.sen.translatev.activity

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.PopupWindow
import com.blankj.utilcode.util.ScreenUtils
import com.sen.translatev.R

import imagepicker.MediaFolder
import imagepicker.adapter.ImageFoldersAdapter

/**
 * 图片文件夹列表窗口
 * Create by: chenWei.li
 * Date: 2018/8/25
 * Time: 上午1:30
 * Email: lichenwei.me@foxmail.com
 */
class ImageFolderPopupWindow(private val mContext: Context, private val mMediaFolderList: List<MediaFolder>) : PopupWindow() {

    private var mRecyclerView: RecyclerView? = null
    var adapter: ImageFoldersAdapter? = null
        private set

    init {
        initView()

    }

    /**
     * 初始化布局
     */
    private fun initView() {
        val view = LayoutInflater.from(mContext).inflate(R.layout.window_image_folders, null)
        initPopupWindow(view)
        mRecyclerView = view.findViewById(R.id.rv_main_imageFolders)
        mRecyclerView?.layoutManager = LinearLayoutManager(mContext)
        adapter = ImageFoldersAdapter(mContext, mMediaFolderList,
            DEFAULT_IMAGE_FOLDER_SELECT
        )
        mRecyclerView?.adapter = adapter


    }

    fun setSelectNum(count :Int){
        adapter?.setSelectNum(count)
    }

    /**
     * 初始化PopupWindow的一些属性
     */
    private fun initPopupWindow(view: View) {
        contentView = view
        width = ScreenUtils.getScreenWidth()

        height = (ScreenUtils.getScreenHeight() * 0.6).toInt()
        setBackgroundDrawable(ColorDrawable(0xff0000))
        animationStyle = R.style.BottomPopAnimation
        isOutsideTouchable = true
        isFocusable = true
        setTouchInterceptor { view1, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_OUTSIDE) {
                dismiss()
            }
            false
        }
    }

    companion object {

        private val DEFAULT_IMAGE_FOLDER_SELECT = 0//默认选中文件夹
    }

}
