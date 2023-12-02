package com.example.testapplication.sreens
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.testapplication.data.api.*
import com.example.testapplication.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val apiData = RetrofitInstance
    
    private var entityList: List<Data> = emptyList()
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fetchData()
        binding.btnNext.setOnClickListener { showNextObject() }
    }

    private fun fetchData() {
        apiData.apiService.getItems().enqueue(object : Callback<ApiResponse<List<Data>>> {
            override fun onResponse(call: Call<ApiResponse<List<Data>>>, response: Response<ApiResponse<List<Data>>>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.d("MainActivity", "Data: ${apiResponse?.data}")
                    if (apiResponse?.data != null && apiResponse.data.isNotEmpty()) {
                        entityList = apiResponse.data
                        showObject(entityList[currentIndex].id)
                    } else {
                        Log.e("MainActivity", "Empty or null data received")
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<Data>>>, t: Throwable) {
                Log.e("MainActivity", "Error fetching data: ${t.message}")
            }
        })
    }


    private fun showObject(id: Int) {
        apiData.apiService.getObject(id).enqueue(object : Callback<DataX> {
            override fun onResponse(call: Call<DataX>, response: Response<DataX>) {
                if (response.isSuccessful) {
                    val responseObject = response.body()
                    Log.d("MainActivity", "Response object: $responseObject")
                    responseObject?.let { handleObject(it) }
                }
            }

            override fun onFailure(call: Call<DataX>, t: Throwable) {
                Log.e("MainActivity", "Error fetching object: ${t.message}")
            }
        })
    }

    private fun handleObject(responseObject: DataX) {
        when (responseObject.type) {
            "text" -> {
                showText(responseObject.message ?: "")
                binding.tvText.visibility = View.VISIBLE
                binding.img.visibility = View.GONE
            }
            "webview" -> {
                showWebView(responseObject.url ?: "")
                binding.tvText.visibility = View.GONE
                binding.img.visibility = View.GONE
            }
            "image" -> {
            showImage(responseObject.url ?: "")
                binding.tvText.visibility = View.GONE
                binding.img.visibility = View.VISIBLE
            }
            "game"-> showNextObject()
        }
    }

    private fun showText(message: String) {
        binding.tvText.text = message
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun showWebView(url: String) {
        binding.webWiew.loadUrl(url)
        binding.webWiew.settings.javaScriptEnabled = true

    }

    private fun showImage(url: String) {
        Glide.with(binding.root)
            .load(url)
            .fitCenter()
            .into(binding.img)
    }

    private fun showNextObject() {
        if (entityList.isNotEmpty()) {
            currentIndex = (currentIndex + 1) % entityList.size
            showObject(entityList[currentIndex].id)
        }
    }
}

