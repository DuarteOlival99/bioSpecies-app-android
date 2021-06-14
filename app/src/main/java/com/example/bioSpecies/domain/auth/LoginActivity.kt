package com.example.bioSpecies.domain.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bioSpecies.R
import com.example.bioSpecies.ui.utils.NavigationManager

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        NavigationManager.goToLogin(
                supportFragmentManager
        )
    }

}
