package imagepicker.view.corpview

import android.graphics.RectF
import android.net.Uri

import imagepicker.view.corpview.callback.LoadCallback

import io.reactivex.Completable

class LoadRequest(private val cropImageView: CropImageView, private val sourceUri: Uri) {

    private var initialFrameScale: Float = 0.toFloat()
    private var initialFrameRect: RectF? = null
    private var useThumbnail: Boolean = false

    fun initialFrameScale(initialFrameScale: Float): LoadRequest {
        this.initialFrameScale = initialFrameScale
        return this
    }

    fun initialFrameRect(initialFrameRect: RectF): LoadRequest {
        this.initialFrameRect = initialFrameRect
        return this
    }

    fun useThumbnail(useThumbnail: Boolean): LoadRequest {
        this.useThumbnail = useThumbnail
        return this
    }

    fun execute(callback: LoadCallback) {
        if (initialFrameRect == null) {
            cropImageView.setInitialFrameScale(initialFrameScale)
        }
        cropImageView.loadAsync(sourceUri, useThumbnail, initialFrameRect, callback)
    }

    fun executeAsCompletable(): Completable {
        if (initialFrameRect == null) {
            cropImageView.setInitialFrameScale(initialFrameScale)
        }
        return cropImageView.loadAsCompletable(sourceUri, useThumbnail, initialFrameRect)
    }
}
