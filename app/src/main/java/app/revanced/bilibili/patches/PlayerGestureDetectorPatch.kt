package app.revanced.bilibili.patches

import android.view.ScaleGestureDetector
import androidx.annotation.Keep
import app.revanced.bilibili.settings.Settings
import app.revanced.bilibili.utils.callMethod
import app.revanced.bilibili.utils.getObjectField
import tv.danmaku.videoplayer.core.videoview.AspectRatio

object PlayerGestureDetectorPatch {
    @JvmStatic
    private var scaleFactor = 1f

    @Keep
    @JvmStatic
    private var gestureServiceFieldName = ""

    @Keep
    @JvmStatic
    private var getPlayerMethodName = ""

    @Keep
    @JvmStatic
    private var getRenderServiceMethodName = ""

    @Keep
    @JvmStatic
    private var setAspectRatioMethodName = ""

    @Keep
    @JvmStatic
    private var restoreMethodName = ""

    init {
        init()
    }

    @Keep
    @JvmStatic
    private fun init() {
    }

    @Keep
    @JvmStatic
    fun disableLongPress(): Boolean {
        return Settings.DISABLE_PLAYER_LONG_PRESS.boolean
    }

    @Keep
    @JvmStatic
    fun scaleToSwitchRadio(): Boolean {
        return Settings.SCALE_TO_SWITCH_RATIO.boolean
    }

    @Keep
    @JvmStatic
    fun onScale(detector: ScaleGestureDetector) {
        scaleFactor = detector.scaleFactor
    }

    @Keep
    @JvmStatic
    fun onScaleBegin(detector: ScaleGestureDetector) {
        scaleFactor = 1f
    }

    @Keep
    @JvmStatic
    fun onScaleEnd(listener: Any, detector: ScaleGestureDetector) {
        if (!scaleToSwitchRadio()) return
        val renderContainerService = listener.getObjectField(gestureServiceFieldName)
            ?.run { callMethod(getPlayerMethodName, this) }
            ?.callMethod(getRenderServiceMethodName) ?: return
        val ratio =
            if (scaleFactor > 1f) AspectRatio.RATIO_CENTER_CROP else AspectRatio.RATIO_ADJUST_CONTENT
        renderContainerService.callMethod(setAspectRatioMethodName, ratio)
        renderContainerService.callMethod(restoreMethodName, true, null)
    }
}
