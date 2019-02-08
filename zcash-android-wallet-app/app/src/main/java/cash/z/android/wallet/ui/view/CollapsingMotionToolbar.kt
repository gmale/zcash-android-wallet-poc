package cash.z.android.wallet.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.constraintlayout.motion.widget.MotionLayout
import com.google.android.material.appbar.AppBarLayout

class CollapsingMotionToolbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MotionLayout(context, attrs, defStyleAttr), AppBarLayout.OnOffsetChangedListener {

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        progress = -verticalOffset / appBarLayout.totalScrollRange.toFloat()
        Log.e("MotionL", "progress: $progress  verticalOffset: $verticalOffset  scrollRange: ${appBarLayout.totalScrollRange.toFloat()}")
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (parent as? AppBarLayout)?.let { appBarLayout ->
//            appBarLayout.removeOnOffsetChangedListener(this)
            appBarLayout.addOnOffsetChangedListener(this)
        }
    }
}