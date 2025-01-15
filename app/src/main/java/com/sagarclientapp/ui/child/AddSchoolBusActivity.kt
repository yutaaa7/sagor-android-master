package com.sagarclientapp.ui.child

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sagarclientapp.R
import com.sagarclientapp.api.Response
import com.sagarclientapp.base.BaseActivity
import com.sagarclientapp.databinding.ActivityAddSchoolBusBinding
import com.sagarclientapp.model.BusListing
import com.sagarclientapp.model.BusListingResponse
import com.sagarclientapp.model.CountryList
import com.sagarclientapp.model.CountryListResponse
import com.sagarclientapp.model.GradesList
import com.sagarclientapp.model.GradesListResponse
import com.sagarclientapp.model.SchoolList
import com.sagarclientapp.model.SchoolListingResponse
import com.sagarclientapp.model.SchoolStudentListResponse
import com.sagarclientapp.model.UpdateNotificationStatusResponse
import com.sagarclientapp.ui.child.adapters.BusAdapter
import com.sagarclientapp.ui.child.adapters.GradesAdapter
import com.sagarclientapp.ui.child.adapters.SchoolBusesListAdapter
import com.sagarclientapp.ui.child.viewModels.SchoolViewModel
import com.sagarclientapp.ui.home.HomeActivity
import com.sagarclientapp.ui.login.adapters.CountryListAdapter
import com.sagarclientapp.utils.AppConstants
import com.sagarclientapp.utils.SharedPref
import com.sagarclientapp.utils.Util.launchActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddSchoolBusActivity : BaseActivity() {

    lateinit var binding: ActivityAddSchoolBusBinding
    var buttonClick = 0
    var school_id: Int? = null
    var grade_id: Int? = null
    var bus_id: Int? = null
    var child_id: Int? = null
    private val viewModels: SchoolViewModel by viewModels<SchoolViewModel>()
    lateinit var schoolBusAdapter: SchoolBusesListAdapter
    lateinit var gradesAdapter: GradesAdapter
    lateinit var busAdapter: BusAdapter
    var schoolList = mutableListOf<SchoolList>()
    var gradesList = mutableListOf<GradesList>()
    var busList = mutableListOf<BusListing>()
    var countryList = mutableListOf<CountryList>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddSchoolBusBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUis()
    }

    private fun initUis() = binding.apply {

        headerAddChildBus.headerTitle.text = getString(R.string.add_your_school_bus)

        llSchoolBuses.visibility = View.VISIBLE
        llGrades.visibility = View.GONE
        llBuses.visibility = View.GONE
        llChild.visibility = View.GONE
        llChildData.visibility = View.GONE

        tvChildName.setOnClickListener {
            tvChildName.visibility = View.GONE
            childLable.visibility = View.VISIBLE
            etChildName.visibility = View.VISIBLE
            etChildName.requestFocus()
        }

        tvMobile.setOnClickListener {
            tvMobile.visibility = View.GONE
            etMobileNumber.visibility = View.VISIBLE
            etMobileNumber.requestFocus()
            mobileLable.visibility = View.VISIBLE
        }
        ivCountryShow.setOnClickListener {
            showCountryDialog()
        }

        /* rvSchoolBusAlphbet.layoutManager = LinearLayoutManager(this@AddSchoolBusActivity)
         rvSchoolBusAlphbet.adapter = SchoolBusAlphabets(this@AddSchoolBusActivity,
             object : SchoolBusAlphabets.onItemClick {
                 override fun onClick() {
                     buttonClick = 1
                     btContinueAddBus.setBackgroundResource(R.drawable.solid_theme_blue)
                 }
             })*/
        rvSchoolBusAlphbet.layoutManager = LinearLayoutManager(this@AddSchoolBusActivity)
        schoolBusAdapter = SchoolBusesListAdapter(this@AddSchoolBusActivity,
            schoolList,
            object : SchoolBusesListAdapter.onItemClick {
                override fun onClick(id: Int?) {
                    school_id = id
                    Log.d("IDS ", "school_id " + school_id)
                    buttonClick = 1
                    btContinueAddBus.setBackgroundResource(R.drawable.solid_theme_blue)
                    getGradesListingApi()
                }

            })

        rvSchoolBusAlphbet.adapter = schoolBusAdapter

        rvGrades.layoutManager = GridLayoutManager(this@AddSchoolBusActivity, 3)
        gradesAdapter = GradesAdapter(this@AddSchoolBusActivity, gradesList,
            object : GradesAdapter.onItemClick {
                override fun onClick(id: Int?) {
                    grade_id = id
                    Log.d("IDS ", "grade_id " + grade_id)

                    buttonClick = 2
                    btContinueAddBus.setBackgroundResource(R.drawable.solid_theme_blue)
                    getSchoolBusesListingApi()
                }

            })
        rvGrades.adapter = gradesAdapter
        rvBuses.layoutManager = LinearLayoutManager(this@AddSchoolBusActivity)

        busAdapter =
            BusAdapter(this@AddSchoolBusActivity, busList, object : BusAdapter.onItemClick {
                override fun onClick(id: Int?) {
                    bus_id = id
                    Log.d("IDS ", "bus_id " + bus_id)

                    buttonClick = 3
                    btContinueAddBus.setBackgroundResource(R.drawable.solid_theme_blue)

                }

            })
        rvBuses.adapter = busAdapter

        btContinueAddBus.setOnClickListener {
            if (buttonClick == 1) {
                //getGradesListingApi(school_id)
                binding.headerAddChildBus.headerTitle.text =
                    getString(R.string.select_grade)

                binding.rlGrades.setImageResource(R.drawable.grade_blue)
               // binding.ivGrades.setImageResource(R.drawable.user_class_blue)
                binding.tvGrades.setTextColor(resources.getColor(R.color.theme_blue))
                btContinueAddBus.setBackgroundResource(R.drawable.solid_light_gray)
                llSchoolBuses.visibility = View.GONE
                llGrades.visibility = View.VISIBLE
                llBuses.visibility = View.GONE
                llChild.visibility = View.GONE
                llChildData.visibility = View.GONE

            } else if (buttonClick == 2) {
                binding.headerAddChildBus.headerTitle.text = getString(R.string.add_your_school_bus)

               // binding.rlBus.setBackgroundResource(R.drawable.elipse_blue)
                binding.ivBus.setImageResource(R.drawable.bus_blue)
                binding.ivDot2.setImageResource(R.drawable.dotted_line_blue)
                binding.tvBus.setTextColor(resources.getColor(R.color.theme_blue))
                btContinueAddBus.setBackgroundResource(R.drawable.solid_light_gray)
                llSchoolBuses.visibility = View.GONE
                llBuses.visibility = View.VISIBLE
                llGrades.visibility = View.GONE
                llChild.visibility = View.GONE
                llChildData.visibility = View.GONE
            } else if (buttonClick == 3) {
               // binding.rlChild.setBackgroundResource(R.drawable.elipse_blue)
                binding.ivChild.setImageResource(R.drawable.child_blue)
                binding.tvChild.setTextColor(resources.getColor(R.color.theme_blue))
                binding.ivDot3.setImageResource(R.drawable.dotted_line_blue)
                btContinueAddBus.setBackgroundResource(R.drawable.solid_theme_blue)
                btSearchChild.visibility = View.VISIBLE
                btContinueAddBus.visibility = View.GONE
                llSchoolBuses.visibility = View.GONE
                llGrades.visibility = View.GONE
                llBuses.visibility = View.GONE
                llChild.visibility = View.VISIBLE
                llChildData.visibility = View.GONE

            } else {

            }

        }

        btSearchChild.setOnClickListener {


            getSchoolStudentListingApi()
        }

        btAddToList.setOnClickListener {
            /* SharedPref.writeString(
                 SharedPref.is_CHILD_ADDED,
                 "true"
             )*/
            addChildApi()

        }
        headerAddChildBus.ivBack.setOnClickListener {
            if (buttonClick == 3) {
                getSchoolBusesListingApi()
                binding.headerAddChildBus.headerTitle.text = getString(R.string.add_your_school_bus)


              //  binding.rlChild.setBackgroundResource(R.drawable.elipse_gray)
                binding.ivChild.setImageResource(R.drawable.child_gray)
                binding.tvChild.setTextColor(resources.getColor(R.color.gray))
                binding.ivDot3.setImageResource(R.drawable.dotted_line)
                btContinueAddBus.setBackgroundResource(R.drawable.solid_light_gray)
                btSearchChild.visibility = View.GONE
                btContinueAddBus.visibility = View.VISIBLE
                llSchoolBuses.visibility = View.GONE
                llGrades.visibility = View.GONE
                llBuses.visibility = View.VISIBLE
                llChild.visibility = View.GONE
                llChildData.visibility = View.GONE
                buttonClick = 2


            } else if (buttonClick == 2) {
                getGradesListingApi()
                binding.headerAddChildBus.headerTitle.text = getString(R.string.select_grade)

               // binding.rlBus.setBackgroundResource(R.drawable.elipse_gray)
                binding.ivBus.setImageResource(R.drawable.bus_gray)
                binding.ivDot2.setImageResource(R.drawable.dotted_line)
                binding.tvBus.setTextColor(resources.getColor(R.color.gray))
                btContinueAddBus.setBackgroundResource(R.drawable.solid_light_gray)
                llSchoolBuses.visibility = View.GONE
                llBuses.visibility = View.GONE
                llGrades.visibility = View.VISIBLE
                llChild.visibility = View.GONE
                llChildData.visibility = View.GONE
                buttonClick = 1

            } else if (buttonClick == 1) {
                getSchoolListingApi("", false)
                //getGradesListingApi(school_id)
                binding.headerAddChildBus.headerTitle.text =
                    getString(R.string.add_your_school_bus)

                binding.rlGrades.setImageResource(R.drawable.grades_gray)
               // binding.ivGrades.setImageResource(R.drawable.users_class)
                binding.tvGrades.setTextColor(resources.getColor(R.color.gray))
                btContinueAddBus.setBackgroundResource(R.drawable.solid_light_gray)
                llSchoolBuses.visibility = View.VISIBLE
                llGrades.visibility = View.GONE
                llBuses.visibility = View.GONE
                llChild.visibility = View.GONE
                llChildData.visibility = View.GONE
                buttonClick = 0

            } else {

                finish()
            }

        }

        etSearchSchool.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var value = p0.toString()
                if (value.length > 2) {
                    getSchoolListingApi(value, false)
                }
                if (value.length == 0) {
                    getSchoolListingApi("", false)
                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        getSchoolListingApi("", true)
        countryListApi()
        apisObservers()
    }

    fun getSchoolListingApi(value: String, showLoader: Boolean) {
        if (showLoader) {
            showDialog()
        }
        viewModels.getSchoolListing(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            value
        )

    }

    fun getSchoolStudentListingApi() {
        if (binding.etChildName.text.toString().equals("")) {
            showToast(getString(R.string.please_enter_your_child_name))
        } /*else if (binding.etMobileNumber.text.toString().equals("")) {
            showToast(getString(R.string.please_enter_the_mobile_number))
        } */ else {
            showDialog()
            viewModels.schoolStudentListApi(
                "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
                binding.etChildName.text.toString(),
                school_id = school_id.toString(),
                grade_id = grade_id.toString(),
                bus_id = bus_id.toString(),
                country_code = binding.tvCountryCode.text.toString(),
                binding.etMobileNumber.text.toString()
            )
        }

    }

    fun addChildApi() {
        showDialog()
        viewModels.addChildApi(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            child_id.toString(), SharedPref.readString(SharedPref.USER_ID)
        )


    }

    fun getGradesListingApi() {

        viewModels.getGradesListing(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            school_id.toString()
        )

    }

    fun getSchoolBusesListingApi() {

        viewModels.getSchoolBusesListing(
            "Bearer " + SharedPref.readString(SharedPref.ACCESS_TOKEN),
            school_id.toString()
        )

    }

    fun countryListApi() {
        showDialog()
        viewModels.countryListApi()

    }

    fun apisObservers() {
        viewModels.schoolListResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is SchoolListingResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
                                binding.headerAddChildBus.headerTitle.text =
                                    getString(R.string.add_your_school_bus)

                                schoolList.clear()
                                data.data?.let { /*it1 -> schoolList.addAll(it1)*/
                                        scool ->
                                    // Use adapter's updateData method
                                    schoolBusAdapter.updateData(scool.toMutableList())
                                }
                               // schoolBusAdapter.notifyDataSetChanged()
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
                    if (it.errorCode == 401) {
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
        viewModels.addChildResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is UpdateNotificationStatusResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {
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
                    if (it.errorCode == 401) {
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
        viewModels.gradesListResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is GradesListResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {

                                gradesList.clear()
                                data.data?.let { /*it1 -> gradesList.addAll(it1)*/
                                        grades ->
                                    // Use adapter's updateData method
                                    gradesAdapter.updateData(grades.toMutableList())
                                }
                               // gradesAdapter.notifyDataSetChanged()
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
                    if (it.errorCode == 401) {
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

        viewModels.schoolBusesListResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is BusListingResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {

                                busList.clear()
                                data.data?.let { /*it1 ->
                                    busList.addAll(it1)*/
                                        buses ->
                                    // Use adapter's updateData method
                                    busAdapter.updateData(buses.toMutableList())
                                }
                                //busAdapter.notifyDataSetChanged()
                               // busAdapter.updateData(busList)

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
                    if (it.errorCode == 401) {
                        showAuthenticateDialog()
                    } else {
                        if (it.errorBody?.errorMessages != null && it.errorBody.errorMessages.size > 0) {
                            showToast(it.errorBody.errorMessages.get(0))
                        } else if (it.errorCode == null) {
                            showToast(it.msg)
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
                    if (it.errorCode == 401) {
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
        viewModels.schoolStudentListResponse.observe(this, Observer {
            when (it) {
                is Response.Success -> {
                    when (val data = it.responseData) {
                        is SchoolStudentListResponse -> {
                            if (data.status == AppConstants.API_SUCCESS) {


                                if (data.data?.size!! > 0) {
                                    binding.btSearchChild.visibility = View.GONE
                                    binding.btContinueAddBus.visibility = View.GONE
                                    binding.btAddToList.visibility = View.VISIBLE
                                    binding.llSchoolBuses.visibility = View.GONE
                                    binding.llGrades.visibility = View.GONE
                                    binding.llBuses.visibility = View.GONE
                                    binding.llChild.visibility = View.GONE
                                    binding.llChildData.visibility = View.VISIBLE

                                    child_id = data.data?.get(0)?.id
                                    Glide.with(this@AddSchoolBusActivity)
                                        .load(AppConstants.BASE_URL_IMAGE + "" + data.data?.get(0)?.avatar)
                                        .into(binding.ivChildImage1)
                                    binding.tvChildNameSearched.text = data.data?.get(0)?.name
                                    if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("0")) {

                                        binding.tvSchoolName.text = data.data?.get(0)?.school?.name
                                        binding.tvGrade.text = data.data?.get(0)?.grades?.name
                                    }
                                    if (SharedPref.readString(SharedPref.LANGUAGE_ID).equals("1")) {
                                        binding.tvSchoolName.text =
                                            data.data?.get(0)?.school?.nameAr
                                        binding.tvGrade.text = data.data?.get(0)?.grades?.nameAr
                                    }

                                    binding.tvBus1.text = getString(R.string.bus) + " " +
                                            data.data?.get(0)?.studentDetail?.vehicle?.vehicleNumber
                                } else {
                                    showToast(getString(R.string.the_data_you_are_searching_for_is_not_available_please_check_with_other_details))
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
                    if (it.errorCode == 401) {
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

    private fun showCountryDialog() {
        val dialog = Dialog(this, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_country_list)

        val recyclerView = dialog.findViewById(R.id.rvCountriesList) as RecyclerView

        recyclerView.layoutManager = LinearLayoutManager(this)
        var countryListAdapter = CountryListAdapter(
            this@AddSchoolBusActivity,
            countryList,
            object : CountryListAdapter.BtnClickListener {
                override fun onClick(countryList: CountryList) {
                    binding.tvCountryCode.text = countryList.phoneCode
                    Glide.with(this@AddSchoolBusActivity)
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





}