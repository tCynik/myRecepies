package com.testtask.myrecipes.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.testtask.myrecipes.R
import com.testtask.myrecipes.domain.models.PictureModel
import com.testtask.myrecipes.domain.models.SingleRecipe

class RecepiesAdapter: RecyclerView.Adapter<RecepiesAdapter.MyViewHolder>() {
    val numberItems: Int = 10
    var recipesContent = mutableListOf<SingleRecipe>()
    set(newValue) {
        field = newValue
        notifyDataSetChanged() // todo: for optimization make notifyItemChanged()
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val name = itemView.findViewById<TextView>(R.id.name)
        private val headline = itemView.findViewById<TextView>(R.id.headline)
        private val description = itemView.findViewById<TextView>(R.id.description)
        private val difficulty = itemView.findViewById<TextView>(R.id.difficulty)
        private val calories = itemView.findViewById<TextView>(R.id.calories)
        private val fats = itemView.findViewById<TextView>(R.id.fats)
        private val proteins = itemView.findViewById<TextView>(R.id.proteins)
        private val carbos = itemView.findViewById<TextView>(R.id.carbos)
        private val imagePlace = itemView.findViewById<ImageView>(R.id.image_place)

        fun bind(position: Int, content: SingleRecipe) { // функция, выполняемая при биндинге к конкретному холдеру
            name.text = content.name
            headline.text = content.headline
            description.text = content.description
            difficulty.text = content.difficulty.toString()
            calories.text = content.calories.toString()
            fats.text = content.fats.toString()
            proteins.text = content.proteins.toString()
            carbos.text = content.carbos.toString()

            placePhoto(content.pre_image)
        }

        private fun placePhoto(pre_image: PictureModel) {
            if (pre_image.image != null)
                imagePlace.setImageDrawable(pre_image.image)
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
        val lineData = recipesContent[position]
        holder.bind(position, lineData)
    }

    override fun getItemCount(): Int {
        return  numberItems
    }
}