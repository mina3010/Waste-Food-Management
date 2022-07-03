@file:Suppress("DEPRECATION")

package com.mm.foodmanagment.utils
import android.app.Activity
import android.content.res.Configuration
import java.util.*

object LangUtil {

    fun Activity.setLanguage(lang: String) {
        val myLocale = Locale(lang)
        Locale.setDefault(myLocale)
        val res = resources
        val dm = res.displayMetrics
        val conf = Configuration()
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
        onConfigurationChanged(conf)
    }
}
