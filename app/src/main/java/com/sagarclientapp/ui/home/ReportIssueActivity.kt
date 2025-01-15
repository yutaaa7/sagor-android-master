package com.sagarclientapp.ui.home

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sagarclientapp.R
import com.sagarclientapp.api.Response
import com.sagarclientapp.base.BaseActivity
import com.sagarclientapp.databinding.ActivityReportIssueBinding
import com.sagarclientapp.model.CountryList
import com.sagarclientapp.model.CountryListResponse
import com.sagarclientapp.model.LoginResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.ui.home.viewModels.ReportAnIssueViewModel
import com.sagarclientapp.ui.login.OtpScreenActivity
import com.sagarclientapp.ui.login.adapters.CountryListAdapter
import com.sagarclientapp.ui.login.viewmodels.LoginViewModel
import com.sagarclientapp.ui.profile.ContactUsActivity
import com.sagarclientapp.ui.workaround.WorkAroundActivity
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
class ReportIssueActivity : BaseActivity() {
    lateinit var binding: ActivityReportIssueBinding
    lateinit var countryListAdapter: CountryListAdapter
    var countryList = mutableListOf<CountryList>()
    var booking_id: String? = null
    var bundle: Bundle? = null
    var file1: File? = null
    var paymentIssueValue: String = "0"
    var tripIssueValue: String = "0"
    var lostItemValue: String = "0"
    var otherValue: String = "0"
    private val viewModels: ReportAnIssueViewModel by viewModels<ReportAnIssueViewModel>()

    companion object {
        const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityReportIssueBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUis()
    }

    private fun initUis() = binding.apply {
        checkCameraPermissions()

        bundle = intent.extras
       /* tvMobile.visibility = View.GONE
        etMobileNumber.visibility = View.VISIBLE
        etMobileNumber.requestFocus()
        mobileLable.visibility = View.VISIBLE
        etMobileNumber.setText(SharedPref.readString(SharedPref.MOBILE))*/

        if (bundle != null) {
            booking_id = bundle?.getString("booking_id")
        }

        headerReport.headerTitle.text = getString(R.string.report_an_issue)
        headerReport.ivBack.setOnClickListener {
            finish()
        }
        btRepostIssueSubmit.setOnClickListener {
            validations()
        }
        ivCountryShow.setOnClickListener {
            showCountryDialog()
        }
        tvCountryCode.setOnClickListener {
            showCountryDialog()
        }
        ivCountryFlag.setOnClickListener {
            showCountryDialog()
        }
        llCountry.setOnClickListener {
            showCountryDialog()
        }
        llAttach.setOnClickListener {
            showCameraDialog()
        }
        paymentIssue.setOnClickListener {
            if (paymentIssue.isChecked) {
                paymentIssueValue = "1"
            } else {
                paymentIssueValue = "0"
            }
        }
        tripIssue.setOnClickListener {
            if (tripIssue.isChecked) {
                tripIssueValue = "1"
            } else {
                tripIssueValue = "0"
            }
        }

        lostItem.setOnClickListener {
            if (lostItem.isChecked) {
                lostItemValue = "1"
            } else {
                lostItemValue = "0"
            }
        }
        other.setOnClickListener {
            if (other.isChecked) {
                otherValue = "1"
            } else {
                otherValue = "0"
            }
        }
        tvMobile.setOnClickListener {
            tvMobile.visibility = View.GONE
            etMobileNumber.visibility = View.VISIBLE
            etMobileNumber.requestFocus()
            mobileLable.visibility = View.VISIBLE
        }

        ivclose1.setOnClickListener {
            rl1.visibility = View.GONE

            file1 = null
            rlAttach.visibility = View.VISIBLE
            llAttach.visibility = View.VISIBLE
            llImages.visibility = View.GONE

        }

        countryListApi()
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

    private fun showCountryDialog() {
        val dialog = Dialog(this, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_country_list)

        val recyclerView = dialog.findViewById(R.id.rvCountriesList) as RecyclerView

        recyclerView.layoutManager = LinearLayoutManager(this)
        countryListAdapter = CountryListAdapter(
            this@ReportIssueActivity,
            countryList,
            object : CountryListAdapter.BtnClickListener {
                override fun onClick(countryList: CountryList) {
                    binding.tvCountryCode.text = countryList.phoneCode
                    Glide.with(this@ReportIssueActivity)
                        .load(countryList.flag).placeholder(R.drawable.flag)
                        .into(binding.ivCountryFlag);
                    dialog.dismiss()
                }

            })
        recyclerView.adapter = countryListAdapter


        val tvCancelCountryDialog = dialog.findViewById(R.id.tvCancelCountryDialog) as TextView
        tvCancelCountryDialog.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun countryListApi() {
        showDialog()
        viewModels.countryListApi()

    }

    fun validations() {
        val phoneNumber = binding.etMobileNumber.text.toString().trim()
        if (phoneNumber.isNullOrEmpty()) {
            Toast.makeText(
                this,
                getString(R.string.please_enter_the_mobile_number), Toast.LENGTH_SHORT
            ).show()
        } else if (phoneNumber.length < 8) {
            showToast(getString(R.string.phone_number_must_be_at_least_8_digits))
            // If the phone number is less than 8 digits

        } else if (phoneNumber.length > 11) {
            showToast(getString(R.string.phone_number_must_be_no_more_than_11_digits))
            // If the phone number exceeds 11 digits

        } else if (binding.etDescription.text.toString().isNullOrEmpty()) {

            showToast(getString(R.string.please_enter_description))
        } else {
            reportAnIssueAPi()
        }
    }

    fun reportAnIssueAPi() {
        showDialog()

        val parts = mutableListOf<MultipartBody.Part>()

        file1?.let {
            val compressedFile = compressImageFile(it, maxSizeKB = 500) // Compress to max 500 KB
            val requestFile1 = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), compressedFile)
            val fileToUpload1 =
                MultipartBody.Part.createFormData("image", it.name, requestFile1)
            parts.add(fileToUpload1)
        }

        val user_id =
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                SharedPref.readString(SharedPref.USER_ID)
            )

