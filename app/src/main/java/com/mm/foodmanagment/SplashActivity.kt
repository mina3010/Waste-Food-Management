package com.mm.foodmanagment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import com.mm.foodmanagment.ui.auth.RegistrationActivity
import com.mm.foodmanagment.ui.main.DashboardActivity
import com.mm.foodmanagment.utils.BaseActivity

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // set fullscreen in android r
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        @Suppress("DEPRECATION")
        Handler().postDelayed(
                {
                    if (checkPrefData()!!) {
                        //launch the main activity
                        startActivity(
                                Intent(this@SplashActivity,
                                        DashboardActivity::class.java)
                        )
                        finish()
                    } else {
                        startActivity(Intent(this@SplashActivity, RegistrationActivity::class.java))
                        finish()
                    }
                }, 3000
        )
    }
}
