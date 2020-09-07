package imagepicker.provider

import android.content.Context
import android.support.v4.content.FileProvider

/**
 * 自定义Provider，避免上层发生provider冲突
 * Create by: chenWei.li
 * Date: 2019/1/24
 * Time: 4:03 PM
 * Email: lichenwei.me@foxmail.com
 */
class ImagePickerProvider : FileProvider() {
    companion object {

        fun getFileProviderName(context: Context): String {
            return context.packageName + ".provider"
        }
    }

}
