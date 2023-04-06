package com.testtask.myrecipes.presentation

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModel
import com.testtask.myrecipes.MainActivity
import com.testtask.myrecipes.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ImageFragment(val context: MainActivity) : Fragment() { // контекст передается для обеспечения вызова runOnUiThread
    private var viewModel: RecipeViewModel? = null

    private var imagePlace: ImageView? = null
    private var textPlace: TextView? = null

    private var recipeImage: Drawable? = null
    private var recipeName = "EMPTY"

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_image, container, false)
        imagePlace = view.findViewById(R.id.recipe_picture)
        textPlace = view.findViewById(R.id.recipe_name)
        viewModel!!.publicCurrentRecipeLive.observe(viewLifecycleOwner) { singleRecipe ->
            if (singleRecipe != null) {// context.runOnUiThread {
                // todo: попробовать запустить без runOnUiThread, по идее должна выпадать ошибка обращения к UI не из главного потока
                val name = singleRecipe.name
                var picture = singleRecipe.full_image.image
                Log.i("bugfix: ImageFragment", "ready to show picture full = ${picture!=null}")
                if (picture == null) picture = singleRecipe.pre_image.image
                imagePlace!!.setImageDrawable(picture)
                textPlace!!.text = name
            }
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ImageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String, context: MainActivity) =
            ImageFragment(context).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun setViewModel(viewModel: RecipeViewModel) {
        this.viewModel = viewModel
    }

    fun setImage(image: Drawable) {
        recipeImage = image
    //imagePlace!!.setImageDrawable(image)
    }

    fun setRecipeName(text: String) {
        recipeName = text
        //textPlace!!.text = text
    }
}