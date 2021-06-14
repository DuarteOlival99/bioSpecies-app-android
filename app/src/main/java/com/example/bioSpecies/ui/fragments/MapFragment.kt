package com.example.bioSpecies.ui.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.bioSpecies.R
import com.example.bioSpecies.data.entity.Animal
import com.example.bioSpecies.data.entity.Especies
import com.example.bioSpecies.data.entity.Photos
import com.example.bioSpecies.data.sensors.location.FusedLocation
import com.example.bioSpecies.data.sensors.location.OnLocationChangedListener
import com.example.bioSpecies.ui.adapters.InfoWindowAdapter
import com.example.bioSpecies.ui.utils.Extensions
import com.example.bioSpecies.ui.utils.NavigationManager
import com.example.bioSpecies.ui.viewmodels.viewmodels.MapViewModel
import com.example.bioSpecies.ui.viewmodels.viewmodels.SplashScreenViewModel
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.map_fragment.*
import kotlinx.android.synthetic.main.map_fragment.view.*
import java.util.*


const val REQUEST_CODE = 100

class MapFragment : PermissionedFragment(REQUEST_CODE), OnMapReadyCallback,
        OnLocationChangedListener{

    private lateinit var viewModel: MapViewModel
    lateinit var viewModelSplashScreen: SplashScreenViewModel

    private var map: GoogleMap? = null
    private var location: Location? = null
    private var aproximaBoolean = false

    private var listUploads = listOf<Photos>()
    private var listAnimais = listOf<Animal>()

    private val extensions = Extensions()

    var locationDescoberta = false

    var mAuth: FirebaseAuth? = null
    var currentUser : FirebaseUser? = null
    var mFirebaseStorage: FirebaseStorage? = null
    var mStorageRef: StorageReference? = null
    var mDatabaseStorage: FirebaseDatabase? = null
    var mDatabaseRef: DatabaseReference? = null

    var mProgressBar : ProgressBar? = null
    var mNivelMapa : TextView? = null
    var mFotoPerfil : de.hdodenhof.circleimageview.CircleImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.map_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
        ButterKnife.bind(this, view)
        view.map_view.onCreate(savedInstanceState)
        viewModelSplashScreen = ViewModelProviders.of(this).get(SplashScreenViewModel::class.java)
        mFotoPerfil = view.findViewById(R.id.profile_image_mapa)
        mProgressBar = view.findViewById(R.id.progress_bar_mapa)
        mNivelMapa = view.findViewById(R.id.nivel_user_mapa)

        return view
    }

    override fun onStart() {
        super.onRequestPermissions(
            activity?.baseContext!!, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
        super.onStart()

        currentUser = mAuth!!.currentUser
        // Read from the database
        mDatabaseRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again whenever data at this location is updated.

                val userID : String = currentUser?.uid.toString()
                val childUser = dataSnapshot.child("Users").child(userID)

                //atualizar foto
                var imageRef = mStorageRef?.child("images/UserProfileImages/" + currentUser?.uid + ".png")
                val ONE_MEGABYTE: Long = 1024 * 1024
                imageRef?.getBytes(ONE_MEGABYTE)?.addOnSuccessListener(OnSuccessListener<ByteArray?> {
                    //converter ByteArray em Bitmap
                    var bmp_foto = BitmapFactory.decodeByteArray(it, 0, it.size)

                    //colocar bitmap na imageview
                    mFotoPerfil?.setImageBitmap(bmp_foto!!)
                })?.addOnFailureListener(OnFailureListener {
                    //Toast.makeText(context, "Erro a fazer load da imagem de perfil no mapa", Toast.LENGTH_LONG).show()
                })


                //atualizar progresso
                val xpAtual: Int = childUser.child("Nivel").child("xp_atual").value.toString().toInt()
                val xpMax: Int = childUser.child("Nivel").child("xp_max").value.toString().toInt()
                val progresso: Int = xpAtual * 100 / xpMax
                mProgressBar?.progress = progresso

                //atualizar nivel
                val levelUser: String = childUser.child("Nivel").child("nivel_atual").value.toString()
                mNivelMapa?.setText(levelUser)

                Log.i("MA->onStart->onData", "Valores atualizados com sucesso")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.i("MA->onStart->onCanc", "Failed to read value")
            }
        })

        viewModelSplashScreen.atualizaListAnimalsFirebase()
    }

    override fun onDestroy() {
        Log.i("onDestroy_map", "evocado")
        super.onDestroy()
    }


    override fun onRequestPermissionSucces() {
        FusedLocation.registerListener(this)
        map_view.getMapAsync(this)
        map_view.onResume()
        zoom()
    }

    override fun onRequestPermissionFailure() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FusedLocation.start(requireActivity().applicationContext)


        mAuth = FirebaseAuth.getInstance()
        mFirebaseStorage = FirebaseStorage.getInstance()
        mStorageRef = mFirebaseStorage!!.reference
        mDatabaseStorage = FirebaseDatabase.getInstance()
        mDatabaseRef = mDatabaseStorage!!.reference
    }


    fun adicionaMarker(
        position: LatLng,
        title: String,
        snippet: String,
        icon: BitmapDescriptor
    ) {
        val marker = MarkerOptions()
                .position(position)
                .title(title)
                .snippet(snippet)
                .icon(icon)

        map!!.addMarker(marker)
    }

    override fun onMapReady(map: GoogleMap?) {
        this.map = map
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ) {
            return
        }
        this.map!!.isMyLocationEnabled = true

        zoom()
        mapInfoWindow()

        listAnimais = viewModel.getListAnimaisFilter()
        Log.i("MAnimal", listAnimais.toString())

        val markerInfoWindowAdapter =
            InfoWindowAdapter(viewModel, activity as Context, R.layout.marker_map_details, listAnimais)
        map?.setInfoWindowAdapter(markerInfoWindowAdapter)

        map?.setOnInfoWindowClickListener(GoogleMap.OnInfoWindowClickListener {
            fragmentManager?.let { NavigationManager.goToPerfilEspecies(it) }
        })

        desenhaMarkersTestAnimal()
    }

    //Fazer em um fragment sozinho ou ir para historico de capturas
    fun getImages(){
        listUploads = viewModel.getListEvaluationPhotos()
        Log.i("getImagesEvaluated", listUploads.toString())
        if (listUploads.isNotEmpty()){

        } else  {
            //fazer página a dizer que nao tem nada para avaliar
        }
    }

    private fun desenhaMarkersTestAnimal(){
        val iconRed: BitmapDescriptor = BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_RED
        ) //muito raro
        val iconOrange: BitmapDescriptor = BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_ORANGE
        )// raro
        val iconGreen: BitmapDescriptor = BitmapDescriptorFactory.defaultMarker(
            BitmapDescriptorFactory.HUE_GREEN
        )//pouco raro
        val iconYellow: BitmapDescriptor = BitmapDescriptorFactory.defaultMarker(
            BitmapDescriptorFactory.HUE_YELLOW
        )//comum

        if (map != null){
            for (animal in listAnimais){
                when (animal.raridade){
                    0 -> {
                        getDescricaoPerCoordenadas(animal, iconGreen) //comum
                    }
                    1 -> {
                        getDescricaoPerCoordenadas(animal, iconYellow)//pouco raro
                    }
                    2 -> {
                        getDescricaoPerCoordenadas(animal, iconOrange)// raro
                    }
                    3 -> {
                        getDescricaoPerCoordenadas(animal, iconRed)//muito raro
                    }
                }
            }
        }
    }

    fun getDescricaoPerCoordenadas(animal: Animal, icon: BitmapDescriptor){
        for (coordenadasAnimal in animal.localizacao){
            adicionaMarker(
                    LatLng(
                            coordenadasAnimal.latitude,
                            coordenadasAnimal.longitude
                    ), animal.nomeTradicional, getDescricao(animal), icon
            )
        }
    }


    private fun mapInfoWindow(){
        map!!.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(arg0: Marker?): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View? {
                val info = LinearLayout(context)
                info.orientation = LinearLayout.VERTICAL
                val title = TextView(context)
                title.setTextColor(Color.BLACK)
                title.gravity = Gravity.CENTER
                title.setTypeface(null, Typeface.BOLD)
                title.text = marker.title
                val snippet = TextView(context)
                snippet.setTextColor(Color.GRAY)
                snippet.text = marker.snippet
                info.addView(title)
                info.addView(snippet)
                return info
            }
        })
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(
                intrinsicWidth,
                intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    private fun zoom() {
        if (location != null) {
            if (map != null){
                map!!.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                                extensions.LocationToLatLng(location!!), 13f
                        )
                )
                val cameraPosition = CameraPosition.Builder()
                        .target(
                                extensions.LocationToLatLng(location!!)
                        ) // Sets the center of the map to location user
                        .zoom(17f) // Sets the zoom
                        //.bearing(90f) // Sets the orientation of the camera to east
                        //.tilt(40f) // Sets the tilt of the camera to 30 degrees
                        .build() // Creates a CameraPosition from the builder
                map!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
        }
    }

    fun getDescricao(animal: Animal) : String{
        var descricaoResult = ""

        val classe = getString(R.string.classe) + "${animal.classe} \n"
        val nomeCientifico = "Nome Cientifico: ${animal.nomeCientifico} \n"
        var raridade = "Raridade: "
        when (animal.raridade){
            0 -> {
                raridade += getString(R.string.common)
            }
            1 -> {
                raridade += getString(R.string.not_rare)
            }
            2 -> {
                raridade += getString(R.string.rare)
            }
            3 -> {
                raridade += getString(R.string.very_rare)
            }
        }
        raridade += "\n"

        descricaoResult += classe
        descricaoResult += nomeCientifico
        descricaoResult += raridade
        return descricaoResult
    }

    fun goToMap(l: Location){
        val gmmIntentUri = Uri.parse("google.navigation:q=${l.latitude},${l.longitude}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    override fun onLocationChanged(locationResult: LocationResult) {
        val location = locationResult.lastLocation
        this.location = location
//        listAnimais = viewModel.getListAnimaisFilter()
//        atualizaMarkers()
        if (this.location != null) {

//            if( !locationDescoberta ){
//                locationDescoberta = true
//                listAnimais = viewModel.getListAnimaisFilter()
//                atualizaMarkers()
//            }

            location.let { this.location = it }
            if(!aproximaBoolean && location != null){
                zoom()
                aproximaBoolean = true
            }
        } else if ((this.location!!.latitude != location.latitude) ||
                (this.location!!.longitude != location.longitude)
        ) {
            listAnimais = viewModel.getListAnimaisFilter()
            atualizaMarkers()
            location.let { this.location = it }
            //zoom()
        }
    }

    @OnClick(R.id.map_filter)
    fun onClickFilters(view: View) {
        fragmentManager?.let {
            FilterDialogFragment()
                    .show(childFragmentManager, "filters")
        }
    }

    @OnClick(R.id.map_camera)
    fun onClickCamera(view: View){
        fragmentManager?.let { NavigationManager.goToCamera(it) }
    }

    @OnClick(R.id.perfil_linearlayout_mapa)
    fun onClickPerfil(view: View){
        fragmentManager?.let { NavigationManager.goToProfile(it) }
    }

    fun atualizaMarkers() {
        map!!.clear()

        listAnimais = viewModel.getListAnimaisFilter()

        val markerInfoWindowAdapter =
                InfoWindowAdapter(viewModel, activity as Context, R.layout.marker_map_details, listAnimais)
        map?.setInfoWindowAdapter(markerInfoWindowAdapter)

        map?.setOnInfoWindowClickListener(GoogleMap.OnInfoWindowClickListener {
            fragmentManager?.let { NavigationManager.goToPerfilEspecies(it) }
        })

        desenhaMarkersTestAnimal()
    }
}