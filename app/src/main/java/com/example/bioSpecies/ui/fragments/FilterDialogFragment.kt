package com.example.bioSpecies.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.bioSpecies.R
import com.example.bioSpecies.ui.viewmodels.viewmodels.MapViewModel
import kotlinx.android.synthetic.main.fragment_filter_dialog.*

import kotlinx.android.synthetic.main.fragment_filter_dialog.view.*

class FilterDialogFragment : DialogFragment(){

    var distance : Double = 0.0
    private lateinit var viewModel: MapViewModel
    private lateinit var seekBar: SeekBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_filter_dialog, container, false)

        seekBar = view.SeekBar
        if(seekBar.progress == 0){
            distance = 0.0
            view.distanceValue.text = getString(R.string.any)
        }else{
            distance = seekBar.progress.toDouble()
            distance /= 10.0
            view.distanceValue.text = "${getString(R.string.less)} ${distance} Km"
        }

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (progress == 0){
                    distance = 0.0
                    view.distanceValue.text = getString(R.string.any)
                }else{
                    distance = progress.toDouble()
                    distance /= 10.0
                    view.distanceValue.text = "${getString(R.string.less)} ${distance} Km"
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        ButterKnife.bind(this,view)
        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
        return view
    }

    override fun onStart() {
        super.onStart()

        common.isChecked = viewModel.getFilterCommon()
        not_rare.isChecked = viewModel.getFilterNotRare()
        rare.isChecked = viewModel.getFilterRare()
        very_rare.isChecked = viewModel.getFilterVeryRare()

        seekBar.progress = (viewModel.getFilterDistance() * 10.0 ).toInt()
    }

    override fun onResume() {
        super.onResume()
        val params: WindowManager.LayoutParams? = dialog?.window?.attributes
        params?.width = RelativeLayout.LayoutParams.MATCH_PARENT
        params?.height = RelativeLayout.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    override fun dismiss() {
        Log.i("dismiss", "dismissFiltros")
        val parentFrag: MapFragment = this.parentFragment as MapFragment
        parentFrag.atualizaMarkers()
        super.dismiss()
    }

    @OnClick(R.id.apply)
    fun onClickApply(view: View){
        viewModel.setFilterCommon(common.isChecked)
        viewModel.setFilterNotRare(not_rare.isChecked)
        viewModel.setFilterRare(rare.isChecked)
        viewModel.setFilterVeryRare(very_rare.isChecked)
        viewModel.setFilterDistance(distance)

        viewModel.atualizaListaAnimaisFilter(common.isChecked,not_rare.isChecked,rare.isChecked,very_rare.isChecked, distance)

        dismiss()
    }

    @OnClick(R.id.clear)
    fun onClickClear(view: View){
        viewModel.setFilterCommon(true)
        viewModel.setFilterNotRare(true)
        viewModel.setFilterRare(true)
        viewModel.setFilterVeryRare(true)
        viewModel.setFilterDistance(0.0)


        viewModel.atualizaListaAnimaisFilter(common = true, notRare = true, rare = true, veryRare = true, 0.0)

        dismiss()
    }

}