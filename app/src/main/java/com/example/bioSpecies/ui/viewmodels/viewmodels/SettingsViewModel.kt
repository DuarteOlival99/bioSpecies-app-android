package com.example.bioSpecies.ui.viewmodels.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.bioSpecies.ui.viewmodels.logic.SettingsLogic


class SettingsViewModel ( application: Application ) : AndroidViewModel (application) {
    private val settingsLogic = SettingsLogic()



}