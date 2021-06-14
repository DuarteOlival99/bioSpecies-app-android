package com.example.bioSpecies.data.sensors.location

import com.google.android.gms.location.LocationResult

interface OnLocationChangedListener {
    fun onLocationChanged(locationResult: LocationResult)
}

