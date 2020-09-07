package imagepicker.task

import android.content.Context
import imagepicker.MediaFile
import imagepicker.MediaLoadCallback
import imagepicker.loader.ImageScanner
import imagepicker.loader.MediaHandler
import java.util.*

/**
 * 媒体库扫描任务（图片）
 * Create by: chenWei.li
 * Date: 2018/8/25
 * Time: 下午12:31
 * Email: lichenwei.me@foxmail.com
 */
class ImageLoadTask(private val mContext: Context, private val mMediaLoadCallback: MediaLoadCallback?) : Runnable {
    private val mImageScanner: ImageScanner?

    init {
        mImageScanner = ImageScanner(mContext)
    }

    override fun run() {
        //存放所有照片
        var imageFileList = ArrayList<MediaFile>()

        if (mImageScanner != null) {
            imageFileList = mImageScanner.queryMedia()
        }

        mMediaLoadCallback?.loadMediaSuccess(MediaHandler.getImageFolder(mContext, imageFileList))


    }

}
