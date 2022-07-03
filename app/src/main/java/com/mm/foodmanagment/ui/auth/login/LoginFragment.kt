package com.mm.foodmanagment.ui.auth.login

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mm.foodmanagment.R
import com.mm.foodmanagment.utils.hideSoftKeyboard
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment : Fragment() {
    private var viewModel: LoginViewModel? = LoginViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_login, container, false)

        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(requireActivity().application)
        )[LoginViewModel::class.java]

        viewModel!!.getUserData()?.observe(requireActivity(),
            { firebaseUser ->
                if (firebaseUser != null) {

                }
            })

        root.password.setOnEditorActionListener(TextView.OnEditorActionListener { it, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                var usernameTxt = root.username.text.toString().trim()
                if (usernameTxt == "admin" || usernameTxt == "Admin" || usernameTxt == "admin@gmail.com" || usernameTxt == "admin@admin.com"
                    || usernameTxt == "Admin@gmail.com" || usernameTxt == "Admin@admin.com"){
                    usernameTxt = "admin@gmial.com"
                }else{
                    usernameTxt = root.username.text.toString().trim()
                }
                viewModel!!.loginEmail(
                    usernameTxt,
                    root.password.text.toString().trim(),
                    requireActivity()
                )

                it.hideSoftKeyboard()

                root.loginBtn.isEnabled = false
                @Suppress("DEPRECATION")
                Handler().postDelayed(
                    {
                        root.loginBtn.isEnabled = true
                    }, 4000
                )

                true
            } else false
        })
        root.loginBtn.setOnClickListener {
            var usernameTxt = root.username.text.toString().trim()
            if (usernameTxt == "admin" || usernameTxt == "Admin" || usernameTxt == "admin@gmail.com" || usernameTxt == "admin@admin.com"
                || usernameTxt == "Admin@gmail.com" || usernameTxt == "Admin@admin.com"){
                usernameTxt = "admin@gmail.com"
            }else{
                usernameTxt = root.username.text.toString().trim()
            }

            viewModel!!.loginEmail(
                usernameTxt,
                root.password.text.toString().trim(),
                requireActivity()
            )

            it.hideSoftKeyboard()

            root.loginBtn.isEnabled = false
            @Suppress("DEPRECATION")
            Handler().postDelayed(
                {
                    root.loginBtn.isEnabled = true
                }, 4000
            )

        }

        root.signBtn.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_login_to_navigation_signup)
        }

        return root
    }

}