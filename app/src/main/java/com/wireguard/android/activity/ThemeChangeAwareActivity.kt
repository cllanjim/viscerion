/*
 * Copyright © 2019 Harsh Shandilya. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.wireguard.android.activity

import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.wireguard.android.Application
import com.wireguard.android.util.restartApplication
import com.wireguard.android.util.ApplicationPreferences
import timber.log.Timber
import java.lang.reflect.Field

abstract class ThemeChangeAwareActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Application.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        Application.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (ApplicationPreferences.appThemeKey == key) {
            val isDarkTheme = ApplicationPreferences.useDarkTheme
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkTheme)
                    AppCompatDelegate.MODE_NIGHT_YES
                else
                    AppCompatDelegate.MODE_NIGHT_NO
            )
            invalidateDrawableCache(resources, isDarkTheme)
            applicationContext?.restartApplication()
        }
    }

    companion object {
        private var lastResources: Resources? = null
        private var lastDarkMode: Boolean = false
        @Synchronized
        private fun invalidateDrawableCache(resources: Resources, darkMode: Boolean) {
            if (resources == lastResources && darkMode == lastDarkMode)
                return

            try {
                var f: Field
                var o: Any = resources
                try {
                    f = o.javaClass.getDeclaredField("mResourcesImpl")
                    f.isAccessible = true
                    o = f.get(o)
                } catch (ignored: Exception) {
                }

                try {
                    f = o.javaClass.getDeclaredField("mDrawableCache")
                    f.isAccessible = true
                    o = f.get(o)
                    try {
                        o.javaClass.getMethod("onConfigurationChange", Int::class.javaPrimitiveType).invoke(o, -1)
                    } catch (ignored: Exception) {
                        o.javaClass.getMethod("clear").invoke(o)
                    }
                } catch (ignored: Exception) {
                    f = o.javaClass.getDeclaredField("mColorDrawableCache")
                    f.isAccessible = true
                    o = f.get(o)
                    try {
                        o.javaClass.getMethod("onConfigurationChange", Int::class.javaPrimitiveType).invoke(o, -1)
                    } catch (ignored: Exception) {
                        o.javaClass.getMethod("clear").invoke(o)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to flush drawable cache")
            }

            lastResources = resources
            lastDarkMode = darkMode
        }
    }
}
