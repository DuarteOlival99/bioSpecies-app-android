package com.example.bioSpecies.ui.fragments

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Optional
import com.example.bioSpecies.R
import com.example.bioSpecies.data.entity.Upload
import com.example.bioSpecies.ui.utils.NavigationManager
import com.example.bioSpecies.ui.viewmodels.viewmodels.ProfileViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.camera_fragment.*
import java.io.*
import java.util.*


class EditProfileFragment : Fragment(){

    private val GALLERY_REQUEST_CODE: Int = 999
    private lateinit var viewModel : ProfileViewModel

    private var mImageView: de.hdodenhof.circleimageview.CircleImageView? = null
    private var mUsername: EditText? = null
    private var mCurrentPassword: EditText? = null
    private var mNewPassword: EditText? = null
    private var mConfirmNewPassword: EditText? = null

    var mAuth: FirebaseAuth? = null
    var currentUser : FirebaseUser? = null
    var mFirebaseStorage: FirebaseStorage? = null
    var mStorageRef: StorageReference? = null
    var mDatabaseStorage: FirebaseDatabase? = null
    var mDatabaseRef: DatabaseReference? = null

    private var mudarFoto = false

    private lateinit var photo: Bitmap

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.edit_profile_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        ButterKnife.bind(this, view)

        Log.i("EPF->OnCreateView", "OnCreateView")
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth!!.currentUser
        mFirebaseStorage = FirebaseStorage.getInstance()
        mStorageRef = mFirebaseStorage!!.reference
        mDatabaseStorage = FirebaseDatabase.getInstance()
        mDatabaseRef = mDatabaseStorage!!.reference

