package com.localattendance.teacherassistant.data.api

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val cookieJar = object : CookieJar {
        private val cookieStore = mutableMapOf<String, List<Cookie>>()

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            cookieStore[url.host] = cookies
            cookies.forEach { cookie ->
                if (cookie.name == "auth_token") {
                    prefs.edit().putString("auth_cookie", cookie.toString()).apply()
                }
            }
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return cookieStore[url.host] ?: emptyList()
        }
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    fun getBaseUrl(): String {
        return prefs.getString(PREF_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
    }

    fun setBaseUrl(url: String) {
        prefs.edit().putString(PREF_BASE_URL, url).apply()
        clearInstance()
    }

    fun createApiService(baseUrl: String? = null): ApiService {
        val url = baseUrl ?: getBaseUrl()
        return Retrofit.Builder()
            .baseUrl(url)
            .client(
                OkHttpClient.Builder()
                    .cookieJar(cookieJar)
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    companion object {
        const val DEFAULT_BASE_URL = "http://10.0.2.2:3000/api/"
        const val PREF_BASE_URL = "server_url"

        @Volatile
        private var INSTANCE: ApiService? = null

        fun getInstance(context: Context): ApiService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ApiClient(context).createApiService().also { INSTANCE = it }
            }
        }

        fun clearInstance() {
            INSTANCE = null
        }
    }
}
