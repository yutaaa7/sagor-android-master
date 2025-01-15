package com.sagarclientapp.api

import android.util.Log
import com.sagarclientapp.utils.SharedPref
import okhttp3.Interceptor

class OAuthInterceptor : Interceptor {

    // private val tokenType: String = "Basic"
    private val tokenType: String = "Bearer Token"
    private var accessToken: String? = null

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request().newBuilder()
       /* request.apply {
            addHeader("Content-Type", "application/json")
            //addHeader("appsource", "android")
            //addHeader("appversion", BuildConfig.VERSION_NAME)
        }*/
        accessToken=SharedPref.readString(SharedPref.ACCESS_TOKEN)
        Log.d("CHECK TOKEN ",accessToken!!)
        //accessToken = "dXNlcjphN2I0Mjg5Ni05MmYxLTQ3NjEtYTUwYy05MDcxNjZjNjdkNTU="
       /* if (accessToken != null && accessToken?.isNotEmpty() == true) {
            Log.d("CHECK TOKEN ","inside  "+accessToken!!)

            request.header("Authorization", "$tokenType $accessToken")
        }*/
        accessToken?.let {
            if (it.isNotEmpty()) {
                Log.d("CHECK TOKEN", "inside $it")
                request.addHeader("Authorization", "$tokenType $it")
            }
        }
        return chain.proceed(request.build())
    }
}