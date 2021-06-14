package com.example.bioSpecies.ui.fragments

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.bioSpecies.R
import com.example.bioSpecies.ui.viewmodels.viewmodels.SettingsViewModel
import kotlinx.android.synthetic.main.settings_fragment.*
import yuku.ambilwarna.AmbilWarnaDialog
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener
import androidx.lifecycle.ViewModelProviders


class SettingsFragment : Fragment() {

    private lateinit var viewModel : SettingsViewModel
    private lateinit var bitmap: Bitmap
    private var mDefaultColor = Color.WHITE

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.settings_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        //cor de background
       // layout_settings.setBackgroundColor(viewModel.getBackgrondColor()!!)
        //darkMode

        //switchButtondark.isChecked = viewModel.getDarkModeBoolean()
        //switchButtondarkAutomatico.isChecked = viewModel.getDarkModeAutomatico()

//        val pref = PreferenceManager.getDefaultSharedPreferences(context)
//        pref.apply {
//            val backgroundColor = getInt("BACKGROUNDCOLOR", R.color.white)
//            settings.setBackgroundColor(backgroundColor)
//            mDefaultColor = backgroundColor
//        }

    }

    @OnClick(R.id.switchButtondark)
    fun onClickSwitchButtondark(view: View){
//        if (switchButtondark.isChecked){
//            AppCompatDelegate.setDefaultNightMode(
//                AppCompatDelegate.MODE_NIGHT_YES
//            )
//            //viewModel.setDarkModeBoolean(true)
//        }else{
//           // Log.i("setDarkModeBoolean", viewModel.getDarkModeBoolean().toString())
//            AppCompatDelegate.setDefaultNightMode(
//                AppCompatDelegate.MODE_NIGHT_NO
//            )
//           // viewModel.setDarkModeBoolean(false)
//            //Log.i("setDarkModeBoolean", viewModel.getDarkModeBoolean().toString())
//        }
    }

    @OnClick(R.id.switchButtondarkAutomatico)
    fun onClickSwitchButtondarkAutomatico(view: View){
        if (switchButtondarkAutomatico.isChecked){
          //  viewModel.setDarkModeAutomatico(true)
        }else{
          //  viewModel.setDarkModeAutomatico(false)
        }
    }


//    private fun color(){
//        image_select_color.isDrawingCacheEnabled = true
//        image_select_color.buildDrawingCache(true)
//
//        //on touch listener on image
//        image_select_color.setOnTouchListener { v, event ->
//            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE){
//
//                bitmap = image_select_color.drawingCache
//                val pixel = bitmap.getPixel(event.x.toInt(), event.y.toInt())
//
//                //Get RGB values from touched pixel
//                val r = Color.red(pixel)
//                val g = Color.green(pixel)
//                val b = Color.blue(pixel)
//
//                //get HEX value from ttouched pixel
//                val hex = "#" + Integer.toHexString(pixel)
//
//                //set background color to view
//                color_view.setBackgroundColor(Color.rgb(r,g,b))
//                //set Hext and RGB values to text view
//                result_tv.text = "RGB: $r, $g, $b \nHEX: $hex"
//
//            }
//            true
//        }
//    }


}