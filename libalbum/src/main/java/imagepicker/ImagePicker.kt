package imagepicker

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import image.load.ImageLoader
import imagepicker.manager.ConfigManager

/**
 * 统一调用入口
 * Create by: chenWei.li
 * Date: 2018/8/26
 * Time: 下午6:31
 * Email: lichenwei.me@foxmail.com
 */
class ImagePicker private constructor() {


    /**
     * 设置标题
     *
     * @param title
     * @return
     */
    fun setTitle(title: String): ImagePicker {
        ConfigManager.instance.title = title
        return instance
    }

    /**
     * 是否支持相机
     *
     * @param showCamera
     * @return
     */
    fun showCamera(showCamera: Boolean): ImagePicker {
        ConfigManager.instance.isShowCamera = showCamera
        return instance
    }

    /**
     * 是否展示图片
     *
     * @param showImage
     * @return
     */
    fun showImage(showImage: Boolean): ImagePicker {
        ConfigManager.instance.isShowImage = showImage
        return instance
    }

    /**
     * 是否展示视频
     *
     * @param showVideo
     * @return
     */
    fun showVideo(showVideo: Boolean): ImagePicker {
        ConfigManager.instance.isShowVideo = showVideo
        return instance
    }


    /**
     * 图片最大选择数
     *
     * @param maxCount
     * @return
     */
    fun setMaxCount(maxCount: Int): ImagePicker {
        ConfigManager.instance.maxCount = maxCount
        return instance
    }

    /**
     * 设置单类型选择（只能选图片或者视频）
     *
     * @param isSingleType
     * @return
     */
    fun setSingleType(isSingleType: Boolean): ImagePicker {
        ConfigManager.instance.isSingleType = isSingleType
        return instance
    }


    /**
     * 设置图片加载器
     *
     * @param imageLoader
     * @return
     */
    fun setImageLoader(imageLoader: ImageLoader): ImagePicker {
        ConfigManager.instance.imageLoader = imageLoader
        return instance
    }

    private fun setCameraPageType(cameraPageType: Int): ImagePicker {
        ConfigManager.instance.cameraPageType = cameraPageType
        return instance
    }

    private fun setGoToPageType(GoPageType: Int): ImagePicker {
        ConfigManager.instance.goToPageType = GoPageType
        return instance
    }







    /**
     * 设置是否裁剪才显示
     *
     * @param is
     * @return
     */
    fun setCutAffterShow(isAffter: Boolean): ImagePicker {
        ConfigManager.instance.isCutAffterShow = isAffter
        return instance
    }

    fun resetOption() {
        ConfigManager.instance.reset()
    }

    /**
     * 设置裁剪的形状
     */

    fun setCutShapType(cutShapType: Int): ImagePicker {
        ConfigManager.instance.cutShapType = cutShapType
        return instance
    }

    /**
     * 设置裁剪是否直接返回，
     * 这个方法调用后，直接去裁剪页面，根据如果是圆形就是去圆形的页面
     *
     * @return
     */
    fun setCutReturn(isReturn: Boolean): ImagePicker {
        ConfigManager.instance.isCutReturn = isReturn
        return instance
    }


    /**
     * 如true 则需要启动
     *
     * @param isOut
     */
    fun setOutSideFrom(isOut: Boolean): ImagePicker {
        if (!isOut) {
            ConfigManager.instance.from = ConfigManager.FROM_SECOND_PAGE
        }

        return instance
    }


    fun setGoPageFragment(page: Class<out Fragment>): ImagePicker {
        ConfigManager.instance.pageFragment = page
        return instance
    }

    fun setGoPageActivity(page: Class<out AppCompatActivity>): ImagePicker {
        ConfigManager.instance.pageActivity = page
        return instance
    }

    fun setCutPageType(type: Int): ImagePicker {
        ConfigManager.instance.cutPageType = type
        return instance
    }

    fun setCutRatioXY(ratioX: Int, ratioY: Int): ImagePicker {
        ConfigManager.instance.ratioX = ratioX
        ConfigManager.instance.ratioY = ratioY
        return instance
    }


//    /**
//     * 启动
//     *
//     * @param activity
//     */
//    fun start(activity: Activity) {
//        val intent = Intent(activity, ImagePickerFragment::class.java)
//        activity.startActivity(intent)
//    }


    fun setCutMode(mode: Int): ImagePicker {
        ConfigManager.instance.cutRatioMode = mode
        return instance
    }


