package com.example.bioSpecies.ui.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
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
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Optional
import com.example.bioSpecies.R
import com.example.bioSpecies.data.entity.PhotoEvaluation
import com.example.bioSpecies.data.entity.Photos
import com.example.bioSpecies.data.entity.Upload
import com.example.bioSpecies.ui.viewmodels.viewmodels.CameraViewModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.camera_fragment.*
import java.io.ByteArrayOutputStream
import java.util.*

class CameraFragment : Fragment(){
    private lateinit var viewModel: CameraViewModel
    lateinit var currentPhotoPath: String
    private var imageView: ImageView? = null
    private var imageCount = 0
    private lateinit var photo: Bitmap

    private var mFirebaseStorage = FirebaseStorage.getInstance()
    private var mDatabaseStorage = FirebaseDatabase.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.camera_fragment, container, false)
        imageView = view!!.findViewById<View>(R.id.imageView1) as ImageView
        viewModel = ViewModelProviders.of(this).get(CameraViewModel::class.java)
        ButterKnife.bind(this, view)
        return view

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

        val listAnimalsNames = viewModel.getlListAnimalsNames()
        val adapter = ArrayAdapter<String>(context as Activity , android.R.layout.simple_dropdown_item_1line, listAnimalsNames)
        foto_name_input.setAdapter(adapter)

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @Optional
    @OnClick(R.id.fabNewFoto)
    fun onClickNewLugar(view: View){
        if (checkSelfPermission(
                requireActivity(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(android.Manifest.permission.CAMERA),
                CameraFragment.MY_CAMERA_PERMISSION_CODE
            )
        } else {
            val cameraIntent =
                Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CameraFragment.CAMERA_REQUEST)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, getText(R.string.com_permissao), Toast.LENGTH_LONG).show()
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            } else {
                Toast.makeText(context, getText(R.string.sem_permissao), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            photo = data!!.extras?.get("data") as Bitmap
            imageView!!.setImageBitmap(photo)
            imageCount = 1
        }
    }

    fun validarNomeEspecie() : Boolean{
        if (foto_name_input.text!!.isEmpty() || verificarNomeAnimal()){
            foto_name_input.error = "É necessario introduzir um nome de animal valido"
            return false
        }else{
            return true
        }
    }

    fun verificarNomeAnimal() : Boolean{
        val nomeIntroduzido = foto_name_input.text.toString()
        val list = viewModel.getlListAnimalsNames()
        if (list.contains(nomeIntroduzido)){
            return false
        }
        return true
    }

    fun validarFoto() : Boolean {
        return if (imageCount == 0){
            popUpSemImagem()
            false
        }else{
            true
        }
    }

    fun popUpSemImagem() {
        //messagem de erro a dizer que nao existe aquela equipa
        val builder = AlertDialog.Builder(context)
        builder.setMessage(getString(R.string.sem_foto_camera))
                .setPositiveButton(getString(R.string.ok),
                        DialogInterface.OnClickListener { dialog, id ->
                        })
        // Create the AlertDialog object and return it
        builder.create()
        builder.show()
    }

    @OnClick(R.id.dont_know_name)
    fun onClickDontKnowName(view: View) {
        if (validarFoto()){
            val outputStream: ByteArrayOutputStream = ByteArrayOutputStream()
            photo.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val data = outputStream.toByteArray()

            var mAuth: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            var inputNameAnimal = foto_name_input.text.toString() + "-||-" + UUID.randomUUID()

            val path: String = "images/camera/photosWithNoName/"  + inputNameAnimal + ".png"
            val mStorageRef: StorageReference = mFirebaseStorage.getReference(path)
            val mDatabaseRef: DatabaseReference =
                mDatabaseStorage.getReference("image/camera/photosWithNoName/" + mAuth?.uid+ "/")

            val metadata: StorageMetadata = StorageMetadata.Builder()
                .setCustomMetadata("user", mAuth?.uid)
                .setCustomMetadata("avaliada?", "false")
                .setCustomMetadata("resultado", "-1")
                .setCustomMetadata("userJaViu?", "false")
                .setCustomMetadata("nomesAtribuidos", "")
                .setCustomMetadata("userNomesAtribuidos", "")
                .build()

            val uploadTask: UploadTask = mStorageRef.putBytes(data, metadata)

            uploadTask.addOnFailureListener(context as Activity) { e ->
                Toast.makeText(
                    context, "Upload Error: " +
                            e.message, Toast.LENGTH_LONG
                ).show()
            }.addOnSuccessListener(context as Activity,
                OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot -> //Uri url = taskSnapshot.getDownloadUrl();
                    val uri: Task<Uri> = taskSnapshot.storage.downloadUrl
                    while (!uri.isComplete);
                    val url: Uri = uri.result!!
                    Toast.makeText(
                        context, "Upload Success", Toast.LENGTH_LONG
                    ).show()
                    val upload = Upload(
                        path,
                        url.toString(),
                        foto_name_input.text.toString()
                    )
                    //mDatabaseRef.push().setValue(upload)
                    Log.i("FBApp1_URL ", url.toString())

                    viewModel.addMoreOneCapture()
                    var listPhotosEvaluation = mutableListOf<PhotoEvaluation>()
                    val imageUnevaluated = Photos(inputNameAnimal,null, url.toString(), foto_name_input.text.toString(), listPhotosEvaluation, mAuth?.uid.toString(), photo, false, -1, false)
                    viewModel.addUnevaluatedPhoto(imageUnevaluated)

                    imageView1.setImageResource(R.drawable.ic_reload)
                    foto_name_input.setText("")
                })
        }
    }

    @OnClick(R.id.send_button)
    fun onClickSend(view: View){
        if (validarNomeEspecie() && validarFoto()){
            val outputStream: ByteArrayOutputStream = ByteArrayOutputStream()
            photo.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val data = outputStream.toByteArray()

            var mAuth: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            var inputNameAnimal = foto_name_input.text.toString() + "-||-" + UUID.randomUUID()

            val path: String = "images/camera/unevaluatedPhotos/"  + inputNameAnimal + ".png"
            val mStorageRef: StorageReference = mFirebaseStorage.getReference(path)
            val mDatabaseRef: DatabaseReference =
                    mDatabaseStorage.getReference("image/camera/unevaluatedPhotos/" + mAuth?.uid+ "/")

            val metadata: StorageMetadata = StorageMetadata.Builder()
                    .setCustomMetadata("user", mAuth?.uid)
                    .setCustomMetadata("avaliada?", "false")
                    .setCustomMetadata("resultado", "-1")
                    .setCustomMetadata("userJaViu?", "false")
                    .build()

            val uploadTask: UploadTask = mStorageRef.putBytes(data, metadata)

            uploadTask.addOnFailureListener(context as Activity) { e ->
                Toast.makeText(
                        context, "Upload Error: " +
                        e.message, Toast.LENGTH_LONG
                ).show()
            }.addOnSuccessListener(context as Activity,
                    OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot -> //Uri url = taskSnapshot.getDownloadUrl();
                        val uri: Task<Uri> = taskSnapshot.storage.downloadUrl
                        while (!uri.isComplete);
                        val url: Uri = uri.result!!
                        Toast.makeText(
                                context, "Upload Success", Toast.LENGTH_LONG
                        ).show()
                        val upload = Upload(
                                path,
                                url.toString(),
                                foto_name_input.text.toString()
                        )
                        //mDatabaseRef.push().setValue(upload)
                        Log.i("FBApp1_URL ", url.toString())

                        viewModel.addMoreOneCapture()
                        var listPhotosEvaluation = mutableListOf<PhotoEvaluation>()
                        val imageUnevaluated = Photos(inputNameAnimal,null, url.toString(), foto_name_input.text.toString(), listPhotosEvaluation, mAuth?.uid.toString(), photo, false, -1, false)
                        viewModel.addUnevaluatedPhoto(imageUnevaluated)

                        imageView1.setImageResource(R.drawable.ic_reload)
                        foto_name_input.setText("")
                    })
        }
    }

    // convert from bitmap to byte array
    fun getBytes(bitmap: Bitmap): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, stream)
        return stream.toByteArray()
    }

    // convert from byte array to bitmap
    fun getImage(image: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(image, 0, image.size)
    }

    companion object {
        private const val CAMERA_REQUEST = 1888
        private const val MY_CAMERA_PERMISSION_CODE = 100
    }


}