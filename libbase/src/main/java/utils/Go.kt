package utils

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import java.io.Serializable

const val DTO_KEY: String = "dto_key"
const val BUNDLE_KEY: String = "bundle_key"

fun go(context: AppCompatActivity, classz: Class<out AppCompatActivity>, dto: SenDto = SenDto()) {
    var intent = Intent(context, classz)
    var bundle = Bundle()
    bundle.putSerializable(DTO_KEY, dto)
    intent.putExtra(BUNDLE_KEY, bundle)
    context.startActivity(intent)
}

fun goForResult(context: AppCompatActivity, classz: Class<out AppCompatActivity>, dto: SenDto = SenDto(), requestCode: Int) {
    var intent = Intent(context, classz)
    var bundle = Bundle()
    bundle.putSerializable(DTO_KEY, dto)
    intent.putExtra(BUNDLE_KEY, bundle)
    context.startActivityForResult(intent, requestCode)
}

class SenDto(
        var str1: String = "",
        var any1: Any? = null,
        var list: List<Any> = arrayListOf()

) : Serializable