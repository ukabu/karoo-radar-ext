package com.ukabu.karooradar

import io.hammerhead.karooext.models.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Shared extension state that is expensive to recompute or read repeatedly.
 */
object SharedState {
    private val _useImperial = MutableStateFlow<Boolean?>(null)
    val useImperial: StateFlow<Boolean?> = _useImperial.asStateFlow()

    fun setImperialPreference(profile: UserProfile) {
        _useImperial.value = profile.preferredUnit.distance == UserProfile.PreferredUnit.UnitType.IMPERIAL
    }
}
