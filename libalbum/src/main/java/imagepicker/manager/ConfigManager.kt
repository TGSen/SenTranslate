package imagepicker.manager


import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import image.load.ImageLoader
import imagepicker.MediaFile
import java.util.*

/**
 * 统一配置管理类
 * Create by: chenWei.li
 * Date: 2019/1/23
 * Time: 10:32 AM
 * Email: lichenwei.me@foxmail.com
 */
class ConfigManager private constructor() {

    var title: String? = null//标题
    var isShowCamera: Boolean = false//是否显示拍照Item，默认不显示
    var isShowImage = true//是否显示图片，默认显示
    var isShowVideo = true//是否显示视频，默认显示
    var selectionMode = SELECT_MODE_SINGLE//选择模式，默认单选
    var maxCount = 1
        set(maxCount) {
            if (maxCount > 1) {
                selectionMode = SELECT_MODE_MULTI
            }
            field = maxCount
        }//最大选择数量，默认为1
    var isSingleType: Boolean = false//是否只支持选单类型（图片或者视频）
    var imagePaths: ArrayList<MediaFile>? = null//上一次选择的图片地址集合

    //camera 页面
    var cameraPageType: Int = CAMERA_PAGE_PICTURE

    var goToPageType: Int = GO_TO_LIST_TRIM
    //裁剪
    var cutShapType: Int = SHAPE_TYPE_RECTANGLE
    //裁剪比例
    var cutRatioMode = CUT_RATIO_MODE_FREE


    var isCutAffterShow = false
    //是否cut 就返回，这个直接是去裁剪页面

    var isCutReturn = false

    //设置左边的裁剪参数，
    var ratioX: Int = 1
        set(value) {
            if (value <= 0) {
                field = 1
            }
            field = value
        }
    var ratioY: Int = 1
        set(value) {
            if (value <= 0) {
                field = 1
            }
            field = value
        }


    //从一级页面，还是二级页面
    var from: Int = FROM_FRIST_PAGE

    //需要跳转的page fragment 或者activity
    var pageFragment: Class<out Fragment>? = null
    var pageActivity: Class<out AppCompatActivity>? = null
    //裁剪页面的设置
    var cutPageType = CUT_PAGE_OPTION_ALL

    fun reset() {
        title = null//标题
        isShowCamera = false//是否显示拍照Item，默认不显示
        isShowImage = true//是否显示图片，默认显示
        isShowVideo = true//是否显示视频，默认显示
        selectionMode = SELECT_MODE_SINGLE//选择模式，默认单选
//        maxCount = 1
        isSingleType = false//是否只支持选单类型（图片或者视频）
        imagePaths = null//上一次选择的图片地址集合

        //camera 页面
        cameraPageType = CAMERA_PAGE_PICTURE
        //裁剪
        cutShapType = SHAPE_TYPE_RECTANGLE
        //裁剪比例
        cutRatioMode = CUT_RATIO_MODE_FREE


        isCutAffterShow = false
        //是否cut 就返回，这个直接是去裁剪页面

        isCutReturn = false

        //设置左边的裁剪参数，
        ratioX = 1

        ratioY = 1


        //从一级页面，还是二级页面
        from = FROM_FRIST_PAGE

        //需要跳转的page fragment 或者activity
        pageFragment = null
        pageActivity = null
        //裁剪页面的设置
        cutPageType = CUT_PAGE_OPTION_ALL
    }

    var imageLoader: ImageLoader? = null
        @Throws(Exception::class)
        get() {
            if (field == null) {
                throw Exception("imageLoader is null")
            }
            return field
        }

    companion object {

        const val SELECT_MODE_SINGLE = 0
        const val SELECT_MODE_MULTI = 1
        //摄像机页面参数
        const val CAMERA_PAGE_VIDEO_PICTURE: Int = 0
        const val CAMERA_PAGE_VIDEO: Int = 1

        const val GO_TO_LIST_TRIM: Int = 0
        const val GO_TO_SIGLE_TRIM: Int = 1
        const val GO_TO_HEAD_TRIM: Int = 2

        const val CAMERA_PAGE_PICTURE: Int = 2
        //裁剪形状
        const val SHAPE_TYPE_RECTANGLE: Int = 0
        const val SHAPE_TYPE_CIRCLE: Int = 1
        //如果是0 ，那么需要跳转配置好的page
        const val FROM_FRIST_PAGE: Int = 0
        const val FROM_SECOND_PAGE: Int = 1

        //裁剪模式
        const val CUT_RATIO_MODE_FREE: Int = 0
        const val CUT_RATIO_MODE_FIXED: Int = 1


        //裁剪页面参数
        const val CUT_PAGE_OPTION_ALL: Int = 0  //该页面该有啥就有啥，以帖子为准
        const val CUT_PAGE_OPTION_NONE: Int = 1 //该页面只留普通页面操作
        const val CUT_PAGE_OPTION_CUT: Int = 2 //基本操作和裁剪的操作

        //

        @Volatile
        private var mConfigManager: ConfigManager? = null

        val instance: ConfigManager
            get() {
                if (mConfigManager == null) {
                    synchronized(SelectionManager::class.java) {
                        if (mConfigManager == null) {
                            mConfigManager = ConfigManager()
                        }
                    }
                }
                return mConfigManager!!
            }
    }
}

