package com.sen.translatev.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
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
import com.sen.translatev.R
import com.sen.translatev.databinding.ActTranslateVBinding
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder
import imagepicker.MediaFile
import kotlinx.android.synthetic.main.act_translate.*
import me.bakumon.statuslayoutmanager.library.OnStatusChildClickListener
import me.bakumon.statuslayoutmanager.library.StatusLayoutManager
import utils.setImageContent
import utils.setOnSingleClickListener


class TranlateActivity : BaseActivity<ActTranslateVBinding>(), View.OnClickListener {
    private var heightPixels: Int = 0
    private var marginTop: Int = 0
    private var offsetDistance: Int = 0
    private lateinit var mHandler: Handler

    private var textAnalyzer: MLTextAnalyzer? = null
    private var srcTextList: ArrayList<MLText.TextLine> = ArrayList()
    private var srcImagePath: String? = null
    var statusLayoutManager:StatusLayoutManager?=null

    val TAG = "Harrison"
    override fun initView() {
        Log.e("Harrison", senDto.str1)
        mHandler = Handler(Looper.getMainLooper())
        imageClose.setOnSingleClickListener(this)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        immersionBar {
            transparentStatusBar()
        }
        initBehavior()
        initRecycleViewAndLayoutState()
    }

    private fun initRecycleViewAndLayoutState() {

        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = object :
            CommonAdapter<MLText.TextLine>(
                this,
                R.layout.item_translate_layout, srcTextList
            ) {
            override fun convert(holder: ViewHolder?, t: MLText.TextLine?, position: Int) {
                holder?.setText(R.id.content, t?.stringValue)
            }
        }

        var listener:OnStatusChildClickListener = object : OnStatusChildClickListener {
            override fun onEmptyChildClick(view: View) {

            }

            override fun onErrorChildClick(view: View) {
                createRemoteTextAnalyzer()
            }

            override fun onCustomerChildClick(view: View) {
//                if (view.id == R.id.tv_customer) {
//                    Toast.makeText(this@MainActivity, R.string.request_access, Toast.LENGTH_SHORT)
//                        .show()
//                } else if (view.id == R.id.tv_customer1) {
//                    Toast.makeText(this@MainActivity, R.string.switch_account, Toast.LENGTH_SHORT)
//                        .show()
//                }
            }
        }


        statusLayoutManager  =
            StatusLayoutManager.Builder(binding.layoutStateRoot)
                .setLoadingLayout(R.layout.layout_loading)
                .setEmptyLayout(R.layout.layout_empty)
                .setErrorLayout(R.layout.layout_error)
                .setErrorClickViewID(R.id.btnErrorClick).setOnStatusChildClickListener(listener).build() // 设置默认布局属性
        statusLayoutManager?.showCustomLayout(R.layout.layout_translate_customer)

    }

    override fun initData() {
        super.initData()
    }

    override fun setLayoutId(): Int {
        return R.layout.act_translate_v
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imageClose -> finish()
        }
    }

    private fun initBehavior() {
        val behavior = BottomSheetBehavior.from(binding.rootNestedScrollView)
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
//                maskView.alpha = slideOffset
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
//                Log.e(TAG, String.format("distance -->>> %s", distance))
            }

        })
        //获取屏幕高度
        heightPixels = resources.displayMetrics.heightPixels
        behavior.isHideable = false
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED

        srcImage.post {
            var pathList = senDto.list as ArrayList<MediaFile>
            srcImagePath = pathList[0].path
            setImageContent(binding.srcImage, srcImagePath)
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
                resources.displayMetrics.heightPixels - resources.displayMetrics.widthPixels * 3 / 4 + ConvertUtils.dp2px(
                    24.0f
                )

        }

    }




    //创建远程文本分析器，去识别文本
    private fun createRemoteTextAnalyzer() {
        statusLayoutManager?.showLoadingLayout()
        val setting =
            MLRemoteTextSetting.Factory().setTextDensityScene(MLRemoteTextSetting.OCR_LOOSE_SCENE)
                .create()
        this.textAnalyzer = MLAnalyzerFactory.getInstance().getRemoteTextAnalyzer(setting)
        var bitmap = BitmapFactory.decodeFile(srcImagePath)
        val mlFrame = MLFrame.Creator().setBitmap(bitmap).create()
        val task: Task<MLText> = this.textAnalyzer?.asyncAnalyseFrame(mlFrame)!!
        task.addOnSuccessListener { mlText -> // Transacting logic for segment success.
            if (mlText != null) {
                remoteDetectSuccess(mlText)
            } else {
                statusLayoutManager?.showEmptyLayout()
            }
        }
            .addOnFailureListener(OnFailureListener { e -> // Transacting logic for segment failure.
                statusLayoutManager?.showErrorLayout()
                return@OnFailureListener
            })
    }

    private fun remoteDetectSuccess(mlTexts: MLText) {
        val blocks = mlTexts.blocks
        if(blocks.size>0){
            for (block in blocks) {
                for (line in block.contents) {
                    if (line.stringValue != null) {
                        srcTextList.add(line)
                    }
                }
            }
            srcTextList.sortWith(Comparator { o1, o2 ->
                val point1 = o1.vertexes
                val point2 = o2.vertexes
                point1[0].y - point2[0].y
            })
            binding.recyclerView.adapter?.notifyDataSetChanged()
            statusLayoutManager?.showSuccessLayout()
        }else{
           statusLayoutManager?.showEmptyLayout()
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