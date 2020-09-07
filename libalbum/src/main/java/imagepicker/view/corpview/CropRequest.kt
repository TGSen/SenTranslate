package imagepicker.view.corpview

import android.graphics.Bitmap
import android.net.Uri

import imagepicker.view.corpview.callback.CropCallback

import io.reactivex.Single

class CropRequest(private val cropImageView: CropImageView, private val sourceUri: Uri) {
    private var outputWidth: Int = 0
    private var outputHeight: Int = 0
    private var outputMaxWidth: Int = 0
    private var outputMaxHeight: Int = 0

    fun outputWidth(outputWidth: Int): CropRequest {
        this.outputWidth = outputWidth
        this.outputHeight = 0
        return this
    }

    fun outputHeight(outputHeight: Int): CropRequest {
        this.outputHeight = outputHeight
        this.outputWidth = 0
        return this
    }

    fun outputMaxWidth(outputMaxWidth: Int): CropRequest {
        this.outputMaxWidth = outputMaxWidth
        return this
    }

    fun outputMaxHeight(outputMaxHeight: Int): CropRequest {
        this.outputMaxHeight = outputMaxHeight
        return this
    }

    private fun build() {
        if (outputWidth > 0) cropImageView.setOutputWidth(outputWidth)
        if (outputHeight > 0) cropImageView.setOutputHeight(outputHeight)
        cropImageView.setOutputMaxSize(outputMaxWidth, outputMaxHeight)
    }

    fun execute(cropCallback: CropCallback) {
        build()
        cropImageView.cropAsync(sourceUri, cropCallback)
    }

    fun executeAsSingle(): Single<Bitmap> {
        build()
        return cropImageView.cropAsSingle(sourceUri)
    }
}
