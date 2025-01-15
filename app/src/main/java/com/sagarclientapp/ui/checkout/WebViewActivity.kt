package com.sagarclientapp.ui.checkout

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.sagarclientapp.R
import com.sagarclientapp.base.BaseActivity
import com.sagarclientapp.model.CreateBooking
import com.sagarclientapp.model.PaymentSuccessResponse
import com.sagarclientapp.utils.AppConstants
import org.json.JSONObject

class WebViewActivity : BaseActivity() {
    var bundle: Bundle? = null
    private lateinit var webView: WebView
    private lateinit var loading_indicator: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_web_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUis()
    }

    fun initUis()
    {
        bundle = intent.extras
        var url=""
        if (bundle != null) {

            // page = bundle?.getString("promo")
            url = bundle?.getString("url").toString()

        }

        webView = findViewById(R.id.webview)
        loading_indicator = findViewById(R.id.loading_indicator)
        setupWebView()

        if (url.isNotEmpty()) {
            webView.loadUrl(url)
        } else {
            webView.loadUrl("https://www.google.com") // Load Google if no URL provided
        }
    }

    private fun setupWebView() {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        WebView.setWebContentsDebuggingEnabled(true)
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url ?: "") // Load the URL in the WebView
                return true // Indicate that we are handling the URL
                Log.d("WebView", "shouldOverrideUrlLoading: $url")
                // Check if the loaded URL is the success URL


            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
               // webView.visibility= View.GONE
                Log.d("WebView", "Finished loading URL: $url")
                if (url == "https://sagor.app/beta/public/api/payment-success") {
                    webView.visibility = View.GONE
                    loading_indicator.visibility = View.VISIBLE
                    // Call JavaScript to extract the JSON from the body
                    view?.evaluateJavascript("(function() { return document.body.innerText; })();") { jsonText ->
                        Log.d("WebView", "JSON Content: $jsonText")
                        extractDataFromJson(jsonText)

                        loading_indicator.visibility = View.GONE
                    }
                }
            }

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                Log.e("WebView", "Error: $errorCode, $description") // Log any loading errors
            }
        }
    }
   /* private fun extractDataFromJson(jsonString: String?) {
        if (!jsonString.isNullOrEmpty()) {
            try {
                // Parse the JSON string
                val jsonObject = JSONObject(jsonString)
                val paymentResponse = Gson().fromJson(jsonObject.toString(), PaymentSuccessResponse::class.java)

                Log.d("WebView", "Payment Status: ${paymentResponse.status}")
                Log.d("WebView", "Payment Message: ${paymentResponse.message}")
                Log.d("WebView", "Transaction Number: ${paymentResponse.data?.transactionNumber}")

                // Handle the extracted data as needed
                // e.g., sendDataToNextActivity(paymentResponse)

            } catch (e: Exception) {
                Log.e("WebView", "JSON parsing error: ${e.message}")
            }
        }
    }*/

   /* private fun extractDataFromJson(jsonString: String?) {
        if (!jsonString.isNullOrEmpty()) {
            try {
                // Clean the JSON string
                val cleanedJsonString = cleanJsonString(jsonString)
                Log.d("WebView", "Cleaned JSON Content: $cleanedJsonString")

                // Parse the cleaned JSON string
                val jsonObject = JSONObject(cleanedJsonString)
                val paymentResponse = Gson().fromJson(jsonObject.toString(), PaymentSuccessResponse::class.java)

                Log.d("WebView", "Payment Status: ${paymentResponse.status}")
                Log.d("WebView", "Payment Message: ${paymentResponse.message}")
                Log.d("WebView", "Transaction Number: ${paymentResponse.data?.transactionNumber}")

                // Handle the extracted data as needed
                // e.g., sendDataToNextActivity(paymentResponse)

            } catch (e: Exception) {
                Log.e("WebView", "JSON parsing error: ${e.message}")
            }
        }
    }*/


    private fun cleanJsonString(jsonString: String?): String {
        // Replace escaped backslashes with actual backslashes
        return jsonString?.replace("\\", "") ?: ""
    }
    private fun removeSurroundingQuotes(jsonString: String?): String {
        return jsonString?.trim('"') ?: ""
    }
    private fun extractDataFromJson(jsonString: String?) {
        if (!jsonString.isNullOrEmpty()) {
            try {
                // Clean the JSON string by removing surrounding quotes
                //val cleanedJsonString = removeSurroundingQuotes(jsonString)

                // Step 1: Clean the JSON String
                val cleanedJsonString = jsonString?.replace("\\", "") ?: ""

                // Step 2: Remove surrounding quotes if present
                val jsonStringWithoutQuotes = cleanedJsonString.trim('"')

                // Parse the cleaned JSON string
                val jsonObject = JSONObject(jsonStringWithoutQuotes)
                val paymentResponse = Gson().fromJson(jsonObject.toString(), PaymentSuccessResponse::class.java)

               /* Log.d("WebView", "Payment Status: ${paymentResponse.status}")
                Log.d("WebView", "Payment Message: ${paymentResponse.message}")
                Log.d("WebView", "Transaction Number: ${paymentResponse.data?.transactionNumber}")
*/
                // Handle the extracted data as needed

                passDataToNextScreen(paymentResponse)

            } catch (e: Exception) {
                Log.e("WebView", "JSON parsing error: ${e.message}")
            }
        }
    }

    private fun passDataToNextScreen(paymentResponse: PaymentSuccessResponse) {
        Log.e("WebView", "paymentResponse "+paymentResponse)

        // Serialize the model to a JSON string
        val gson = Gson()
        val jsonString = gson.toJson(paymentResponse)

        // Create an intent and pass the JSON string
        val resultIntent = Intent()
        Log.e("WebView", "jsonString "+jsonString)

        resultIntent.putExtra("paymentResponse", jsonString)

        // Set the result and finish the current activity
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }





}