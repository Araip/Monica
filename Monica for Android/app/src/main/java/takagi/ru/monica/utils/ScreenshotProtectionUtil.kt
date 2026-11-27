package takagi.ru.monica.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.Window
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.platform.LocalContext

/**
 * 防截屏工具类
 */
object ScreenshotProtectionUtil {
    private data class ForcedProtection(var owners: Int, var ordinaryEnabled: Boolean)
    private val forcedWindows = java.util.WeakHashMap<Window, ForcedProtection>()
    private val forcedProtectionChanges = mutableIntStateOf(0)
    private const val FLAG = WindowManager.LayoutParams.FLAG_SECURE

    /** Main-thread ownership shared with the ordinary app setting. */
    fun acquireForcedProtection(window: Window) {
        // 富江改造：防截屏已彻底移除，空操作
        return
    }

    fun releaseForcedProtection(window: Window) {
        // 富江改造：防截屏已彻底移除，空操作
        return
    }

    /** Observed by separate Compose dialog windows without retaining their host Activity. */
    internal fun hasForcedProtection(window: Window): Boolean {
        forcedProtectionChanges.intValue
        return forcedWindows.containsKey(window)
    }
    
    fun enableScreenshotProtection(activity: Activity) {
        // 富江改造：防截屏已彻底移除，空操作
    }
    
    /**
     * 禁用防截屏保护
     */
    fun disableScreenshotProtection(activity: Activity) {
        activity.window.clearFlags(FLAG)
    }
}

internal tailrec fun Context.screenshotActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> if (baseContext !== this) baseContext.screenshotActivity() else null
    else -> null
}

/**
 * Compose组件：防截屏保护
 */
@Composable
fun ScreenshotProtection(
    enabled: Boolean
) {
    val context = LocalContext.current
    
    DisposableEffect(context, enabled) {
        val activity = context.screenshotActivity()
        if (activity != null) {
            if (enabled) {
                ScreenshotProtectionUtil.enableScreenshotProtection(activity)
            } else {
                ScreenshotProtectionUtil.disableScreenshotProtection(activity)
            }
        }
        
        onDispose {
            // 在组件销毁时恢复原始状态
            if (activity != null && enabled) {
                ScreenshotProtectionUtil.disableScreenshotProtection(activity)
            }
        }
    }
}
