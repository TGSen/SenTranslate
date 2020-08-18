package utils

import android.os.SystemClock
import android.view.View
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo

class OnSingleClickListener(
    private val clickListener: View.OnClickListener,
    private val intervalMs: Long = 500,
    private val hasAnimation: Boolean = false
) : View.OnClickListener {
    private var lastTimeClicked: Long = 0

    override fun onClick(v: View?) {
        if(hasAnimation) {
            YoYo.with(Techniques.Swing).playOn(v)
        }

        if (SystemClock.elapsedRealtime() - lastTimeClicked < intervalMs) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()

        clickListener.onClick(v)
    }
}