        Log.i("EPF->OnCreate", "OnCreate")
    }

   override fun onStart() {
        super.onStart()


    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @Override
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        Log.i("EPF->OnViewCreated", "OnViewCreated")

        mImageView = view.findViewById(R.id.image_view_perfil)
        mUsername = view.findViewById(R.id.name_input)
        mCurrentPassword = view.findViewById(R.id.current_password_input)
        mNewPassword = view.findViewById(R.id.new_password_input)
        mConfirmNewPassword = view.findViewById(R.id.confirm_new_password_input)

        // Read from the database
        mDatabaseRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once

                //atualizar a foto do utilizador
                var imageRef = mStorageRef?.child("images/UserProfileImages/" + currentUser?.uid + ".png")
                val ONE_MEGABYTE: Long = 1024 * 1024
                imageRef?.getBytes(ONE_MEGABYTE)?.addOnSuccessListener(OnSuccessListener<ByteArray?> {
                    //converter ByteArray em Bitmap
                    var bmp_foto = BitmapFactory.decodeByteArray(it, 0, it.size)

                    //colocar bitmap na imageview
                    mImageView?.setImageBitmap(bmp_foto!!) //para a foto do fragmento do EditarPerfil

                    Log.i("EPF->OnStart", "Deu load da imagem")
                })?.addOnFailureListener(OnFailureListener {
                    //Toast.makeText(context, "Erro a fazer load da imagem de perfil no editarPerfil", Toast.LENGTH_LONG).show()
                    Log.i("EPF->OnStart", "Erro ao dar load da imagem")
                })
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    @OnClick(R.id.button_edit_profile)
    fun onClickEdit(view: View){

        val username = mUsername?.text.toString().trim()
        val currentPassword = mCurrentPassword?.text.toString().trim()
        val newPassword = mNewPassword?.text.toString().trim()
        val confirmNewPassword = mConfirmNewPassword?.text.toString().trim()

        //alterar foto, username e password
        if (currentUser != null) {

            if (!username.isEmpty()) {  //se o campo do username não estiver vazio, mudar username
                //guardar username
                var userRef = mDatabaseRef?.child("Users")?.child(currentUser?.uid.toString()) //referencia para os users da real time database
                userRef?.child("Username")?.setValue(username) //atualiza o valor do Username no RealTime Storage
            }

            if (!currentPassword.isEmpty()) { //se password nao estah vazia, verifica se os outras campos da password estão bem. se estiver vazia nao faz nada

                if (newPassword.isEmpty() || confirmNewPassword.isEmpty() || !newPassword.equals(confirmNewPassword)) {
                    //mensagem de erro
                    Toast.makeText(context, "Erro na nova password", Toast.LENGTH_LONG).show()
                } else {
                    //guardar nova password
                    val credential = EmailAuthProvider.getCredential(currentUser?.email!!, currentPassword)
                    currentUser?.reauthenticate(credential)?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            //update da password
                            currentUser!!.updatePassword(newPassword)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Log.i("EPF->onClickEdit", "Guardou nova password")
                                        }
                                    }

                        } else {
                            Toast.makeText(context, "Erro: alteracao da password foi possivel", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

            if (mudarFoto) {
                //guarda foto adicionada ao storage  -> images/UserProfileImages/userid.png
                guardarFotoPerfil()
            }else{
                fragmentManager?.let { NavigationManager.goToProfile(it) }
            }
            mudarFoto = false


        } else {
            Toast.makeText(context, "Erro: user é null", Toast.LENGTH_LONG).show()
        }

    }


    @Optional
    @OnClick(R.id.image_view_perfil)
    fun onClickNewLugar(view: View){

        showDialog("Merdinha", context)

    }

    private fun showDialog(title: String, context: Context?) {
        val dialog = context?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.custom_dialog)
        val body = dialog?.findViewById(R.id.customDialogTitle) as TextView
        //body.text = title
        val camera = dialog?.findViewById(R.id.escolherCamera) as TextView
        val galeria = dialog?.findViewById(R.id.escolherGaleria) as TextView
        camera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA), EditProfileFragment.MY_CAMERA_PERMISSION_CODE)
            } else {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, EditProfileFragment.CAMERA_REQUEST)
            }

            dialog.dismiss()
        }

        galeria.setOnClickListener {
            //Create an Intent with action as ACTION_PICK
            val intent = Intent(Intent.ACTION_PICK)
            // Sets the type as image/*. This ensures only components of type image are selected
            intent.type = "image/*"
            //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            // Launching the Intent
            startActivityForResult(intent, GALLERY_REQUEST_CODE)

            dialog.dismiss()
        }

        dialog.show()

    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == EditProfileFragment.MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, getText(R.string.com_permissao), Toast.LENGTH_LONG).show()
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, EditProfileFragment.CAMERA_REQUEST)
            } else {
                Toast.makeText(context, getText(R.string.sem_permissao), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EditProfileFragment.CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            //mImageView?.setImageResource(R.drawable.ic_perfil)

            photo = data!!.extras?.get("data") as Bitmap
            mImageView?.setImageBitmap(photo)

            mudarFoto = true
        }
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            //data.getData returns the content URI for the selected Image
            val selectedImage: Uri? = data?.data

            photo = getBitmapFormUri(this.requireActivity(), selectedImage) as Bitmap
            mImageView?.setImageBitmap(photo)

            mudarFoto = true
        }
    }

    private fun guardarFotoPerfil(){
        val outputStream: ByteArrayOutputStream = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val data = outputStream.toByteArray()

        val path: String = "images/UserProfileImages/" + currentUser!!.uid + ".png"
        mStorageRef = mFirebaseStorage?.getReference(path)

        val metadata: StorageMetadata = StorageMetadata.Builder()
                .setCustomMetadata("user", currentUser?.uid)
                .build()

        val uploadTask: UploadTask? = mStorageRef?.putBytes(data, metadata)

        uploadTask?.addOnFailureListener(context as Activity) { e ->
            Toast.makeText(context, "Upload Error: " + e.message, Toast.LENGTH_LONG).show()
        }?.addOnSuccessListener(context as Activity,
                OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    val uri: Task<Uri> = taskSnapshot.storage.downloadUrl
                    while (!uri.isComplete);
                    val url: Uri = uri.result!!
                    Toast.makeText(context, "Upload Success", Toast.LENGTH_LONG).show()
                    val upload = Upload(
                            path,
                            url.toString(),
                            currentUser!!.uid
                    )

                    Log.i("FBApp1_URL ", url.toString())

                    if (uploadTask.isComplete) {
                        fragmentManager?.let { NavigationManager.goToProfile(it) }
                    }
                })
    }

    companion object {
        private const val CAMERA_REQUEST = 1888
        private const val MY_CAMERA_PERMISSION_CODE = 100
    }


    fun getBitmapFormUri(ac: Activity, uri: Uri?): Bitmap? {
        var input: InputStream? = ac.contentResolver.openInputStream(uri!!)
        val onlyBoundsOptions = BitmapFactory.Options()
        onlyBoundsOptions.inJustDecodeBounds = true
        onlyBoundsOptions.inDither = true //optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888 //optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions)
        input?.close()
        val originalWidth = onlyBoundsOptions.outWidth
        val originalHeight = onlyBoundsOptions.outHeight
        if (originalWidth == -1 || originalHeight == -1) return null
        //Image resolution is based on 480x800
        val hh = 800f //The height is set as 800f here
        val ww = 480f //Set the width here to 480f
        //Zoom ratio. Because it is a fixed scale, only one data of height or width is used for calculation
        var be = 1 //be=1 means no scaling
        if (originalWidth > originalHeight && originalWidth > ww) { //If the width is large, scale according to the fixed size of the width
            be = (originalWidth / ww).toInt()
        } else if (originalWidth < originalHeight && originalHeight > hh) { //If the height is high, scale according to the fixed size of the width
            be = (originalHeight / hh).toInt()
        }
        if (be <= 0) be = 1
        //Proportional compression
        val bitmapOptions = BitmapFactory.Options()
        bitmapOptions.inSampleSize = be //Set scaling
        bitmapOptions.inDither = true //optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888 //optional
        input = ac.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions)
        input?.close()
        return compressImage(bitmap) //Mass compression again
    }

    fun compressImage(image: Bitmap?): Bitmap? {
        val baos = ByteArrayOutputStream()
        image!!.compress(Bitmap.CompressFormat.JPEG, 100, baos) //Quality compression method, here 100 means no compression, store the compressed data in the BIOS
        var options = 100
        while (baos.toByteArray().size / 1024 > 100) {  //Cycle to determine if the compressed image is greater than 100kb, greater than continue compression
            baos.reset() //Reset the BIOS to clear it
            //First parameter: picture format, second parameter: picture quality, 100 is the highest, 0 is the worst, third parameter: save the compressed data stream
            image.compress(Bitmap.CompressFormat.JPEG, options, baos) //Here, the compression options are used to store the compressed data in the BIOS
            options -= 10 //10 less each time
        }
        val isBm = ByteArrayInputStream(baos.toByteArray()) //Store the compressed data in ByteArrayInputStream
        return BitmapFactory.decodeStream(isBm, null, null)
    }



}