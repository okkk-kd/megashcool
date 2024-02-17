package ru.sample.duckapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import ru.sample.duckapp.domain.Duck
import ru.sample.duckapp.infra.Api
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {
    
    private val api = Api.ducksApi
    
    private lateinit var imageView: ImageView
    private lateinit var editText: TextInputEditText
    private lateinit var progress: ProgressBar
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        imageView = findViewById(R.id.duckImageView)
        editText = findViewById(R.id.editText)
        progress = findViewById(R.id.progressBar)
    }
    
    @OptIn(DelicateCoroutinesApi::class)
    fun getNext(v: View) {
        GlobalScope.launch(Dispatchers.IO) {
            runOnUiThread {
                progress.visibility = View.VISIBLE
            }
                if (editText.text.isNullOrEmpty()) {
                    api.getRandomDuck().enqueue(object : Callback<Duck> {
                        override fun onResponse(call: Call<Duck>, response: Response<Duck>) {
                            if (response.isSuccessful) {
                                val duck = response.body() as Duck
                                Glide.with(v.context).load(duck.url).into(imageView);
                                runOnUiThread {
                                    progress.visibility = View.GONE
                                }
                            }
                        }

                        override fun onFailure(call: Call<Duck>, t: Throwable) {
                        }
                    })
                } else if
                    (editText.text.toString().toInt() in 100..103 ||
                    editText.text.toString().toInt() in 200..226 ||
                    editText.text.toString().toInt() in 300..308 ||
                    editText.text.toString().toInt() in 400..429 ||
                    editText.text.toString().toInt() in 500..511 ||
                    editText.text.toString().toInt() in 520..526 ||
                    editText.text.toString().toInt() == 451 ||
                    editText.text.toString().toInt() == 499 ||
                            editText.text.isNullOrEmpty()
                            )
                 {
                    val code = editText.text.toString().toInt()
                    val codeStr = editText.text.toString()
                    api.getHttpCodeDuck(
                        code = codeStr
                    ).enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            if (response.isSuccessful) {
                                response.body()?.let { body ->
                                    val imageFile = saveResponseBodyAsFile(body)
                                    if (imageFile != null) {
                                        Glide.with(v.context)
                                            .load(imageFile)
                                            .into(imageView)
                                    }
                                }
                                runOnUiThread {
                                    progress.visibility = View.GONE
                                }
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            t.message
                        }
                    })
                }
//            }
        }
    }
    
    fun saveResponseBodyAsFile(body: ResponseBody): File? {
        try {
            val file = File.createTempFile("prefix", ".jpeg")
            file.outputStream().use { fileOutputStream ->
                body.byteStream().use { inputStream ->
                    inputStream.copyTo(fileOutputStream)
                }
            }
            return file
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}