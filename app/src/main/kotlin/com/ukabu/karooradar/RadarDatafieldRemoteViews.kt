package com.ukabu.karooradar

import android.content.Context
import android.content.res.Configuration
import android.util.TypedValue
import android.widget.RemoteViews
import androidx.compose.ui.graphics.toArgb
import io.hammerhead.karooext.models.ViewConfig

/**
 * Builds a native-style RemoteViews datafield for radar metrics.
 */
internal fun radarDatafieldRemoteViews(
    context: Context,
    alignment: ViewConfig.Alignment,
    label: String,
    value: String,
    threatLevel: ThreatLevel,
    textSize: Int,
    labelSize: Int,
): RemoteViews {
    val layoutRes = when (alignment) {
        ViewConfig.Alignment.LEFT -> R.layout.radar_datafield_left
        ViewConfig.Alignment.CENTER -> R.layout.radar_datafield_center
        else -> R.layout.radar_datafield
    }

    val rv = RemoteViews(context.packageName, layoutRes)

    val isNightMode = (context.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

    val textColor = threatLevel.toTextColor(isNightMode)

    rv.setTextViewText(R.id.field_label, label)
    rv.setTextViewTextSize(R.id.field_label, TypedValue.COMPLEX_UNIT_SP, labelSize.toFloat())
    rv.setTextColor(R.id.field_label, textColor.toArgb())

    rv.setTextViewText(R.id.field_value, value)
    rv.setTextViewTextSize(R.id.field_value, TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
    rv.setTextColor(R.id.field_value, textColor.toArgb())
    // Nudge the value up slightly to match the native host, which applies a
    // negative dataTranslationY to numeric values. Scales with the value size so
    // it stays proportionate across grid sizes.
    val valuePx = textSize * context.resources.displayMetrics.density
    rv.setFloat(R.id.field_value, "setTranslationY", -0.01f * valuePx)
    // The native header sits its label lower than a vertically-centered label;
    // nudge ours down to match. Scales with the value size.
    rv.setFloat(R.id.field_label, "setTranslationY", 0.12f * valuePx)

    val backgroundRes = when (threatLevel) {
        ThreatLevel.CLEAR -> 0
        ThreatLevel.APPROACHING -> R.drawable.radar_datafield_background_approaching
        ThreatLevel.WARNING -> R.drawable.radar_datafield_background_warning
        ThreatLevel.CRITICAL -> R.drawable.radar_datafield_background_critical
    }

    if (backgroundRes != 0) {
        rv.setInt(R.id.field_root, "setBackgroundResource", backgroundRes)
    } else {
        rv.setInt(R.id.field_root, "setBackgroundResource", 0)
    }

    return rv
}
