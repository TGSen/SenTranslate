package imagepicker

import imagepicker.view.ClipMatrix
import java.io.Serializable
import java.util.*


const val ITEM_TYPE_CAMERA = 1
const val ITEM_TYPE_IMAGE = 2
const val ITEM_TYPE_VIDEO = 3

/**
 * 图片文件夹实体类
 * Create by: chenWei.li
 * Date: 2018/8/23
 * Time: 上午12:56
 * Email: lichenwei.me@foxmail.com
 */
class MediaFolder(var folderId: Int, var folderName: String?, var folderCover: String?, var mediaFileList: ArrayList<MediaFile>?) {
    var isCheck: Boolean = false
}


/**
 * 媒体实体类
 * Create by: chenWei.li
 * Date: 2018/8/22
 * Time: 上午12:36
 * Email: lichenwei.me@foxmail.com
 */
/**
 * 保存上一次的位置状态
 */

// class MediaFile1(
//    @SerializedName("path") var path: String? = null,
//    var mime: String? = null,
//    var folderId: Int? = null,
//    var folderName: String? = null,
//    var duration: Long = 0,
//    var dateToken: Long = 0,
//    var seletedIndex = 0 ,// 在选择列表index
//    var position = 0 ,//在显示列表中的index
//    var cutPath: String? = null,
//    var imageToken: String? = null,
//    var scaleMatrix: Matrix? = null,
//    //特殊需求使用的原图路径
//    var srcPath: String? = null
//) : Serializable {
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as MediaFile
//
//        if (path != other.path) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        return path?.hashCode() ?: 0
//    }
//
//    fun getVideoImage(): Array<String> {
//        return arrayOf("", path.orEmpty())
//    }
//}


class MediaFile : Serializable {

    var path: String? = null
    var mime: String? = null
    var folderId: Int? = null
    var folderName: String? = null
    var duration: Long = 0
    var dateToken: Long = 0
    var seletedIndex = 0 // 在选择列表index
    var position = 0 //在显示列表中的index
    var cutPath: String? = null
    var imageToken: String? = null
    var scaleMatrix: ClipMatrix? = null
    //特殊需求使用的原图路径
    var srcPath: String? = null
    var seleteItem:Boolean = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MediaFile

        if (path != other.path) return false

        return true
    }

    override fun hashCode(): Int {
        return path?.hashCode() ?: 0
    }

    fun getRealImage(): String {
        return if (cutPath.isNullOrEmpty()) path ?: "" else cutPath ?: ""
    }

    fun getVideoImage(): Array<String> {
        return arrayOf("", path.orEmpty())
    }

}