    /**
     * 启动发帖子页面
     */
    fun luanchPostOption(
        pageFragment: Class<out Fragment>? = null,
        pageActivity: Class<out AppCompatActivity>? = null,
        outSideFrom: Boolean = true,
        count: Int = 9
    ) {
        instance.showImage(true)//设置是否展示图片
            .showVideo(true)//设置是否展示视频
            .setMaxCount(count)//设置最大选择图片数目(默认为1，单选)
            .setSingleType(true)//设置图片视频不能同时选择
            .setCameraPageType(ConfigManager.CAMERA_PAGE_VIDEO_PICTURE)
            .setOutSideFrom(outSideFrom)
        pageFragment?.let {
            instance.setGoPageFragment(pageFragment!!)
        }
        pageActivity?.let {
            instance.setGoPageActivity(pageActivity!!)
        }

    }


    /**
     * 获取多张图片，按比例的图片
     * 这个是宠物照片使用的
     */
    fun luanchMutilRatioPictureOption(
        pageFragment: Class<out Fragment>? = null,
        pageActivity: Class<out AppCompatActivity>? = null,
        ratioX: Int,
        ratioY: Int,
        outSideFrom: Boolean = true,
        count: Int = 9,
        isCutReturn: Boolean = false
    ) {
        instance.showImage(true)//设置是否展示图片
            .showVideo(false)//设置是否展示视频
            .setMaxCount(count)//设置最大选择图片数目(默认为1，单选)
            .setSingleType(true)//设置图片视频不能同时选择
            .setCameraPageType(ConfigManager.CAMERA_PAGE_PICTURE)
            .setCutAffterShow(true)
            .setCutReturn(isCutReturn)
            .setCutMode(ConfigManager.CUT_RATIO_MODE_FIXED)
            .setCutPageType(ConfigManager.CUT_PAGE_OPTION_CUT)
            .setOutSideFrom(outSideFrom)
            .setCutRatioXY(ratioX, ratioY)
        pageFragment?.let {
            instance.setGoPageFragment(pageFragment!!)
        }
        pageActivity?.let {
            instance.setGoPageActivity(pageActivity!!)
        }
    }

    /**
     * 获取多张图片，按比例的图片
     * 这个是宠物照片使用的
     */
    fun luanchPictureOption(count: Int = 9) {
        instance.resetOption()
        instance.showImage(true)//设置是否展示图片
            .showVideo(false)//设置是否展示视频
            .setMaxCount(count)//设置最大选择图片数目(默认为1，单选)
            .setSingleType(true)//设置图片视频不能同时选择
            .setCameraPageType(ConfigManager.CAMERA_PAGE_PICTURE)
            .setGoToPageType(ConfigManager.GO_TO_LIST_TRIM)
    }


    /**
     * 启动获取头像
     * outSideFrom: true-需要跳转对应页面  false-不需要跳
     */
    fun launchHeadPictureOption() {
        instance.resetOption()
        instance.showImage(true)//设置是否展示图片
            .showVideo(false)//设置是否展示视频
            .setMaxCount(1)//设置最大选择图片数目(默认为1，单选)
            .setSingleType(true)//设置图片视频不能同时选择
            .setCameraPageType(ConfigManager.CAMERA_PAGE_PICTURE)
            .setGoToPageType(ConfigManager.GO_TO_HEAD_TRIM)
    }

    /**
     * 获取1张图片，进入预览，比如宠物证书
     */
    fun luanchOnePictureOption(
        pageFragment: Class<out Fragment>? = null,
        pageActivity: Class<out AppCompatActivity>? = null,
        outSideFrom: Boolean = true
    ) {
        instance.showImage(true)//设置是否展示图片
            .showVideo(false)//设置是否展示视频
            .setMaxCount(1)//设置最大选择图片数目(默认为1，单选)
            .setSingleType(true)//设置图片视频不能同时选择
            .setCameraPageType(ConfigManager.CAMERA_PAGE_PICTURE)
            .setCutAffterShow(false)
            .setOutSideFrom(outSideFrom)
            .setCutPageType(ConfigManager.CUT_PAGE_OPTION_NONE)
        pageFragment?.let {
            instance.setGoPageFragment(pageFragment!!)
        }
        pageActivity?.let {
            instance.setGoPageActivity(pageActivity!!)
        }

    }

    /**
     * 只有视频
     */
    fun luanchVideoOption() {
        instance.resetOption()
        instance.showImage(false)//设置是否展示图片
            .showVideo(true)//设置是否展示视频
            .setMaxCount(1)//设置最大选择图片数目(默认为1，单选)
            .setSingleType(true)//设置图片视频不能同时选择
            .setCameraPageType(ConfigManager.CAMERA_PAGE_VIDEO)

    }


    companion object {

        val EXTRA_SELECT_IMAGES = "selectItems"

        @Volatile
        private var mImagePicker: ImagePicker? = null

        /**
         * 创建对象
         *
         * @return
         */
        val instance: ImagePicker
            get() {
                if (mImagePicker == null) {
                    synchronized(ImagePicker::class.java) {
                        if (mImagePicker == null) {
                            mImagePicker = ImagePicker()
                        }
                    }
                }
                return mImagePicker!!
            }
    }

}
