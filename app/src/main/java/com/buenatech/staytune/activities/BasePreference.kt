package com.buenatech.staytune.activities

import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

abstract class BasePreference : PreferenceFragmentCompat() {

    fun setPreferenceSummary(key: String, summary: String?) {
        findPreference<Preference>(key)?.summary = summary
    }


}