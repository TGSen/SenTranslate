package imagepicker.view.corpview.animation

interface SimpleValueAnimator {

    val isAnimationStarted: Boolean
    fun startAnimation(duration: Long)

    fun cancelAnimation()

    fun addAnimatorListener(animatorListener: SimpleValueAnimatorListener)
}
