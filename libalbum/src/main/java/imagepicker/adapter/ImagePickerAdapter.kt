package imagepicker.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sen.libalbum.R
import imagepicker.ITEM_TYPE_CAMERA
import imagepicker.ITEM_TYPE_IMAGE
import imagepicker.ITEM_TYPE_VIDEO
import imagepicker.MediaFile
import imagepicker.manager.ConfigManager
import imagepicker.manager.SelectionManager
import imagepicker.utils.MediaFileUtil
import imagepicker.utils.Utils
import imagepicker.view.SquareImageView
import imagepicker.view.SquareRelativeLayout
import java.io.File

/**
 * 列表适配器
 * Create by: chenWei.li
 * Date: 2018/8/23
 * Time: 上午1:18
 * Email: lichenwei.me@foxmail.com
 */
class ImagePickerAdapter(private val mContext: Context, private val mMediaFileList: MutableList<MediaFile>?,private val mSeletedManager:SelectionManager) :
    RecyclerView.Adapter<ImagePickerAdapter.BaseHolder>() {
    private val isShowCamera: Boolean = ConfigManager.instance.isShowCamera


    /**
     * 接口回调，将点击事件向外抛
     */
    private var mOnItemClickListener: OnItemClickListener? = null


    override fun getItemViewType(position: Int): Int {
        var position = position
        if (isShowCamera) {
            if (position == 0) {
                return ITEM_TYPE_CAMERA
            }
            //如果有相机存在，position位置需要-1
            position--
        }
        return if (MediaFileUtil.isVideoFileType(mMediaFileList!![position].path )) {
            ITEM_TYPE_VIDEO
        } else {
            ITEM_TYPE_IMAGE
        }
    }

    override fun getItemCount(): Int {
        if (mMediaFileList == null) {
            return 0
        }
        return if (isShowCamera) mMediaFileList.size + 1 else mMediaFileList.size
    }

    fun showVideoMask() {
//        if (SelectionManager.Companion.getInstance().selectSize() > 0 && !isShowVideoMask) {
//            isShowVideoMask = true
//            notifyDataSetChanged()
//        } else if (SelectionManager.Companion.getInstance().selectSize() === 0 && isShowVideoMask) {
//            isShowVideoMask = false
//            notifyDataSetChanged()
//        }
    }

    /**
     * 获取item所对应的数据源
     *
     * @param position
     * @return
     */
    fun getMediaFile(position: Int): MediaFile? {
        return if (isShowCamera) {
            if (position == 0) {
                null
            } else mMediaFileList!![position - 1]
        } else mMediaFileList!![position]
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        val view: View
        if (viewType == ITEM_TYPE_CAMERA) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview_camera, null)
            return CameraHolder(view)
        }
        if (viewType == ITEM_TYPE_IMAGE) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview_image, null)
            return ImageHolder(view)
        }
        if (viewType == ITEM_TYPE_VIDEO) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview_video, null)
            return VideoHolder(view)
        }
        throw ExceptionInInitializerError("")
    }


    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        val itemType = getItemViewType(position)
        val mediaFile = getMediaFile(position)
        when (itemType) {
            //图片、视频Item
            ITEM_TYPE_IMAGE, ITEM_TYPE_VIDEO -> {
                val mediaHolder = holder as MediaHolder
                bindMedia(mediaHolder, mediaFile!!, position)
            }
            //相机Item
            else -> {
                val mediaHolder = holder as CameraHolder
                if (ConfigManager.instance.cameraPageType == ConfigManager.CAMERA_PAGE_PICTURE) {
                    mediaHolder.mImageView.setImageResource(R.drawable.ic_camera)
                } else {
                    mediaHolder.mImageView.setImageResource(R.drawable.ic_video)
                }
            }
        }
        //设置点击事件监听
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener { view -> mOnItemClickListener?.onMediaCheck(view, position) }
        }
    }

    override fun onBindViewHolder(mediaHolder: BaseHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(mediaHolder, position, payloads)
        if(payloads.isEmpty()){
            onBindViewHolder(mediaHolder, position)
        }else{
            val mediaFile = getMediaFile(position)
            if (mediaHolder is ImageHolder) {
                var seleted = mSeletedManager.isImageSelect(mediaFile?.path.orEmpty(), position = position)
                if (seleted >= 0) {
                    mediaHolder.mImageCheck?.visibility = View.GONE
                    mediaHolder.imgeSeletedBg?.visibility = View.VISIBLE
//                    mediaHolder.indexView?.visibility = View.VISIBLE
//                    mediaHolder.indexView?.text = seleted.toString()
                } else {
                    mediaHolder.mImageCheck?.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_check_bg))
                    mediaHolder.imgeSeletedBg?.visibility = View.GONE
                    mediaHolder.mImageCheck?.visibility = View.VISIBLE
//                    mediaHolder.indexView?.visibility = View.GONE
                }
            }

            if (mediaHolder is VideoHolder) {
                var seleted = mSeletedManager.isImageSelect(mediaFile?.path.orEmpty(), position = position)
                //如果是视频，需要显示视频时长
                if (seleted > 0) {
                    mediaHolder.mImageCheck?.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_checked_bg_imge))
                    mediaHolder.imgeSeletedBg?.visibility = View.VISIBLE
                } else {

                    mediaHolder.mImageCheck?.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_check_bg))
                    mediaHolder.imgeSeletedBg?.visibility = View.GONE
                }
            }
        }
    }


    /**
     * 绑定数据（图片、视频）
     *
     * @param mediaHolder
     * @param mediaFile
     */
    private fun bindMedia(mediaHolder: MediaHolder, mediaFile: MediaFile, position: Int) {

        val imagePath = mediaFile.path
        //选择状态（仅是UI表现，真正数据交给SelectionManager管理）
        if (mediaHolder is ImageHolder) {
            var seleted = mSeletedManager.isImageSelect(imagePath.orEmpty(), position = position)
            if (seleted >= 0) {
                mediaHolder.mImageCheck?.visibility = View.GONE
                mediaHolder.imgeSeletedBg?.visibility = View.VISIBLE
//                mediaHolder.indexView?.visibility = View.VISIBLE
//                mediaHolder.indexView?.text = seleted.toString()
            } else {
                mediaHolder.mImageCheck?.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_check_bg))
                mediaHolder.imgeSeletedBg?.visibility = View.GONE
                mediaHolder.mImageCheck?.visibility = View.VISIBLE
//                mediaHolder.indexView?.visibility = View.GONE
            }
        }
        ConfigManager.instance.imageLoader?.loadImage(mediaHolder.mImageView, imagePath.orEmpty())

        if (mediaHolder is VideoHolder) {
            var seleted = mSeletedManager.isImageSelect(imagePath.orEmpty(), position = position)
            //如果是视频，需要显示视频时长
            val duration = Utils.getVideoDuration(mediaFile.duration)
            mediaHolder.mVideoDuration.text = duration
            if (seleted > 0) {
                mediaHolder.mImageCheck?.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_checked_bg_imge))
                mediaHolder.imgeSeletedBg?.visibility = View.VISIBLE
            } else {
                mediaHolder.mImageCheck?.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_check_bg))
                mediaHolder.imgeSeletedBg?.visibility = View.GONE
            }
        }

    }

    fun addItem(imagePath: String?) {
        if (!TextUtils.isEmpty(imagePath) && File(imagePath).exists()) {
            val mediaFile = MediaFile()
            mediaFile.path = imagePath
            mMediaFileList?.add(0, mediaFile)
            notifyItemChanged(0)
        }
    }
    fun addItem(imagePath: String?,duration:Long) {
        if (!TextUtils.isEmpty(imagePath) && File(imagePath).exists()) {
            val mediaFile = MediaFile()
            mediaFile.path = imagePath
            mediaFile.duration = duration
            mMediaFileList?.add(0, mediaFile)
            notifyItemChanged(0)
        }
    }

    /**
     * 图片Item
     */
    internal inner class ImageHolder(itemView: View) : MediaHolder(itemView) {
//        var indexView: TextView? = itemView.findViewById(R.id.indexView)
    }

    /**
     * 视频Item
     */
    internal inner class VideoHolder(itemView: View) : MediaHolder(itemView) {
        val mVideoDuration: TextView = itemView.findViewById(R.id.tv_item_videoDuration)
    }

    internal open inner class CameraHolder(itemView: View) : BaseHolder(itemView) {
        var mImageView: SquareImageView = itemView.findViewById(R.id.iv_item_image)
    }

    /**
     * 媒体Item
     */
    internal open inner class MediaHolder(itemView: View) : BaseHolder(itemView) {
        var mImageView: SquareImageView = itemView.findViewById(R.id.iv_item_image)
        var mImageCheck: ImageView? = itemView.findViewById(R.id.iv_item_check)
        var imgeSeletedBg: ImageView? = itemView.findViewById(R.id.imgBg)
    }

    /**
     * 基础Item
     */
    open inner class BaseHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mSquareRelativeLayout: SquareRelativeLayout = itemView.findViewById(R.id.srl_item)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {

        fun onMediaCheck(view: View, position: Int)
    }
}
