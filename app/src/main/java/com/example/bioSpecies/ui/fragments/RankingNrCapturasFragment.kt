package com.example.bioSpecies.ui.fragments

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import com.example.bioSpecies.R
import com.example.bioSpecies.ui.viewmodels.viewmodels.RankingViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.caderneta_expression.view.*
import kotlinx.android.synthetic.main.caderneta_fragment.*
import kotlinx.android.synthetic.main.caderneta_group_fragment.*
import kotlinx.android.synthetic.main.ranking_numero_capturas_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class RankingNrCapturasFragment : Fragment(){

    private lateinit var viewModel : RankingViewModel

    val imageRef = Firebase.storage.reference
    var mFirebaseStorage: FirebaseStorage? = null
    var mStorageRef: StorageReference? = null


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.ranking_numero_capturas_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(RankingViewModel::class.java)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mFirebaseStorage = FirebaseStorage.getInstance()
        mStorageRef = mFirebaseStorage!!.reference
    }

   override fun onStart() {
       super.onStart()

       val listRankingNrCapturas = viewModel.getListRankingNrCapturas()
       Log.i("listRankingNrCapturas", listRankingNrCapturas.toString())

       ranking_nr_capturas_thirdPlace_text_value.text = listRankingNrCapturas[0].nrCapturas.toString()
       ranking_nr_capturas_secondPlace_text_value.text = listRankingNrCapturas[1].nrCapturas.toString()
       ranking_nr_capturas_firstPlace_text_value.text = listRankingNrCapturas[2].nrCapturas.toString()

       ranking_nr_capturas_my_text.text = viewModel.getMyNrCapturas()


       //atualizar a foto do utilizador first position
       var imageRef1 = mStorageRef?.child("images/UserProfileImages/" + listRankingNrCapturas[2].userUUID + ".png")
       var imageRef2 = mStorageRef?.child("images/UserProfileImages/" + listRankingNrCapturas[1].userUUID + ".png")
       var imageRef3 = mStorageRef?.child("images/UserProfileImages/" + listRankingNrCapturas[0].userUUID + ".png")

       val ONE_MEGABYTE: Long = 1024 * 1024


       imageRef1?.getBytes(ONE_MEGABYTE)?.addOnSuccessListener(OnSuccessListener<ByteArray?> {
           //converter ByteArray em Bitmap
           var bmp_foto = BitmapFactory.decodeByteArray(it, 0, it.size)
           //colocar bitmap na imageview
           ranking_nr_capturas_firstPlace_image?.setImageBitmap(bmp_foto!!) //para a foto do fragmento do perfil
       })?.addOnFailureListener(OnFailureListener {
           //Toast.makeText(context, "Erro a fazer load da imagem de perfil no perfil", Toast.LENGTH_LONG).show()
       })

       imageRef2?.getBytes(ONE_MEGABYTE)?.addOnSuccessListener(OnSuccessListener<ByteArray?> {
           //converter ByteArray em Bitmap
           var bmp_foto = BitmapFactory.decodeByteArray(it, 0, it.size)
           //colocar bitmap na imageview
           ranking_nr_capturas_secondPlace_image?.setImageBitmap(bmp_foto!!) //para a foto do fragmento do perfil
       })?.addOnFailureListener(OnFailureListener {
           //Toast.makeText(context, "Erro a fazer load da imagem de perfil no perfil", Toast.LENGTH_LONG).show()
       })

       imageRef3?.getBytes(ONE_MEGABYTE)?.addOnSuccessListener(OnSuccessListener<ByteArray?> {
           //converter ByteArray em Bitmap
           var bmp_foto = BitmapFactory.decodeByteArray(it, 0, it.size)
           //colocar bitmap na imageview
           ranking_nr_capturas_thirdPlace_image?.setImageBitmap(bmp_foto!!) //para a foto do fragmento do perfil
       })?.addOnFailureListener(OnFailureListener {
           //Toast.makeText(context, "Erro a fazer load da imagem de perfil no perfil", Toast.LENGTH_LONG).show()
       })

   }

    override fun onDestroy() {
        super.onDestroy()
    }

//    fun getImages(){
//            imageRef.child("images/UserProfileImages/M8wsRWiLxNgmof6SL2iXNF1JKAX2").downloadUrl.addOnSuccessListener {
//                // Got the download URL for 'users/me/profile.png'
//                val url =
//            }.addOnFailureListener {
//                // Handle any errors
//            }
//    }
//
//    private fun getImages2() = CoroutineScope(Dispatchers.IO).launch {
//        try {
//            val images = imageRef.child("images/UserProfileImages/M8wsRWiLxNgmof6SL2iXNF1JKAX2").listAll().await()
//            Log.i("images2", images.toString())
//            for(image in images.items) {
//                val url = image.downloadUrl.await()
//                Log.i("URLTESTE2", url.toString())
//            }
//
//        } catch (e: Exception) {
//
//        }
//    }



}