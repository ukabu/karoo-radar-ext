package com.ukabu.karooradar

import android.content.Context
import android.content.res.Configuration
import android.widget.RemoteViews
import androidx.compose.ui.graphics.toArgb
import io.hammerhead.karooext.models.ViewConfig

/**
 * Builds a native-style RemoteViews datafield for radar metrics.
 */
internal fun radarDatafieldRemoteViews(
    context: Context,
    alignment: ViewConfig.Alignment,
    value: String,
    threatLevel: ThreatLevel,
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

    rv.setTextViewText(R.id.field_value, value)

    rv.setTextColor(R.id.field_value, textColor.toArgb())

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
