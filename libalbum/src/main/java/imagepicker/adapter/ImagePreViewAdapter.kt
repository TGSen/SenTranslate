package imagepicker.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import imagepicker.MediaFile
import imagepicker.view.PinchImageView
import java.util.*


class ImagePreViewAdapter(private val mContext: Context, private var mMediaFileList: List<MediaFile>) : PagerAdapter() {
    private var viewCache = LinkedList<PinchImageView>()
    private var clickLinstener: ItemClickLinstener? = null
    override fun getCount(): Int {
        return mMediaFileList?.size ?: 0
    }

    interface ItemClickLinstener {
        fun onClick()
    }

    fun setOnClickListener(listener: ItemClickLinstener) {
        this.clickLinstener = listener
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    var imageViewLinstener = View.OnClickListener {
        clickLinstener?.onClick()
    }
    fun removeItem(position: Int){
        notifyDataSetChanged()
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView: PinchImageView
        if (viewCache.size > 0) {
            imageView = viewCache.remove()
            imageView.reset()
        } else {
            imageView = PinchImageView(mContext)
        }
        imageView.canDouableDownScale(false)
        imageView.setOnClickListener(imageViewLinstener)
        try {
            val mediaFile = mMediaFileList?.get(position)
            if (TextUtils.isEmpty(mediaFile?.cutPath)) {
                setNoCacheImage(imageView, mediaFile.path.orEmpty())
//                instance.imageLoader?.loadPreImage(imageView, mediaFile?.path.orEmpty())
            } else {
                setNoCacheImage(imageView, mediaFile.cutPath.orEmpty())
//                instance.imageLoader?.loadPreImage(imageView, mediaFile?.cutPath.orEmpty())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        container.addView(imageView)
        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val imageView = `object` as PinchImageView
        container.removeView(imageView)
        viewCache.add(imageView)
    }

    fun change(position: Int, url: String?) {
        mMediaFileList[position].cutPath = url
        notifyDataSetChanged()
    }

    fun change(position: Int, mediaFile: MediaFile) {
        mediaFile?.let {
            //            mMediaFileList.get(position)= mediaFile //不知道为啥报错，算了赋值吧
            var item = mMediaFileList?.get(position)
            item = mediaFile
            notifyDataSetChanged()
        }

    }

    //解决数据不刷新的问题
    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    fun reset(currentItem: Int) {
        if (mMediaFileList!![currentItem].cutPath == null) return
        mMediaFileList[currentItem].cutPath = null
        notifyDataSetChanged()
    }

    fun setNoCacheImage(iv: ImageView, url: String?) {
        Glide.with(iv.context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.NONE)//禁用磁盘缓存
            .skipMemoryCache(true)//跳过内存缓存
            .into(iv)
    }

}