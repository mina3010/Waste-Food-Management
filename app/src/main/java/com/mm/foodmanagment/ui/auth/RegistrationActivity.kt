package com.mm.foodmanagment.ui.auth

import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import com.mm.foodmanagment.R
import com.mm.foodmanagment.utils.BaseActivity

class RegistrationActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        FirebaseFirestore.setLoggingEnabled(true);

    }
}