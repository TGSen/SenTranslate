package imagepicker.manager


import android.text.TextUtils
import imagepicker.MediaFile
import imagepicker.utils.MediaFileUtil
import java.util.*

/**
 * 媒体选择集合管理类
 * Create by: chenWei.li
 * Date: 2018/8/23
 * Time: 上午1:19
 * Email: lichenwei.me@foxmail.com
 *
 * 修改by 唐家森，不再持有单例，让每个页面都有自己的selectPaths
 * 让它在裁剪 和
 */
class SelectionManager {

    /**
     * 获取当前所选图片集合path
     *
     * @return
     */
    val selectPaths = ArrayList<MediaFile>()
    val needDelectedPaths = ArrayList<MediaFile>() //需要删除的
    var mStartIndex = 0

    /**
     * 获取当前设置最大选择数
     *
     * @return
     */
    /**
     * 设置最大选择数
     *
     * @param maxCount
     */
    var maxCount = 1

    /**
     * 是否还可以继续选择图片
     *
     * @return
     */
    val isCanChoose: Boolean
        get() = selectPaths.size < maxCount

    /**
     * Video 目前保证只有一个
     * @param imagePath
     * @return
     */
    fun addVideoToSelectList(imagePath: MediaFile, position: Int) {
        if (selectPaths.size < maxCount) {
            imagePath.seletedIndex = selectSize() + 1
            imagePath.position = position
            selectPaths.add(imagePath)
        } else {
            //先判断是不是自己
            var first = selectPaths[0]
            needDelectedPaths.add(first)
            selectPaths.clear()
            if (first.path != imagePath.path) {
                imagePath.seletedIndex = selectSize() + 1
                imagePath.position = position
                selectPaths.add(imagePath)
            }
        }
    }

    fun addJustOneToSelectList(imagePath: MediaFile, position: Int) {
        if (selectPaths.size < maxCount) {
            imagePath.seletedIndex = selectSize() + 1
            imagePath.position = position
            selectPaths.add(imagePath)
        } else {
            //先判断是不是自己
            var first = selectPaths[0]
            needDelectedPaths.add(first)
            selectPaths.clear()
            if (first.path != imagePath.path) {
                imagePath.seletedIndex = selectSize() + 1
                imagePath.position = position
                selectPaths.add(imagePath)
            }
        }
    }

    /**
     * 这个是提供明明知道需要添加的
     */

    fun addItemToSelect(path:String){
        var item  = MediaFile()
        item.path = path
        item.seletedIndex = mStartIndex+selectSize() + 1
        selectPaths.add(item)
    }

    /**
     * 添加/移除图片到选择集合
     *
     * @param imagePath
     * @return
     */
    fun addImageToSelectList(imagePath: MediaFile, position: Int): Boolean {
        var delete = false
        selectPaths.forEach {
            if (it.path == imagePath.path) {
                it.position = position
                needDelectedPaths.add(it)
                delete = true
            }
        }
        if (delete) {
            selectPaths.remove(needDelectedPaths[0])
            for (index in selectPaths.indices) {
                selectPaths[index].seletedIndex = mStartIndex+index + 1
            }
            return true
        }
        if (selectPaths.size < maxCount) {
            imagePath.seletedIndex = mStartIndex+selectSize() + 1
            imagePath.position = position
            selectPaths.add(imagePath)
            return true
        }
        return false
    }

    fun ressetDelectList() {
        needDelectedPaths.clear()
    }

    /**
     * 添加/移除图片到选择集合
     *拍照的情况下如果超过9张那么，替换第九张
     * @param imagePath
     * @return
     */
    fun addImageToSelectList(imagePath: String, addFrist: Boolean = false): Boolean {
        if (TextUtils.isEmpty(imagePath)) return false
        selectPaths.forEach {
            if (it.path == imagePath) {
                return selectPaths.remove(it)
            }
        }
        var mediaFile = MediaFile()
        mediaFile.path = imagePath

        if (selectPaths.size < maxCount) {

        } else {
            //将最后一个remove
            selectPaths.removeAt(maxCount - 1)
        }

        if (addFrist) {
            selectPaths.add(0, mediaFile)
        } else {
            selectPaths.add(mediaFile)
        }
        return true
    }

    /**
     * 添加图片到选择集合
     *
     * @param imagePaths
     */
    fun addImagePathsToSelectList(imagePaths: List<MediaFile>?) {
        if (imagePaths != null) {
            for (i in imagePaths.indices) {
                val imagePath = imagePaths[i]
                if (!selectPaths.contains(imagePath) && selectPaths.size < maxCount) {
                    imagePath.seletedIndex =mStartIndex+i + 1
                    selectPaths.add(imagePath)
                }
            }
        }
    }


    /**
     * 判断当前图片是否被选择
     *
     * @param imagePath
     * @return
     */
    fun isImageSelect(imagePath: String): Int {
        selectPaths.forEach {
            if (it.path == imagePath) {
                return it.seletedIndex
            }
        }
        return -1
    }

    /**
     * 判断当前图片是否被选择,如果选择了，就写入当前的position
     *
     * @param imagePath
     * @return
     */
    fun isImageSelect(imagePath: String, position: Int): Int {
        selectPaths.forEach {
            if (it.path == imagePath) {
                it.position = position
                return it.seletedIndex
            }
        }
        return -1
    }


    /**
     * 清除已选图片
     */
    fun removeAll() {
        selectPaths.clear()
    }

    fun selectSize(): Int {
        return selectPaths.size
    }

    /**
     * path : 传入 MediaFile 中的 path
     */

    fun removePath(path: String) {
        selectPaths.forEach {
            if (it.path == path) {
                selectPaths.remove(it)
                return
            }
        }
    }

    /**
     * 是否可以添加到选择集合（在singleType模式下，图片视频不能一起选）
     *
     * @param currentPath
     * @param filePath
     * @return
     */
    fun isCanAddSelectionPaths(currentPath: String, filePath: String): Boolean {
        return !(MediaFileUtil.isVideoFileType(currentPath) && !MediaFileUtil.isVideoFileType(filePath) || !MediaFileUtil.isVideoFileType(
            currentPath
        ) && MediaFileUtil.isVideoFileType(filePath))
    }



}
