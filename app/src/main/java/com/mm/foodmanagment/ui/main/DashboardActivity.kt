package com.mm.foodmanagment.ui.main

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.mm.foodmanagment.R
import com.mm.foodmanagment.ui.auth.RegistrationActivity
import com.mm.foodmanagment.ui.main.addPost.AddPostFragment
import com.mm.foodmanagment.ui.main.users.service.FirebaseService
import com.mm.foodmanagment.utils.BaseActivity
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.coroutines.DelicateCoroutinesApi

class DashboardActivity : BaseActivity() , NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout
    private lateinit var mAppBarConfiguration: AppBarConfiguration
    private lateinit var auth:FirebaseAuth
    private lateinit var uid:String
    @DelicateCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        FirebaseFirestore.setLoggingEnabled(true);
        savePrefsData(true)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        drawer = findViewById(R.id.my_drawer_layout)
        val navViewLeft: NavigationView = findViewById(R.id.nav_view_left)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_main_fragment)
        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        auth= FirebaseAuth.getInstance()
        uid=auth.currentUser?.uid.toString()
        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        fab.setOnClickListener {
            val fragment = AddPostFragment()
            supportFragmentManager.beginTransaction().replace(R.id.nav_main_fragment, fragment)
                .addToBackStack(null).commit()
        }

//        show_notifications.setOnClickListener {
//            val fragment = NotificationFragment()
//            supportFragmentManager.beginTransaction().replace(R.id.nav_main_fragment, fragment)
//                .addToBackStack(null).commit()
//        }

//
//        val type = getUserType()
//        if (type =="0") {
////            saveUserType("0")
//            navView.inflateMenu(R.menu.bottom_nav_menu)
//        }else{
////            saveUserType("1")
//            navView.inflateMenu(R.menu.bottom_nav_menu_helper)
//        }

//            navView.inflateMenu(R.menu.bottom_nav_menu)
        navView.background = null
        navView.menu.getItem(2).isEnabled = true

        toggle.isDrawerIndicatorEnabled = false; //disable "hamburger to arrow" drawable
        toggle.setHomeAsUpIndicator(R.drawable.menu); //set your own

        nav_drawer_btn.setOnClickListener {  drawer.openDrawer(GravityCompat.START) }
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        mAppBarConfiguration = AppBarConfiguration(navController.graph, drawer)
        mAppBarConfiguration = AppBarConfiguration.Builder(R.id.navigation_home)
            .setDrawerLayout(drawer)
            .build()

        NavigationUI.setupWithNavController(navViewLeft, navController)


        navViewLeft.menu.findItem(R.id.nav_logout)
            .setOnMenuItemClickListener {
                drawer.closeDrawer(GravityCompat.START)
                startActivity(Intent(this, RegistrationActivity::class.java))
                finish()
                savePrefsData(false)
                false
            }

//        navViewLeft.menu.findItem(R.id.nav_settings)
//            .setOnMenuItemClickListener {
//                drawer.closeDrawer(GravityCompat.START)
////                startActivity(Intent(this,SettingActivity::class.java))
//
//                false
//            }
        navView.setupWithNavController(navController)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val drawer = findViewById<DrawerLayout>(R.id.my_drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}