package com.sagarclientapp.ui.profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.sagarclientapp.R
import com.sagarclientapp.api.Response
import com.sagarclientapp.base.BaseActivity
import com.sagarclientapp.databinding.ActivityContactUsBinding
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.ui.home.HomeActivity
import com.sagarclientapp.ui.profile.viewmodels.ContactUsViewModel
import com.sagarclientapp.utils.AppConstants
import com.sagarclientapp.utils.SharedPref
import com.sagarclientapp.utils.Util.launchActivity
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream


import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ContactUsActivity : BaseActivity() {

    lateinit var binding: ActivityContactUsBinding
    var file1: File? = null
    var file2: File? = null
    var file3: File? = null
    private val viewModels: ContactUsViewModel by viewModels<ContactUsViewModel>()

    companion object {
        const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityContactUsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUis()
    }

    private fun initUis() = binding.apply {

        //checkCameraPermissions()
        contactUsHeader.headerTitle.text = getString(R.string.contact_us)

        contactUsHeader.ivBack.setOnClickListener {
            finish()
        }
        llAttach.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this@ContactUsActivity,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this@ContactUsActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Check if we should show the rationale for camera permission
                if (ActivityCompat.shouldShowRequestPermissionRationale(this@ContactUsActivity, Manifest.permission.CAMERA) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this@ContactUsActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ) {
                    // Show rationale dialog
                    showPermissionRationaleDialog()
                } else {
                    // Directly request permission
                    ActivityCompat.requestPermissions(
                        this@ContactUsActivity,
                        arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        CAMERA_PERMISSION_REQUEST_CODE
                    )
                }
            }
            else {
                showCameraDialog()
            }

        }
        btSubmitContact.setOnClickListener {
            if (binding.etMessage.text.toString().equals("")) {
                showToast(getString(R.string.please_enter_something))
            } else {
                uploadFile()
            }
        }
        ivclose1.setOnClickListener {
            rl1.visibility = View.INVISIBLE

            file1 = null
            rlAttach.visibility = View.VISIBLE
            llAttach.visibility = View.VISIBLE


            if (file1 == null && file2 == null && file3 == null) {
                llImages.visibility = View.GONE
            }
        }
        ivclose2.setOnClickListener {
            rl2.visibility = View.INVISIBLE
            file2 = null
            rlAttach.visibility = View.VISIBLE
            llAttach.visibility = View.VISIBLE
            if (file1 == null && file2 == null && file3 == null) {
                llImages.visibility = View.GONE
            }

        }
        ivclose3.setOnClickListener {
            rl3.visibility = View.INVISIBLE
            file3 = null
            rlAttach.visibility = View.VISIBLE
            llAttach.visibility = View.VISIBLE
            if (file1 == null && file2 == null && file3 == null) {
                llImages.visibility = View.GONE
            }

        }

        apisObservers()
    }

    fun showCameraDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_camera)
        val btGallery: Button = dialog.findViewById(R.id.btGallery)
        val btCamera: Button = dialog.findViewById(R.id.btCamera)
        val ivCancel: ImageView = dialog.findViewById(R.id.ivCancel)
        btCamera.setOnClickListener {

            openCamera()
            dialog.dismiss()
        }
        btGallery.setOnClickListener {
            openGallery()
            dialog.dismiss()
        }
        ivCancel.setOnClickListener {
            dialog.cancel()
        }


        val window = dialog.window
        if (window != null) {
            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
        dialog.show()
    }

   /* fun checkCameraPermissions() {
        // Request camera permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted
                    // openCamera()
                } else {

                    // Permission denied
                    //showToast(getString(R.string.camera_permission_is_required_to_use_this_feature))

                }
                return
            }
        }
    }*/

    fun checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Check if we should show the rationale for camera permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ) {
                // Show rationale dialog
                showPermissionRationaleDialog()
            } else {
                // Directly request permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    CAMERA_PERMISSION_REQUEST_CODE
                )
            }
        } else {
            // Permission already granted, proceed with camera access
            //openCamera()
        }
    }
    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle(getString(R.string.camera_permission_required))
            .setMessage(getString(R.string.this_app_needs_camera_and_storage_access_to_function_properly_please_allow_these_permissions))
            .setPositiveButton(getString(R.string.allow)) { _, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    CAMERA_PERMISSION_REQUEST_CODE
                )
            }
            /*.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }*/
            .show()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Camera permission granted
                   // openCamera()
                    showCameraDialog()
                } else {
                    // Camera permission denied
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Permission denied but not permanently, show the rationale dialog
                        showPermissionRationaleDialog()
                    } else {
                        // Permission denied with "Don't ask again", guide the user to settings
                        showSettingsDialog()
                    }
                }
            }
        }
    }
    private fun showSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_required))
            .setMessage(getString(R.string.you_have_denied_the_camera_permission_please_go_to_the_app_settings_and_enable_it_manually))
            .setPositiveButton(getString(R.string.go_to_settings)) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }



    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                null
            }
            photoFile?.also {
                val photoURI = FileProvider.getUriForFile(
                    this,
                    "com.sagarclientapp.fileprovider",
                    it
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startForResultCamera.launch(intent)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startForResultGallery.launch(intent)
    }

    private lateinit var currentPhotoPath: String

    private val startForResultCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (file1 == null) {
                    file1 = File(currentPhotoPath)
                    val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                    binding.llImages.visibility = View.VISIBLE
                    binding.rl1.visibility = View.VISIBLE
                    binding.iv1.setImageBitmap(bitmap)
                } else if (file2 == null) {
                    file2 = File(currentPhotoPath)
                    val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                    binding.llImages.visibility = View.VISIBLE
                    binding.rl2.visibility = View.VISIBLE

                    binding.iv2.setImageBitmap(bitmap)
                } else if (file3 == null) {
                    file2 = File(currentPhotoPath)
                    val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                    binding.llImages.visibility = View.VISIBLE
                    binding.rlAttach.visibility = View.GONE
                    binding.rl3.visibility = View.VISIBLE

                    binding.iv3.setImageBitmap(bitmap)
                }

                //uploadFile(file)
            }
        }

    private fun uploadFile() {
        showDialog()
        /*val requestfile1 =
            file1?.let {
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), it)
            }
        val fileToUpload1 =
            requestfile1?.let {
                MultipartBody.Part.createFormData(
                    "contact_documents[0]", file1?.name,
                    it
                )
            }
        val requestfile2 =
            file2?.let {
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), it)
            }
        val fileToUpload2 =
            requestfile2?.let {
                MultipartBody.Part.createFormData(
                    "contact_documents[1]", file2?.name,
                    it
                )
            }
        val requestfile3 =
            file3?.let {
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), it)
            }
        val fileToUpload3 =
            requestfile3?.let {
                MultipartBody.Part.createFormData(
                    "contact_documents[2]", file3?.name,
                    it
                )
            }

        val message =
            RequestBody.create("text/plain".toMediaTypeOrNull(), binding.etMessage.text.toString())
        val user_id = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            SharedPref.readString(SharedPref.USER_ID)
        )
        if (file1 == null && file2 == null && file3 == null)
        {*/
        /*viewModels.contactusApi(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            user_id = user_id,
            message = message,
            file1 = fileToUpload1,
            file2 = fileToUpload2,
            file3 = fileToUpload3,

            )*/
        //}

        val parts = mutableListOf<MultipartBody.Part>()

        file1?.let {
            val compressedFile = compressImageFile(it, maxSizeKB = 500) // Compress to max 500 KB

            val requestFile1 = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), compressedFile)
            val fileToUpload1 =
                MultipartBody.Part.createFormData("contact_documents[0]", it.name, requestFile1)
            parts.add(fileToUpload1)
        }

        file2?.let {
            val compressedFile = compressImageFile(it, maxSizeKB = 500) // Compress to max 500 KB

            val requestFile2 = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), compressedFile)
            val fileToUpload2 =
                MultipartBody.Part.createFormData("contact_documents[1]", it.name, requestFile2)
            parts.add(fileToUpload2)
        }

        file3?.let {
            val compressedFile = compressImageFile(it, maxSizeKB = 500) // Compress to max 500 KB

            val requestFile3 = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), compressedFile)
            val fileToUpload3 =
                MultipartBody.Part.createFormData("contact_documents[2]", it.name, requestFile3)
            parts.add(fileToUpload3)
        }

        val message =
            RequestBody.create("text/plain".toMediaTypeOrNull(), binding.etMessage.text.toString())
        val userId = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            SharedPref.readString(SharedPref.USER_ID)
        )

        viewModels.contactusApi(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            user_id = userId,
            message = message,
            files = parts // Pass the list of parts here
        )


    }

    private val startForResultGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                selectedImageUri?.let {
                    /*val filePath = getRealPathFromURI(it)
                    filePath?.let { path ->
                        val file = File(path)
                        val bitmap = BitmapFactory.decodeFile(filePath)
                        binding.llImages.visibility = View.VISIBLE
                        binding.iv1.setImageBitmap(bitmap)
                       // uploadFile(file)
                    }*/
                    if (file1 == null) {
                        val filePath = getRealPathFromURI(it)
                        filePath?.let { path ->
                            file1 = File(path)
                            val bitmap = BitmapFactory.decodeFile(filePath)
                            binding.llImages.visibility = View.VISIBLE
                            binding.rl1.visibility = View.VISIBLE

                            binding.iv1.setImageBitmap(bitmap)
                            // uploadFile(file)
                        }
                    } else if (file2 == null) {
                        val filePath = getRealPathFromURI(it)
                        filePath?.let { path ->
                            file2 = File(path)
                            val bitmap = BitmapFactory.decodeFile(filePath)
                            binding.llImages.visibility = View.VISIBLE
                            binding.rl2.visibility = View.VISIBLE

                            binding.iv2.setImageBitmap(bitmap)
                            // uploadFile(file)
                        }
                    } else if (file3 == null) {
                        val filePath = getRealPathFromURI(it)
                        filePath?.let { path ->
                            file3 = File(path)
                            val bitmap = BitmapFactory.decodeFile(filePath)
                            binding.llImages.visibility = View.VISIBLE
                            binding.rl3.visibility = View.VISIBLE
                            binding.rlAttach.visibility = View.GONE

                            binding.iv3.setImageBitmap(bitmap)
                            // uploadFile(file)
                        }
                    } else {

                    }
                }
            }
        }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        val idx = cursor?.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        val realPath = idx?.let { cursor.getString(it) }
        cursor?.close()
        return realPath
    }

    fun apisObservers() {
        viewModels.contactUsResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    hideDialog()
                    when (val data = it.responseData) {
                        is UpdateNotificationStatusResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                //  finish()
                                showToast(data.message)
                                launchActivity(
                                    HomeActivity::class.java,
                                    removeAllPrevActivities = true
                                )
                            } else {
                                it.responseData.message?.let { it1 -> showToast(it1) }
                            }
                        }
                    }


                }

                is Response.Loading -> {
                    try {
                        showDialog()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                is Response.Error -> {
                    hideDialog()
                    if (it.errorBody?.status == 401) {
                        showAuthenticateDialog()
                    } else {
                        if (it.errorBody?.errorMessages != null && it.errorBody.errorMessages.size > 0) {
                            showToast(it.errorBody.errorMessages.get(0))
                        } else if (it.errorCode == null) {
                            showToast(it.msg)
                        }else if (it.errorBody?.error != null) {
                            // showToast(it.errorBody?.error)
                            showToast(getFirstErrorMessage(it.errorBody?.error))

                        }  else {
                            showToast(it.errorBody?.message.toString())
                        }
                    }

                }
            }
        })
    }

    fun compressImageFile(imageFile: File, maxSizeKB: Int): File {
        // Load the image from file
        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)

        // Compress the bitmap
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream) // Adjust quality as needed (80% in this case)

        // Check if the size is below the required max size in KB
        var compressQuality = 80 // Start with 80% quality
        var streamLength = outputStream.toByteArray().size / 1024
        while (streamLength > maxSizeKB && compressQuality > 10) {
            outputStream.reset()
            compressQuality -= 10
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, outputStream)
            streamLength = outputStream.toByteArray().size / 1024
        }

        // Write the compressed data back to a file
        val compressedFile = File(imageFile.parent, "compressed_" + imageFile.name)
        val fos = FileOutputStream(compressedFile)
        fos.write(outputStream.toByteArray())
        fos.flush()
        fos.close()

        return compressedFile
    }
}