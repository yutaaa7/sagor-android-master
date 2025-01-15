package com.sagarclientapp.ui

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.sagarclientapp.R
import com.sagarclientapp.base.BaseActivity
import com.sagarclientapp.databinding.ActivityLanguageSelectBinding
import com.sagarclientapp.ui.workaround.WorkAroundActivity
import com.sagarclientapp.utils.AppConstants
import com.sagarclientapp.utils.SharedPref
import com.sagarclientapp.utils.Util
import com.sagarclientapp.utils.Util.launchActivity

class LanguageSelectActivity : BaseActivity() {

    private lateinit var binding: ActivityLanguageSelectBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLanguageSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUis()
    }

    private fun initUis() = binding.apply {
        //0 - english, 1 - arabic
        btEnglish.setOnClickListener {
           // showToast("English")
            //throw RuntimeException("Test Crash") // Force a crash
            Util.setLocale(this@LanguageSelectActivity, "en")

            SharedPref.writeString(SharedPref.LANGUAGE_ID, "0")
            launchActivity(WorkAroundActivity::class.java, removeAllPrevActivities = true)
        }
        btArebic.setOnClickListener {
           // showToast("ARABIC")

            SharedPref.writeString(SharedPref.LANGUAGE_ID, "1")
            Util.setLocale(this@LanguageSelectActivity, "ar")

            launchActivity(WorkAroundActivity::class.java, removeAllPrevActivities = true)

        }

    }
}