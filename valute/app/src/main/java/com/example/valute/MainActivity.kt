package com.example.valute

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.valute.databinding.ActivityMainBinding
import com.example.valute.tmp.Valutes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private const val POOL_COUNT = 2

class MainActivity (
    private val executor: ExecutorService = Executors.newFixedThreadPool(POOL_COUNT)
) : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val adapter = Adapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val future = executor.submit<String> {
            Jsoup.connect("https://www.cbr-xml-daily.ru/daily_json.js")
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute()
                .body()
        }
        val result = future.get(5, TimeUnit.SECONDS)
        val maptype = object:TypeToken<Map<String,Any>>(){}.type
        val maper = Gson().fromJson<Map<String,Any>>(result, maptype)
        val qwer = maper.get("Valute") as Map<String,Any>
        val qwert = qwer.map {
            ValuteName(it.value as Map<String,Any>)
        }
        val qwert3 = qwer.map {
            ValuteValue(it.value as Map<String,Any>)
        }
        val qwert2 = qwer.map {
            ValuteCharCode(it.value as Map<String,Any>)
        }
        init(qwert,qwert2,qwert3,qwer.size)
    }
    override fun onDestroy() {
        executor.shutdown()
        super.onDestroy()
    }

    fun ValuteCharCode(map: Map<String,Any>): String {
        val CharCode = map.get("CharCode").toString()
        return CharCode
    }
    fun ValuteName(map: Map<String,Any>): String {
        var Name= map.get("Name").toString()
        val Nominal= map.get("Nominal") as Double
        if (Nominal>1) {
            var zxc = String.format("%.0f",Nominal)
            Name = ("$zxc " + "$Name")
        }
        return "$Name"
    }
    fun ValuteValue(map: Map<String,Any>): String {
        var Value= map.get("Value") as Double
        val Nominal= map.get("Nominal") as Double
        return String.format("%.4f", Value) + " ₽"
    }
    private fun init(name:List<String>,value:List<String>,valute:List<String>,size:Int){
        binding.apply {
            rcView.layoutManager = LinearLayoutManager(this@MainActivity)
            rcView.adapter = adapter
            val a = size
            for(a in 0..33) {
                val plant = Valutes(name.get(a), value.get(a), valute.get(a))
                adapter.addValute(plant)
            }

        }
    }
}

class Valute(
    val CharCode: String,
    val Name: String,
    val Value: Double,
    val Nominal: Int){
    override fun toString(): String {
        return "$CharCode,$Name,$Value,$Nominal"
    }
}




