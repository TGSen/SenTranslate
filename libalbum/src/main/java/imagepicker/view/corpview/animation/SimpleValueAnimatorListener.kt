package imagepicker.view.corpview.animation

interface SimpleValueAnimatorListener {
    fun onAnimationStarted()

    fun onAnimationUpdated(scale: Float)

    fun onAnimationFinished()
}
