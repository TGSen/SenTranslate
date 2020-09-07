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

import imagepicker.MediaFolder
import imagepicker.manager.ConfigManager

/**
 * 图片文件夹列表适配器
 * Create by: chenWei.li
 * Date: 2018/8/25
 * Time: 上午1:36
 * Email: lichenwei.me@foxmail.com
 */
class ImageFoldersAdapter(
    private val mContext: Context,
    private val mMediaFolderList: List<MediaFolder>?,
    private var mCurrentImageFolderIndex: Int
) : RecyclerView.Adapter<ImageFoldersAdapter.ViewHolder>() {

    private var seletedNum = 0

    /**
     * 接口回调，Item点击事件
     */
    private var mImageFolderChangeListener: OnImageFolderChangeListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview_folder, null))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mediaFolder = mMediaFolderList?.get(position)
        val folderCover = mediaFolder?.folderCover
        val folderName = mediaFolder?.folderName
        val imageSize = mediaFolder?.mediaFileList?.size

        if (!TextUtils.isEmpty(folderName)) {
            holder.mFolderName.text = folderName
        }

        holder.mImageSize.text = String.format(mContext.getString(R.string.folder_num), imageSize.toString())
        holder.selectedNum.visibility = if (position == 0 && seletedNum > 0) View.VISIBLE else View.GONE
        holder.selectedNum.text = seletedNum.toString()

//        //加载图片
        ConfigManager.instance.imageLoader?.loadImage(holder.mImageCover, folderCover.orEmpty())


        if (mImageFolderChangeListener != null) {
            holder.itemView.setOnClickListener { view ->
                mCurrentImageFolderIndex = position
                notifyDataSetChanged()
                mImageFolderChangeListener!!.onImageFolderChange(view, position)
            }
        }
        if (position == (mMediaFolderList?.size?.minus(1))) {
            holder.line.visibility = View.GONE
        } else {
            holder.line.visibility = View.VISIBLE
        }

    }

    override fun getItemCount(): Int {
        return mMediaFolderList?.size ?: 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val mImageCover: ImageView = itemView.findViewById(R.id.iv_item_imageCover)
        val mFolderName: TextView = itemView.findViewById(R.id.tv_item_folderName)
        val mImageSize: TextView = itemView.findViewById(R.id.tv_item_imageSize)
        val selectedNum: TextView = itemView.findViewById(R.id.selectedNum)
        val line: View = itemView.findViewById(R.id.line)

    }

    fun setOnImageFolderChangeListener(onItemClickListener: OnImageFolderChangeListener) {
        this.mImageFolderChangeListener = onItemClickListener
    }

    fun setSelectNum(count: Int) {
        this.seletedNum = count
        notifyItemChanged(0)
    }

    interface OnImageFolderChangeListener {
        fun onImageFolderChange(view: View, position: Int)
    }
}
