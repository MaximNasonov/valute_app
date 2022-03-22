package com.example.valute

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.valute.databinding.ValteItemBinding
import com.example.valute.tmp.Valutes

class Adapter: RecyclerView.Adapter<Adapter.Holder>() {
    val valuteList = ArrayList<Valutes>()
    class Holder(item: View): RecyclerView.ViewHolder(item) {
        val binding = ValteItemBinding.bind(item)
        fun bind(valut: Valutes) = with(binding){
            textView.text = valut.name
            textView2.text = valut.charcode
            textView3.text = valut.value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.valte_item, parent,false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(valuteList[position])
    }

    override fun getItemCount(): Int {
        return valuteList.size
    }

    fun addValute(valute: Valutes){
        valuteList.add(valute)
        notifyDataSetChanged()
    }
}