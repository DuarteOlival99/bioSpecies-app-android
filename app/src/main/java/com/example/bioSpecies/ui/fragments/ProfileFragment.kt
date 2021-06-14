package com.example.bioSpecies.ui.fragments

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.bioSpecies.R
import com.example.bioSpecies.data.entity.Photos
import com.example.bioSpecies.domain.project.MainActivity
import com.example.bioSpecies.ui.utils.NavigationManager
import com.example.bioSpecies.ui.viewmodels.viewmodels.MapViewModel
import com.example.bioSpecies.ui.viewmodels.viewmodels.ProfileViewModel
import com.example.bioSpecies.ui.viewmodels.viewmodels.SplashScreenViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class ProfileFragment : Fragment(){

    private lateinit var viewModel : ProfileViewModel
    lateinit var viewModelSplashScreen: SplashScreenViewModel

    private val imageRef = Firebase.storage.reference

    var mAuth: FirebaseAuth? = null
    var currentUser : FirebaseUser? = null
    var mFirebaseStorage: FirebaseStorage? = null
    var mStorageRef: StorageReference? = null
    var mDatabaseStorage: FirebaseDatabase? = null
    var mDatabaseRef: DatabaseReference? = null

    var mProfileFoto : de.hdodenhof.circleimageview.CircleImageView? = null
    var mProfileUsername : TextView? = null
    var mProfileLevel : TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.profile_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        viewModelSplashScreen = ViewModelProviders.of(this).get(SplashScreenViewModel::class.java)
        ButterKnife.bind(this, view)

        mProfileFoto = view.findViewById(R.id.image_profile)
        mProfileUsername = view.findViewById(R.id.profile_user_name)
        mProfileLevel = view.findViewById(R.id.user_level_valor)

        Log.i("PF->onCreateView", "onCreateView")

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        mFirebaseStorage = FirebaseStorage.getInstance()
        mStorageRef = mFirebaseStorage!!.reference
        mDatabaseStorage = FirebaseDatabase.getInstance()
        mDatabaseRef = mDatabaseStorage!!.reference

        Log.i("PF->onCreate", "onCreate")
    }

   override fun onStart() {
        super.onStart()

       currentUser = mAuth!!.currentUser

       // Read from the database
       mDatabaseRef?.addValueEventListener(object : ValueEventListener {
           override fun onDataChange(dataSnapshot: DataSnapshot) {
               // This method is called once with the initial value and again whenever data at this location is updated.

               val userID : String = currentUser?.uid.toString()
               val childUser = dataSnapshot.child("Users").child(userID)

               //atualizar a foto do utilizador no drawer
               val profile_photo_drawer = (activity as MainActivity).nav_drawer.getHeaderView(0).findViewById(R.id.drawer_photo) as de.hdodenhof.circleimageview.CircleImageView

               //atualizar a foto do utilizador
               var imageRef = mStorageRef?.child("images/UserProfileImages/" + currentUser?.uid + ".png")
               val ONE_MEGABYTE: Long = 1024 * 1024
               imageRef?.getBytes(ONE_MEGABYTE)?.addOnSuccessListener(OnSuccessListener<ByteArray?> {
                   //converter ByteArray em Bitmap
                   var bmp_foto = BitmapFactory.decodeByteArray(it, 0, it.size)

                   //colocar bitmap na imageview
                   mProfileFoto?.setImageBitmap(bmp_foto!!) //para a foto do fragmento do perfil
                   profile_photo_drawer.setImageBitmap(bmp_foto!!) //para a foto do drawer
               })?.addOnFailureListener(OnFailureListener {
                   //Toast.makeText(context, "Erro a fazer load da imagem de perfil no perfil", Toast.LENGTH_LONG).show()
               })


               //atualizar o username do utilizador
               val nomeUser: String = childUser.child("Username").value.toString()
               mProfileUsername?.setText(nomeUser)

               //atualizar nivel do user
               val levelUser: String = childUser.child("Nivel").child("nivel_atual").value.toString()
               mProfileLevel?.setText(levelUser)


               //atualizar desafios completados
               //TODO

               Log.i("PF->onStart->onData", "Valores atualizados com sucesso")
           }

           override fun onCancelled(error: DatabaseError) {
               // Failed to read value
               Log.i("PF->onStart->onCanc", "Failed to read value")
           }
       })

    }

    override fun onDestroy() {
        super.onDestroy()
    }



    fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            // Log exception
            Log.i("getBitmapFromURL_Error", e.toString())
            null
        }
    }


    @OnClick(R.id.edit_profile)
    fun onClickEdit(view: View){
        fragmentManager?.let { NavigationManager.goToEditProfile(it) }
    }

    @OnClick(R.id.button_Statistics)
    fun onClickStatistics(view: View){
        fragmentManager?.let { NavigationManager.goToStatisticsProfile(it) }
    }

    @OnClick(R.id.capture_history, R.id.capture_history_arrow)
    fun onClickCaptureHistory(view: View){
        fragmentManager?.let { NavigationManager.goToCaptureHistory(it) }
    }


}