package com.sen.translatev

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import base.activity.BaseActivity
import com.blankj.utilcode.util.ConvertUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.huawei.hmf.tasks.OnFailureListener
import com.huawei.hmf.tasks.Task
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.MLFrame
import com.huawei.hms.mlsdk.text.MLRemoteTextSetting
import com.huawei.hms.mlsdk.text.MLText
import com.huawei.hms.mlsdk.text.MLTextAnalyzer
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslator
import com.sen.translatev.databinding.ActTranslateBinding
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder
import imagepicker.MediaFile
import kotlinx.android.synthetic.main.act_translate.*
import utils.setImageContent


class TranlateActivity : BaseActivity<ActTranslateBinding>(), View.OnClickListener {
    private var heightPixels: Int = 0
    private var marginTop: Int = 0
    private var offsetDistance: Int = 0
    private lateinit var mHandler: Handler
    private val STATE_ERROR_TEXT_ANALYZER = 2
    private val STATE_ERROR_TEXT_TRANSLATE = 3
    private val STATE_IDEO = 1
    private var currentState = STATE_IDEO

    private var textAnalyzer: MLTextAnalyzer? = null

    val TAG = "Harrison"
    override fun initView() {
        Log.e("Harrison", senDto.str1)
        mHandler = Handler(Looper.getMainLooper())

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        immersionBar {
            transparentStatusBar()
        }
        initBehavior()
        initRecycleView()
    }

    private fun initRecycleView() {
        var list = ArrayList<String>()
        for (index in 1..100) {
            list.add("index $index")
        }
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = object :
            CommonAdapter<String>(this, R.layout.item_translate_layout, list) {
            override fun convert(holder: ViewHolder?, t: String?, position: Int) {
                holder?.setText(R.id.position, position.toString())
                holder?.setText(R.id.content, t.orEmpty())
            }
        }
    }

    override fun initData() {
        super.initData()
        if(this.currentState == STATE_IDEO){

        }
    }

    override fun setLayoutId(): Int {
        return R.layout.act_translate
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
        }
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

        srcImage.post {
            var pathList = senDto.list as ArrayList<MediaFile>
            setImageContent(binding.srcImage, pathList[0].path)
            val lp = imageClose.layoutParams as FrameLayout.LayoutParams

            //获取状态栏高度
            var statusBarHeight = 0
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                statusBarHeight = resources.getDimensionPixelSize(resourceId)
            }
            //返回按钮至屏幕顶部的高度
            marginTop = imageClose.height + lp.topMargin + lp.bottomMargin / 2 + statusBarHeight
            //返回按钮至根布局的距离
            offsetDistance = lp.topMargin
            behavior.peekHeight =
                resources.displayMetrics.widthPixels * 3 / 4 + ConvertUtils.dp2px(
                    48.0f
                )
            errorTryWork()
        }

    }

    private fun errorTryWork(){
        if(currentState==STATE_ERROR_TEXT_ANALYZER || currentState ==STATE_IDEO){
            createRemoteTextAnalyzer()
        }else{

        }
    }

    //创建远程文本分析器，去识别文本
    private fun createRemoteTextAnalyzer() {
        val setting =
            MLRemoteTextSetting.Factory().setTextDensityScene(MLRemoteTextSetting.OCR_LOOSE_SCENE)
                .create()
        this.textAnalyzer = MLAnalyzerFactory.getInstance().getRemoteTextAnalyzer(setting)
        srcImage.isDrawingCacheEnabled = true;
        var bitmap = Bitmap.createBitmap(srcImage.drawingCache);
        srcImage.isDrawingCacheEnabled = false;
        val mlFrame = MLFrame.Creator().setBitmap(bitmap).create()
        val task: Task<MLText> = this.textAnalyzer?.asyncAnalyseFrame(mlFrame)!!
        task.addOnSuccessListener { mlText -> // Transacting logic for segment success.
            if (mlText != null) {
                currentState = STATE_IDEO
                remoteDetectSuccess(mlText)
            } else {
                Log.e("Harrison", "onsuccess fail")
                displayFailure()
                currentState = STATE_ERROR_TEXT_ANALYZER
            }
        }
            .addOnFailureListener(OnFailureListener { e -> // Transacting logic for segment failure.
                Log.e("Harrison", "onFailure fail " + e.localizedMessage)
                currentState = STATE_ERROR_TEXT_ANALYZER
                displayFailure()
                return@OnFailureListener
            })
    }

    private fun remoteDetectSuccess(mlTexts: MLText) {
        var sourceText = ""
        val blocks = mlTexts.blocks
        val lines: MutableList<MLText.TextLine> =
            java.util.ArrayList()
        for (block in blocks) {
            for (line in block.contents) {
                if (line.stringValue != null) {
                    lines.add(line)
                }
            }
        }
        lines.sortWith(Comparator { o1, o2 ->
            val point1 = o1.vertexes
            val point2 = o2.vertexes
            point1[0].y - point2[0].y
        })
        for (i in lines.indices) {
            sourceText = sourceText + lines[i].stringValue.trim { it <= ' ' } + "\n"
        }
        this.createRemoteTranslator()
    }

    private var translator: MLRemoteTranslator? = null
    private fun createRemoteTranslator() {
//        val factory =
//            MLRemoteTranslateSetting.Factory() // Set the target language code. The ISO 639-1 standard is used.
//                .setTargetLangCode(this.dstLanguage)
//        factory.setSourceLangCode(this.srcLanguage)
//        this.translator = MLTranslatorFactory.getInstance().getRemoteTranslator(factory.create())
//        val task: Task<String> =
//            translator?.asyncTranslate(sourceText)!!
//        task.addOnSuccessListener { text ->
//            if (text != null) {
//                remoteDisplaySuccess(text)
//            } else {
//                displayFailure()
//            }
//        }.addOnFailureListener { displayFailure() }
    }

    private fun displayFailure() {

    }


}