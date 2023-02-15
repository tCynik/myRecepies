package com.testtask.myrecipes.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.testtask.myrecipes.R

class MyAdapter: RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    val numberItems: Int = 10
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val textPlace = itemView.findViewById<TextView>(R.id.text_place)
        private val imagePlace = itemView.findViewById<ImageView>(R.id.image_place)
        fun bind(position: Int) { // функция, выполняемая при биндинге к конкретному холдеру
            textPlace.text = "position is $position"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val context = parent.context
        val itemId = R.layout.item_layout // лайаут одного айтема
        val inflator = LayoutInflater.from(context)

        val view = inflator.inflate(itemId, parent, false)

        val viewHolder = MyViewHolder(view)
// Далее значения при создании вью холдера
// значение, постоянное для каждого вью холдера (повторяющееся при переиспользовании)
//        viewHolder.viewHolderNumber.text = "VH index = $nubmerViewHolders"
//        nubmerViewHolders++
        return viewHolder

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return  numberItems
    }
}