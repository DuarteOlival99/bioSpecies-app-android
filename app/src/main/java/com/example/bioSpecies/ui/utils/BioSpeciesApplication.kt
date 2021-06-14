package com.example.bioSpecies.ui.utils

import android.app.Application
import com.example.bioSpecies.data.sensors.battery.Battery

class BioSpeciesApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Battery.start(this)
    }
}