package imagepicker.view.corpview.callback

import android.graphics.Bitmap

interface CropCallback : Callback {
    fun onSuccess(cropped: Bitmap)
}
