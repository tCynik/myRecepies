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

class RecipesAdapter: RecyclerView.Adapter<RecipesAdapter.MyViewHolder>() {
    //val numberItems: Int = 10
    var myClickListener: OnItemClickListener? = null

    fun setOnClickListener(listener: OnItemClickListener) {
        myClickListener = listener
    }

    var recipesContent = listOf<SingleRecipe>()
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

        fun bindCurrentHolder(contentLine: SingleRecipe) { // функция, выполняемая при биндинге к конкретному холдеру
            name.text = contentLine.name
            headline.text = contentLine.headline
            description.text = contentLine.description
            difficulty.text = contentLine.difficulty.toString()
            calories.text = contentLine.calories.toString()
            fats.text = contentLine.fats.toString()
            proteins.text = contentLine.proteins.toString()
            carbos.text = contentLine.carbos.toString()

            placePhoto(contentLine.pre_image)
        }

        private fun placePhoto(pre_image: PictureModel) {
            if (pre_image.image != null) {
                imagePlace.setImageDrawable(pre_image.image)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val context = parent.context
        val itemId = R.layout.item_layout // лайаут одного айтема
        val inflator = LayoutInflater.from(context)

        val view = inflator.inflate(itemId, parent, false)

        val viewHolder = MyViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (recipesContent.isNotEmpty()) { // если данных пока нет, биндить нечего
            val lineData = recipesContent[position]
            holder.bindCurrentHolder(lineData)
        }

        holder.itemView.setOnClickListener {view -> val currentPosition = holder.bindingAdapterPosition
        myClickListener!!.onItemClick(view, currentPosition)}
    }

    override fun getItemCount(): Int {
        return  recipesContent.size
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
}

