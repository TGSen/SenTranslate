package com.sen.translatev

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.util.Log
import android.view.View
import base.activity.BaseActivity
import com.blankj.utilcode.util.ConvertUtils
import com.sen.translatev.databinding.ActTranslateBinding
import kotlinx.android.synthetic.main.act_translate.*
import utils.setImageContent

class TranlateActivity : BaseActivity<ActTranslateBinding>(), View.OnClickListener {
    private var heightPixels: Int = 0
    private var marginTop: Int = 0
    private var offsetDistance: Int = 0
    private lateinit var mHandler: Handler

    val TAG = "Harrison"
    override fun initView() {
        Log.e("Harrison", senDto.str1)
        mHandler = Handler(Looper.getMainLooper())

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBehavior()
    }

    override fun setLayoutId(): Int {
        return R.layout.act_translate
    }

    override fun onClick(p0: View?) {
    }

    private fun initBehavior() {
        val behavior = BottomSheetBehavior.from(binding.NestedScrollView)
        behavior.isHideable = true
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val layoutParams = bottomSheet.layoutParams
                //如果控件本身的Height值就小于返回按钮的高度，就不用做处理
                if (bottomSheet.height > heightPixels - marginTop) {
                    //屏幕高度减去marinTop作为控件的Height
                    layoutParams.height = heightPixels - marginTop
                    bottomSheet.layoutParams = layoutParams
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                var distance: Float = 0F;
                /**
                 * slideOffset为底部的新偏移量，值在[-1,1]范围内。当BottomSheetBehavior处于折叠(STATE_COLLAPSED)和
                 * 展开(STATE_EXPANDED)状态之间时,它的值始终在[0,1]范围内，向上移动趋近于1，向下区间于0。[-1,0]处于
                 * 隐藏状态(STATE_HIDDEN)和折叠状态(STATE_COLLAPSED)之间。
                 */

                //这里的BottomSheetBehavior初始化完成后，界面设置始终可见，所以不用考虑[-1,0]区间
                //色差值变化->其实是遮罩的透明度变化，拖拽至最高，顶部成半透明色
                maskView.alpha = slideOffset
                //offsetDistance是initSystem()中获得的，是返回按钮至根布局的距离
                distance = offsetDistance * slideOffset
                //当BottomSheetBehavior由隐藏状态变为折叠状态(即gif图开始的由底部滑出至设置的最小高度)
                //slide在[-1,0]的区间内，不加判断会出现顶部布局向下偏移的情况。
                if (distance > 0) {
                    constraint.translationY = -distance
                }

//                Log.e(
//                    TAG,
//                    String.format(
//                        "slideOffset -->>> %s bottomSheet.getHeight() -->>> %s heightPixels -->>> %s",
//                        slideOffset,
//                        bottomSheet.height,
//                        heightPixels
//                    )
//                )
                Log.e(TAG, String.format("distance -->>> %s", distance))
            }

        })
        //获取屏幕高度
        heightPixels = resources.displayMetrics.heightPixels
        behavior.isHideable = false
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED

        imageView.post {
            setImageContent(binding.srcImage, senDto.str1)
            val lp = imageView.layoutParams as ConstraintLayout.LayoutParams

            //获取状态栏高度
            var statusBarHeight = 0
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                statusBarHeight = resources.getDimensionPixelSize(resourceId)
            }
            //返回按钮至屏幕顶部的高度
            marginTop = imageView.height + lp.topMargin + lp.bottomMargin / 2+statusBarHeight
            //返回按钮至根布局的距离
            offsetDistance = lp.topMargin
            behavior.peekHeight =
                resources.displayMetrics.widthPixels * 3 / 4  + ConvertUtils.dp2px(
                    48.0f
                )

        }

    }


}