package imagepicker.loader

import android.content.Context
import android.database.Cursor
import android.net.Uri
import java.util.*

/**
 * 媒体库查询任务基类
 * Create by: chenWei.li
 * Date: 2019/1/21
 * Time: 8:35 PM
 * Email: lichenwei.me@foxmail.com
 */
abstract class AbsMediaScanner<T>(private val mContext: Context) {

    /**
     * 查询URI
     *
     * @return
     */
    protected abstract val scanUri: Uri

    /**
     * 查询列名
     *
     * @return
     */
    protected abstract var projection: Array<String>

    /**
     * 查询条件
     *
     * @return
     */
    protected abstract val selection: String?

    /**
     * 查询条件值
     *
     * @return
     */
    protected abstract var selectionArgs: Array<String>

    /**
     * 查询排序
     *
     * @return
     */
    protected abstract val order: String

    /**
     * 对外暴露游标，让开发者灵活构建对象
     *
     * @param cursor
     * @return
     */
    protected abstract fun parse(cursor: Cursor): T?

    /**
     * 根据查询条件进行媒体库查询，隐藏查询细节，让开发者更专注业务
     *
     * @return
     */
    fun queryMedia(): ArrayList<T> {
        val list = ArrayList<T>()
        val contentResolver = mContext.contentResolver
        val cursor = contentResolver.query(scanUri, projection, selection, selectionArgs, order)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val t = parse(cursor)
                //有时为空就是过滤该文件
                if (t != null) {
                    list.add(t)
                }

            }
            cursor.close()
        }
        return list
    }

}
