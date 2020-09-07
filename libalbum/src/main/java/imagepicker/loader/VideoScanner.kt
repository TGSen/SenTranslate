package imagepicker.loader

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import imagepicker.MediaFile
import imagepicker.VideoContant
import java.io.File


/**
 * 媒体库扫描类(视频)
 * Create by: chenWei.li
 * Date: 2018/8/21
 * Time: 上午1:01
 * Email: lichenwei.me@foxmail.com
 */
class VideoScanner(private val mContext: Context) : AbsMediaScanner<MediaFile>(mContext) {
    override val scanUri: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    override var projection = arrayOf(MediaStore.Video.Media.DATA, MediaStore.Video.Media.MIME_TYPE, MediaStore.Video.Media.BUCKET_ID, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.DATE_TAKEN)
    override val selection: String? = null
    override var selectionArgs = emptyArray<String>()
    override val order: String = MediaStore.Images.Media.DATE_TAKEN + " desc"

    /**
     * 构建媒体对象
     * 同时需要过滤无效视频
     * @param cursor
     * @return
     */
    override fun parse(cursor: Cursor): MediaFile? {
        val duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
        //视频长度限制5分钟以内
        if (duration <= 0 || duration > VideoContant.VIDEO_LOCAL_MAX) {
            return null
        }
        val path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))

        if (!File(path).canRead()) {
            return null
        }
        val mime = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE))
        val folderId = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID))
        val folderName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))

        val dateToken = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN))
        val mediaFile = MediaFile()
        mediaFile.path = path
        mediaFile.mime = mime
        mediaFile.folderId = folderId
        mediaFile.folderName = folderName
        mediaFile.duration = duration
        mediaFile.dateToken = dateToken

        return mediaFile
    }
}
