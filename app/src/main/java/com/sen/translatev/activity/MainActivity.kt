package com.sen.translatev.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.huawei.hmf.tasks.OnFailureListener
import com.huawei.hmf.tasks.Task
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.MLFrame
import com.huawei.hms.mlsdk.text.MLRemoteTextSetting
import com.huawei.hms.mlsdk.text.MLText
import com.huawei.hms.mlsdk.text.MLTextAnalyzer
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslateSetting
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslator
import com.sen.translatev.R
import java.util.*

class MainActivity : AppCompatActivity() {
    private var originBitmap: Bitmap? =null
    private val dstLanguage: String="EN"
    private val srcLanguage: String="Auto"
    private lateinit var sourceText: String
    private var textAnalyzer: MLTextAnalyzer?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        var path = Environment.getExternalStorageDirectory().absolutePath+"/Download/123.jpg"

        originBitmap =  BitmapFactory.decodeResource(resources,
            R.drawable.e
        )
        var image = findViewById<ImageView>(R.id.image)
        image.setImageBitmap(originBitmap)
        findViewById<View>(R.id.start).setOnClickListener {
            createRemoteTextAnalyzer()
        }
        findViewById<View>(R.id.finish).setOnClickListener {
            finish()
        }
    }

    private  fun remoteDetectSuccess(mlTexts: MLText) {
        this.sourceText = ""
        val blocks = mlTexts.blocks
        val lines: MutableList<MLText.TextLine> =
            ArrayList()
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
            this.sourceText = this.sourceText + lines[i].stringValue.trim { it <= ' ' } + "\n"
        }
        this.createRemoteTranslator()
    }
    private var translator: MLRemoteTranslator? = null
    private fun createRemoteTranslator() {
        val factory =
            MLRemoteTranslateSetting.Factory() // Set the target language code. The ISO 639-1 standard is used.
                .setTargetLangCode(this.dstLanguage)
        factory.setSourceLangCode(this.srcLanguage)
        this.translator = MLTranslatorFactory.getInstance().getRemoteTranslator(factory.create())
        val task: Task<String> =
            translator?.asyncTranslate(sourceText)!!
        task.addOnSuccessListener { text ->
            if (text != null) {
                remoteDisplaySuccess(text)
            } else {
                displayFailure()
            }
        }.addOnFailureListener { displayFailure() }
    }
    private fun remoteDisplaySuccess(test: String) {
        Log.e("Harrison", "res : $test")

    }

    private fun createRemoteTextAnalyzer() {
        val setting = MLRemoteTextSetting.Factory().setTextDensityScene(MLRemoteTextSetting.OCR_LOOSE_SCENE)
                .create()
        this.textAnalyzer = MLAnalyzerFactory.getInstance().getRemoteTextAnalyzer(setting)
            val mlFrame = MLFrame.Creator().setBitmap(this.originBitmap).create()
            val task: Task<MLText> = this.textAnalyzer?.asyncAnalyseFrame(mlFrame)!!
            task.addOnSuccessListener { mlText -> // Transacting logic for segment success.
                if (mlText != null) {
                   remoteDetectSuccess(mlText)
                } else {
                    Log.e("Harrison", "onsuccess fail")
                   displayFailure()
                }
            }
                .addOnFailureListener(OnFailureListener { e -> // Transacting logic for segment failure.
                    Log.e("Harrison", "onFailure fail " + e.localizedMessage)
                    displayFailure()
                    return@OnFailureListener
                })
        }

    private fun displayFailure() {
        Toast.makeText(
            this.applicationContext,
            "Fail",
            Toast.LENGTH_SHORT
        ).show()
    }


}