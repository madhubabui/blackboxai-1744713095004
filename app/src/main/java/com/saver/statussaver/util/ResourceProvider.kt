package com.saver.statussaver.util

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val resources: Resources = context.resources

    fun getString(@StringRes resId: Int): String {
        return resources.getString(resId)
    }

    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String {
        return resources.getString(resId, *formatArgs)
    }

    fun getQuantityString(@PluralsRes resId: Int, quantity: Int, vararg formatArgs: Any): String {
        return resources.getQuantityString(resId, quantity, *formatArgs)
    }

    fun getStringArray(@ArrayRes resId: Int): Array<String> {
        return resources.getStringArray(resId)
    }

    fun getColor(@ColorRes resId: Int): Int {
        return ContextCompat.getColor(context, resId)
    }

    fun getColorStateList(@ColorRes resId: Int): ColorStateList? {
        return ContextCompat.getColorStateList(context, resId)
    }

    fun getDrawable(@DrawableRes resId: Int): Drawable? {
        return ContextCompat.getDrawable(context, resId)
    }

    fun getDimensionPixelSize(dimenRes: Int): Int {
        return resources.getDimensionPixelSize(dimenRes)
    }

    fun getFloat(dimenRes: Int): Float {
        return resources.getDimension(dimenRes)
    }

    fun getBoolean(boolRes: Int): Boolean {
        return resources.getBoolean(boolRes)
    }

    fun getInteger(intRes: Int): Int {
        return resources.getInteger(intRes)
    }

    fun formatFileSize(sizeInBytes: Long): String {
        return when {
            sizeInBytes < 1024 -> "$sizeInBytes B"
            sizeInBytes < 1024 * 1024 -> "${sizeInBytes / 1024} KB"
            sizeInBytes < 1024 * 1024 * 1024 -> String.format("%.1f MB", sizeInBytes / (1024 * 1024f))
            else -> String.format("%.1f GB", sizeInBytes / (1024 * 1024 * 1024f))
        }
    }

    fun formatDuration(durationMs: Long): String {
        val seconds = (durationMs / 1000) % 60
        val minutes = (durationMs / (1000 * 60)) % 60
        val hours = durationMs / (1000 * 60 * 60)

        return when {
            hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%02d:%02d", minutes, seconds)
        }
    }

    fun formatCount(count: Int): String {
        return when {
            count < 1000 -> count.toString()
            count < 1000000 -> String.format("%.1fK", count / 1000f)
            else -> String.format("%.1fM", count / 1000000f)
        }
    }

    fun getScreenWidth(): Int {
        return resources.displayMetrics.widthPixels
    }

    fun getScreenHeight(): Int {
        return resources.displayMetrics.heightPixels
    }

    fun getDensity(): Float {
        return resources.displayMetrics.density
    }

    fun dpToPx(dp: Float): Int {
        return (dp * getDensity() + 0.5f).toInt()
    }

    fun pxToDp(px: Int): Float {
        return px / getDensity()
    }
}
