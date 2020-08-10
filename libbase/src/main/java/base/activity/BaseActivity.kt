package base.activity


import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.libbase.R
import com.gyf.immersionbar.ktx.immersionBar
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import utils.BUNDLE_KEY
import utils.DTO_KEY
import utils.ScreenAdapter
import utils.SenDto


abstract class BaseActivity<Binding : ViewDataBinding> : AppCompatActivity() {
    protected lateinit var binding: Binding
    open val senDto by lazy { (intent?.getBundleExtra(BUNDLE_KEY)?.getSerializable(DTO_KEY) ?: SenDto()) as SenDto }
    override fun onCreate(savedInstanceState: Bundle?) {
        ScreenAdapter.setCustomDensity(this)
        super.onCreate(savedInstanceState)
        immersionBar {
            statusBarDarkFont(false)
            barColor(R.color.colorPrimary)
        }
        binding = DataBindingUtil.setContentView(this, setLayoutId())
        initView()
        initData()
        initLinstener()

        if (isNeedEventBus()) {
            EventBus.getDefault().register(this)
        }
    }

    open fun initLinstener() {

    }


    open fun initData() {

    }

    /**
     * 防止出错用的
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun empty(empty: Int) {

    }

    open fun isNeedEventBus(): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    protected abstract fun initView()

    abstract fun setLayoutId(): Int


}
