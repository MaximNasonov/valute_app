package com.example.valute

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
    private var count = 0.0
    private var number = 0.0
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
        val listValute = maper.get("Valute") as Map<String,Any>
        val listNameWithNominal = listValute.map {
            createValuteNameWithNominal(it.value as Map<String,Any>)
        }
        val listValueWithR = listValute.map {
            creatValueWithR(it.value as Map<String,Any>)
        }
        val listCharCode = listValute.map {
            createCharCode(it.value as Map<String,Any>)
        }
        val listName = listValute.map {
            createValuteName(it.value as Map<String,Any>)
        }
        val listValueWithNominal = listValute.map {
            creatValueWithNominal(it.value as Map<String,Any>)
        }
        init(listNameWithNominal,listCharCode,listValueWithR,listValute.size)
        val spiner = binding.spinner
        val adapter2: ArrayAdapter<String> = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, listName)
        spiner.adapter = adapter2
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spiner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                count = listValueWithNominal.get(position)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        binding.button.setOnClickListener {
            number = binding.value.text.toString().toDouble()
            binding.result.text = String.format("%.2f", (number/count))
        }
    }
    override fun onDestroy() {
        executor.shutdown()
        super.onDestroy()
    }

    fun createCharCode(map: Map<String,Any>): String {
        val CharCode = map.get("CharCode").toString()
        return CharCode
    }
    fun createValuteNameWithNominal(map: Map<String,Any>): String {
        var Name= map.get("Name").toString()
        val Nominal= map.get("Nominal") as Double
        if (Nominal>1) {
            var zxc = String.format("%.0f",Nominal)
            Name = ("$zxc " + "$Name")
        }
        return "$Name"
    }
    fun createValuteName(map: Map<String,Any>): String {
        var name= map.get("Name").toString()
        return name
    }
    fun creatValueWithR(map: Map<String,Any>): String {
        var Value= map.get("Value") as Double
        return String.format("%.4f", Value) + " ₽"
    }
    fun creatValueWithNominal(map: Map<String,Any>): Double {
        var Value= map.get("Value") as Double
        val Nominal= map.get("Nominal") as Double
            Value/=Nominal
        return Value
    }
    private fun init(name:List<String>,value:List<String>,valute:List<String>,size:Int){
        binding.apply {
            rcView.layoutManager = LinearLayoutManager(this@MainActivity)
            rcView.adapter = adapter
            val a = size - 1
            for(i in 0..a) {
                val item = Valutes(name[i], value[i], valute[i])
                adapter.addValute(item)
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




