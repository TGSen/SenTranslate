package com.sen.translatev.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.widget.Toast
import base.activity.BaseActivity
import com.blankj.utilcode.util.ToastUtils
import com.sen.translatev.R
import com.sen.translatev.databinding.ActivityImagepickerBinding
import com.yanzhenjie.permission.runtime.Permission
import image.load.GlideLoader
import imagepicker.*
import imagepicker.adapter.GridItemDecoration
import imagepicker.adapter.ImageFoldersAdapter
import imagepicker.adapter.ImagePickerAdapter
import imagepicker.manager.ConfigManager
import imagepicker.manager.SelectionManager
import imagepicker.task.CommonExecutor
import imagepicker.task.ImageLoadTask
import imagepicker.task.MediaLoadTask
import imagepicker.task.VideoLoadTask
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import permission.SPermission
import utils.SenDto
import utils.go
import utils.setOnSingleClickListener
import java.util.*

class ImagePickerActivity : BaseActivity<ActivityImagepickerBinding>(),
    ImagePickerAdapter.OnItemClickListener, ImageFoldersAdapter.OnImageFolderChangeListener,
    View.OnClickListener {
    override fun setLayoutId(): Int {
        return R.layout.activity_imagepicker
    }

    private val mSelectionManager: SelectionManager by lazy { SelectionManager() }

    /**
     * 启动参数
     */
    private var isShowCamera: Boolean = false
    private var isShowImage: Boolean = false
    private var isShowVideo: Boolean = false
    private var isSingleType: Boolean = false
    private var mMaxCount: Int = 0
    private var mImagePaths: ArrayList<MediaFile>? = null

    /**
     * 界面UI
     */


    private var mGridLayoutManager: GridLayoutManager? = null
    private var mImagePickerAdapter: ImagePickerAdapter? = null

    //图片数据源
    private var mMediaFileList: MutableList<MediaFile>? = null

    //文件夹数据源
    private var mMediaFolderList: List<MediaFolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
//        StatusBarUtil.immersive(this, Color.BLACK)
        initConfig(isAll = false, isImage = true, isVideo = true)
        getData()

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun OnMediaSeleted(item: OnMediaSeleted) {
        finish()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun OnAddPicture(item: OnAddPicture) {
        mImagePickerAdapter?.addItem(item.imagePath)
    }


    /**
     * 拍照相关
     */
    private var mFilePath: String? = null


    /**
     * 初始化配置
     */
    private fun initConfig(isAll: Boolean, isImage: Boolean, isVideo: Boolean) {
        ImagePicker.instance
            .showCamera(false)//设置是否显示拍照按钮
            .showImage(isImage)//设置是否展示图片
            .showVideo(isVideo)//设置是否展示视频
            .setMaxCount(1)//设置最大选择图片数目(默认为1，单选)
            .setSingleType(isAll)//设置图片视频不能同时选择
            .setImageLoader(GlideLoader())//设置自定义图片加载器

        isShowCamera = ConfigManager.instance.isShowCamera
        isShowImage = ConfigManager.instance.isShowImage
        isShowVideo = ConfigManager.instance.isShowVideo
        isSingleType = ConfigManager.instance.isSingleType
        mMaxCount = ConfigManager.instance.maxCount
        mSelectionManager.maxCount = mMaxCount

        //载入历史选择记录
        mImagePaths = ConfigManager.instance.imagePaths
        mImagePaths?.let {
            mSelectionManager.addImagePathsToSelectList(mImagePaths)
        }

    }


    /**
     * 初始化布局控件
     */
    override fun initView() {
        binding.apply {
            mGridLayoutManager = GridLayoutManager(this@ImagePickerActivity, 4)
            mRecyclerView.layoutManager = mGridLayoutManager
            mRecyclerView.addItemDecoration(GridItemDecoration(4, GridItemDecoration.GRIDLAYOUT))
            mRecyclerView.setHasFixedSize(true)
            mMediaFileList = ArrayList()
            mImagePickerAdapter =
                ImagePickerAdapter(this@ImagePickerActivity, mMediaFileList, mSelectionManager)
            mImagePickerAdapter?.setOnItemClickListener(this@ImagePickerActivity)
            mRecyclerView.adapter = mImagePickerAdapter
        }


    }

    override fun initLinstener() {
        super.initLinstener()
        binding.apply {

            imgClose.setOnSingleClickListener(this@ImagePickerActivity)

            imgNext.setOnSingleClickListener(this@ImagePickerActivity)


            mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                }
            })

        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgClose -> {
                if (ConfigManager.instance.from == ConfigManager.FROM_FRIST_PAGE) {
                    mSelectionManager.removeAll()
                }
                setResult(AppCompatActivity.RESULT_CANCELED)
                finish()
            }
            R.id.imgNext -> {
                commitSelection()
            }

        }
    }

    /**
     * 获取数据源
     */
    private fun getData() {
        SPermission.getInstance().hasPermissions(
            this,
            Permission.Group.STORAGE,
            object : SPermission.OnPermissionListener {
                override fun onSuccess() {
//                CameraPreviewActivity.goto(this@)
                    startScannerTask()
                }

                override fun onFaild() {
                    ToastUtils.showShort("Camera is get onFaild")
                }

            })


    }


    /**
     * 开启扫描任务
     */
    private fun startScannerTask() {
        var mediaLoadTask: Runnable? = null

        //照片、视频全部加载
        if (isShowImage && isShowVideo) {
            mediaLoadTask = MediaLoadTask(this, MediaLoader())
        }

        //只加载视频
        if (!isShowImage && isShowVideo) {
            mediaLoadTask = VideoLoadTask(this, MediaLoader())
        }

        //只加载图片
        if (isShowImage && !isShowVideo) {
            mediaLoadTask = ImageLoadTask(this, MediaLoader())
        }

        //不符合以上场景，采用照片、视频全部加载
        if (mediaLoadTask == null) {
            mediaLoadTask = MediaLoadTask(this, MediaLoader())
        }

        CommonExecutor.instance?.execute(mediaLoadTask)
    }


    /**
     * 处理媒体数据加载成功后的UI渲染
     */
    internal inner class MediaLoader : MediaLoadCallback {

        override fun loadMediaSuccess(mediaFolderList: List<MediaFolder>) {
            runOnUiThread {
                if (mediaFolderList.isNotEmpty()) {
                    //默认加载全部照片
                    mMediaFileList?.addAll(mediaFolderList[0].mediaFileList!!)
                    mImagePickerAdapter?.notifyDataSetChanged()

                    //图片文件夹数据
                    mMediaFolderList = ArrayList(mediaFolderList)
                    updateCommitButton()
                }
            }
        }
    }


    /**
     * 选中/取消选中图片
     *
     * @param view
     * @param position
     */
    override fun onMediaCheck(view: View, position: Int) {
        if (isShowCamera) {
            if (position == 0) {
                showCamera()
                return
            }
        }
        //执行选中/取消操作
        val mediaFile = mImagePickerAdapter?.getMediaFile(position)
        if (mediaFile != null) {
            mSelectionManager.addJustOneToSelectList(mediaFile, position)
            notifayItemChange()
        }
        updateCommitButton()
    }

    private fun notifayItemChange() {
        mSelectionManager.needDelectedPaths.forEach {
            mImagePickerAdapter?.notifyItemChanged(it.position,"payload")
        }
        mSelectionManager.selectPaths.forEach {
            mImagePickerAdapter?.notifyItemChanged(it.position,"payload")
        }
        mSelectionManager.needDelectedPaths.clear()
    }

    /**
     * 更新确认按钮状态
     */
    @SuppressLint("StringFormatInvalid")
    private fun updateCommitButton() {
        //改变确定按钮UI
        val selectCount = mSelectionManager.selectPaths.size
        if (selectCount == 0) {
            // binding.tvNext.isEnabled = false
//            binding.tvNext.text = getString(R.string.next)
            return
        } else {
            // binding.tvNext.isEnabled = true
            //binding.tvNext.text = String.format(getString(R.string.next_select_count), selectCount)
        }
        mImagePickerAdapter?.showVideoMask()

    }

    /**
     * 跳转相机拍照,目前需求：选了9张，如果是拍照，那么将最后那张替换，如果是拍视频，拍完后将已选的清除
     */
    private fun showCamera() {

        //    CameraPreviewActivity.gotoThis(this)
    }

    /**
     * 当图片文件夹切换时，刷新图片列表数据源
     *
     * @param view
     * @param position
     */
    override fun onImageFolderChange(view: View, position: Int) {

    }


    /**
     * 拍照回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
//            if (requestCode == REQUEST_CODE_CAPTURE) {
//                //通知媒体库刷新
//                sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + mFilePath!!)))
//                //添加到选中集合
//                mSelectionManager.addImageToSelectList(mFilePath)
//
//                val list = ArrayList(mSelectionManager.selectPaths)
//                val intent = Intent()
//                intent.putStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES, list)
//                setResult(Activity.RESULT_OK, intent)
//                mSelectionManager.removeAll()//清空选中记录
//                finish()
//            }

            if (requestCode == REQUEST_SELECT_IMAGES_CODE) {
                commitSelection()
            }
        }
    }

    /**
     * 选择图片完毕，返回
     */
    private fun commitSelection() {
//        val list = ArrayList(SelectionManager.getInstance().selectPaths)
//        val intent = Intent()
//        intent.putStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES, list)
//        setResult(Activity.RESULT_OK, intent)
//        SelectionManager.getInstance().removeAll()//清空选中记录
//        finish()
        val list = ArrayList(mSelectionManager.selectPaths)
        if (list.size > 0) {
            go(
                HomeActivity@ this,
                TranlateActivity::class.java, dto = SenDto(list = list)
            )
            finish()
        }
//        if (MediaFileUtil.isVideoFileType(list[0].path)) {
//            list[0]?.path?.let { CutVideoActivity.gotoThis(this@ImagePickerActivity, it) }
//        }else{
//            CutImageActivity.gotoThis(this@ImagePickerActivity)
//        }

    }


    override fun onResume() {
        super.onResume()
        mImagePickerAdapter?.notifyDataSetChanged()
        updateCommitButton()
    }

    override fun onBackPressed() {
        setResult(AppCompatActivity.RESULT_CANCELED)
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            ConfigManager.instance.imageLoader?.clearMemoryCache()
            // SelectionManager.getInstance().removeAll()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        EventBus.getDefault().unregister(this)
    }

    companion object {

        /**
         * 大图预览页相关
         */
        private const val REQUEST_SELECT_IMAGES_CODE = 0x01//用于在大图预览页中点击提交按钮标识
        private const val REQUEST_CODE_CAPTURE = 0x02//点击拍照标识

        /**
         * 权限相关
         */
        private const val REQUEST_PERMISSION_CAMERA_CODE = 0x03


    }

}
