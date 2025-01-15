package com.sagarclientapp.ui.workaround

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.sagarclientapp.R
import com.sagarclientapp.databinding.ActivityWorkAroundBinding
import com.sagarclientapp.ui.LanguageSelectActivity
import com.sagarclientapp.ui.login.LoginActivity
import com.sagarclientapp.utils.Util.launchActivity

class WorkAroundActivity : AppCompatActivity() {
    lateinit var binding: ActivityWorkAroundBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWorkAroundBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUis()
    }

    private fun initUis() = binding.apply {

        viewPager.adapter = PageAdapter(supportFragmentManager)

        next.setOnClickListener {
           if (binding.viewPager.getCurrentItem() == 2 )
            {
                val bundle = Bundle()
                bundle.putString("where", "boarding")
                launchActivity(LoginActivity::class.java, removeAllPrevActivities = true,
                    bundle = bundle)

            }
            else{
                viewPager.setCurrentItem(getItem(+1), true);
               if (binding.viewPager.getCurrentItem() == 1)
               {
                   ivdot2.setImageDrawable(resources.getDrawable(R.drawable.tab_indicator_selected))
               }
               if (binding.viewPager.getCurrentItem() == 2)
               {
                   ivdot3.setImageDrawable(resources.getDrawable(R.drawable.tab_indicator_selected))

               }

            }
            Log.d("TAG",binding.viewPager.getCurrentItem().toString())

        }
    }

    private fun getItem(i: Int): Int {
        return binding.viewPager.getCurrentItem() + i
    }
}