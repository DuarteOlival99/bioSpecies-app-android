package com.example.bioSpecies.ui.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.location.Location
import android.text.style.StyleSpan
import android.util.Log
import com.example.bioSpecies.data.entity.Photos
import com.example.bioSpecies.ui.viewmodels.viewmodels.SplashScreenViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class Extensions {

    val imageRef = Firebase.storage.reference
    var mAuth: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    var listImages = mutableListOf<Photos>()
    var listImagesUnevaluated = mutableListOf<Photos>()

    fun LocationToLatLng(location: Location) : LatLng {
        return LatLng(location.latitude, location.longitude)
    }

    fun distanceBetweenLatLng(LatLng1Latitude: Double , LatLng1Longitude: Double, LatLng2Latitude: Double , LatLng2Longitude: Double): Double {

        val results = FloatArray(1)
        Location.distanceBetween(    //	distanceBetween(double startLatitude, double startLongitude, double endLatitude, double endLongitude, float[] results)
            LatLng1Latitude, LatLng1Longitude,
            LatLng2Latitude, LatLng2Longitude,
            results
        )
        results to Float
        return metroToKm(results[0])
    }

    fun metroToKm (distance: Float) : Double {

        if (distance > 1000.0f) {
            return (distance / 1000.0f).toDouble()
        } else {
            return (distance / 1000.0f).toDouble()
        }

    }


}