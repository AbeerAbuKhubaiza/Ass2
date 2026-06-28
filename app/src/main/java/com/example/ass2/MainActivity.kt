package com.example.ass2

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var etSearchQuery: EditText
    private lateinit var btnSearch: AppCompatButton
    private lateinit var progressBar: ProgressBar
    private lateinit var tvErrorStatus: TextView
    private lateinit var rvVideos: RecyclerView
    private lateinit var videoAdapter: VideoAdapter

    private val apiKey = "AIzaSyAEk7F_bbhTFUWxwJXDn5fzxviwCJYk7EY"
    private val baseUrl = "https://www.googleapis.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etSearchQuery = findViewById(R.id.etSearchQuery)
        btnSearch = findViewById(R.id.btnSearch)
        progressBar = findViewById(R.id.progressBar)
        tvErrorStatus = findViewById(R.id.tvErrorStatus)
        rvVideos = findViewById(R.id.rvVideos)

        rvVideos.layoutManager = LinearLayoutManager(this)
        videoAdapter = VideoAdapter(emptyList())
        rvVideos.adapter = videoAdapter

        btnSearch.setOnClickListener {
            performSearch()
        }

        etSearchQuery.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                performSearch()
                true
            } else {
                false
            }
        }
    }

    private fun performSearch() {
        val query = etSearchQuery.text.toString().trim()
        if (query.isEmpty()) {
            showStatus("الرجاء كتابة كلمة للبحث ⚠️", isError = true)
        } else if (!isNetworkAvailable()) {
            showStatus("لا يوجد اتصال بالإنترنت! يرجى التحقق من الشبكة 🌐", isError = true)
        } else {
            fetchYoutubeVideos(query)
        }
    }


    private fun isNetworkAvailable(): Boolean {
        return try {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } catch (e: Exception) {
            true
        }
    }

    private fun showStatus(message: String, isError: Boolean = false) {
        tvErrorStatus.text = message
        if (isError) {
            tvErrorStatus.setTextColor(resources.getColor(R.color.pink_primary))
        } else {
            tvErrorStatus.setTextColor(resources.getColor(R.color.gray_text))
        }
        tvErrorStatus.visibility = View.VISIBLE
        rvVideos.visibility = View.GONE
    }

    private fun fetchYoutubeVideos(query: String) {
        progressBar.visibility = View.VISIBLE
        tvErrorStatus.visibility = View.GONE
        rvVideos.visibility = View.GONE

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(YoutubeApiService::class.java)

        apiService.searchVideos(query = query, maxResults = 15, apiKey = apiKey)
            .enqueue(object : Callback<YoutubeResponse> {
                override fun onResponse(
                    call: Call<YoutubeResponse>,
                    response: Response<YoutubeResponse>
                ) {
                    progressBar.visibility = View.GONE

                    if (response.isSuccessful && response.body() != null) {
                        val videoList = response.body()!!.items
                        if (videoList.isEmpty()) {
                            showStatus("لم يتم العثور على أي نتائج للبحث 🔍", isError = true)
                        } else {
                            tvErrorStatus.visibility = View.GONE
                            rvVideos.visibility = View.VISIBLE
                            videoAdapter.updateData(videoList)
                        }
                    } else {
                        showStatus("فشل في معالجة الطلب، يرجى المحاولة لاحقاً ⚠️", isError = true)
                    }
                }

                override fun onFailure(call: Call<YoutubeResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    showStatus("خطأ في الاتصال! تأكد من اتصال هاتفك بالإنترنت 🌐", isError = true)
                }
            })
    }
}