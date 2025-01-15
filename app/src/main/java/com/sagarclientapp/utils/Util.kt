package com.sagarclientapp.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.text.TextUtils
import android.util.Log
import android.view.View
import java.util.Locale
import java.util.regex.Pattern

object Util {
    private val emailRegex = Pattern.compile(
        "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
    )
    fun Activity.launchActivity(
        className: Class<*>,
        bundle: Bundle? = null,
        view: View? = null,
        removeAllPrevActivities: Boolean = false,
        removeOnlyThisActivity:Boolean=false
    ) {
        val activityLaunchIntent = Intent(this, className)
        bundle?.let { activityLaunchIntent.putExtras(it) }
        startActivity(activityLaunchIntent)

        if (removeAllPrevActivities) {
            this@launchActivity.finishAffinity()
        }
        if (removeOnlyThisActivity) {
            this@launchActivity.finish()
        }
    }
    fun isValidEmail(email: String): Boolean {
        var value = false
        if (!TextUtils.isEmpty(email) && emailRegex.matcher(email).matches()) {
            value = true
        }
        return value
    }
    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Log.d("LocaleChange", "Changing language to: $languageCode")
        Locale.setDefault(locale)

        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale)
            configuration.setLocales(LocaleList(locale))
        } else {
            configuration.locale = locale
        }

        resources.updateConfiguration(configuration, resources.displayMetrics)

        /*val locale = Locale(languageCode)
        Locale.setDefault(locale)

        var config = context.resources.configuration
        config.setLocale(locale)

        if (languageCode == "ar") {
            config.layoutDirection = View.LAYOUT_DIRECTION_RTL
        } else {
            config.layoutDirection = View.LAYOUT_DIRECTION_LTR
        }

        context.resources.updateConfiguration(config, context.resources.displayMetrics)*/
    }
}