        val booking_id =
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                booking_id.toString()
            )
        val country_code =
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                binding.tvCountryCode.text.toString()
            )
        val mobile_no =
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                binding.etMobileNumber.text.toString()
            )
        val paymentIssueValue =
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                paymentIssueValue
            )
        val tripIssueValue =
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                tripIssueValue
            )
        val lostItemValue =
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                lostItemValue
            )
        val otherValue =
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                otherValue
            )
        val description =
            RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                binding.etDescription.text.toString()
            )
        viewModels.reportAnIssue(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            user_id,
            booking_id,
            country_code,
            mobile_no,
            paymentIssueValue,
            tripIssueValue,
            lostItemValue,
            otherValue,
            description,
            parts
        )

    }

    fun apisObservers() {
        viewModels.reportAnIssueResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is UpdateNotificationStatusResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
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

                    hideDialog()

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
                    if (it.errorBody?.status == 422) {
                        showToast(getString(R.string.the_mobile_has_already_been_taken))
                    } else if (it.errorBody?.status == 401) {
                        showAuthenticateDialog()
                    } else {
                        if (it.errorBody?.errorMessages != null && it.errorBody.errorMessages.size > 0) {
                            showToast(it.errorBody.errorMessages.get(0))
                        } else if (it.errorCode == null) {
                            showToast(it.msg)
                        } else if (it.errorBody?.error != null) {
                            // showToast(it.errorBody?.error)
                            showToast(getFirstErrorMessage(it.errorBody?.error))

                        } else {
                            showToast(it.errorBody?.message.toString())
                        }
                    }

                }
            }
        })
        viewModels.countryListResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is CountryListResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                countryList.clear()
                                data.data?.let { it1 -> countryList.addAll(it1) }
                                if (countryList.size == 1) {
                                    binding.tvCountryCode.text = countryList[0].phoneCode
                                    Glide.with(this)
                                        .load(countryList[0].flag).placeholder(R.drawable.flag)
                                        .into(binding.ivCountryFlag);
                                } else {

                                }

                            } else {
                                it.responseData.message?.let { it1 -> showToast(it1) }
                            }
                        }
                    }

                    hideDialog()

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
                        } else if (it.errorBody?.error != null) {
                            // showToast(it.errorBody?.error)
                            showToast(getFirstErrorMessage(it.errorBody?.error))

                        } else {
                            showToast(it.errorBody?.message.toString())
                        }
                    }


                }
            }
        })
    }

    fun checkCameraPermissions() {
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
                ContactUsActivity.CAMERA_PERMISSION_REQUEST_CODE
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
            ContactUsActivity.CAMERA_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted
                    // openCamera()
                } else {
                    // Permission denied
                    showToast(getString(R.string.camera_permission_is_required_to_use_this_feature))

                }
                return
            }
        }
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

                file1 = File(currentPhotoPath)
                val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                binding.rlAttach.visibility = View.GONE

                binding.llImages.visibility = View.VISIBLE
                binding.rl1.visibility = View.VISIBLE
                binding.iv1.setImageBitmap(bitmap)

                //uploadFile(file)
            }
        }

    private val startForResultGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                selectedImageUri?.let {
                    val filePath = getRealPathFromURI(it)
                    filePath?.let { path ->
                        file1 = File(path)
                        val bitmap = BitmapFactory.decodeFile(filePath)
                        binding.llImages.visibility = View.VISIBLE
                        binding.rlAttach.visibility = View.GONE
                        binding.rl1.visibility = View.VISIBLE

                        binding.iv1.setImageBitmap(bitmap)
                        // uploadFile(file)
                    }

                }
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