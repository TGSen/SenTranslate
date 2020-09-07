package imagepicker.view.corpview

import android.graphics.Bitmap
import android.net.Uri

import imagepicker.view.corpview.callback.SaveCallback
import imagepicker.view.corpview.callback.SavePathCallback

import io.reactivex.Single

class SaveRequest(private val cropImageView: CropImageView, private val image: Bitmap) {
    private var compressFormat: Bitmap.CompressFormat? = null
    private var compressQuality = -1

    fun compressFormat(compressFormat: Bitmap.CompressFormat): SaveRequest {
        this.compressFormat = compressFormat
        return this
    }

    fun compressQuality(compressQuality: Int): SaveRequest {
        this.compressQuality = compressQuality
        return this
    }

    private fun build() {
        if (compressFormat != null) {
            cropImageView.setCompressFormat(compressFormat)
        }
        if (compressQuality >= 0) {
            cropImageView.setCompressQuality(compressQuality)
        }
    }

    fun execute(saveUri: Uri, callback: SaveCallback) {
        build()
        cropImageView.saveAsync(saveUri, image, callback)
    }

    fun execute(path: String, callback: SavePathCallback) {
        build()
        cropImageView.saveAsync(path, image, callback)
    }

    fun executeAsSingle(saveUri: Uri): Single<Uri> {
        build()
        return cropImageView.saveAsSingle(image, saveUri)
    }
}
