package com.testtask.myrecipes

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.testtask.myrecipes.data.interfaces.ImageDownloaderInterface
import com.testtask.myrecipes.data.interfaces.ImageLoaderInterface
import com.testtask.myrecipes.data.interfaces.ImageSaverInterface
import com.testtask.myrecipes.data.interfaces.RecipesStorageInterface
import com.testtask.myrecipes.data.network.ImageDownloader
import com.testtask.myrecipes.data.storage.DataBaseHelper
import com.testtask.myrecipes.data.storage.RecipesStorage
import com.testtask.myrecipes.data.storage.image_load_save.ImageLoader
import com.testtask.myrecipes.data.storage.image_load_save.ImageSaver
import com.testtask.myrecipes.domain.ErrorsProcessor
import com.testtask.myrecipes.presentation.ImageFragment
import com.testtask.myrecipes.presentation.RecipesAdapter
import com.testtask.myrecipes.presentation.RecipeViewModel
import com.testtask.myrecipes.presentation.interfaces.ToasterAndLogger

class MainActivity : AppCompatActivity(), RecipesAdapter.OnItemClickListener {
    var myRecyclerView: RecyclerView? = null
    var progressBar: ProgressBar? = null

    val recipesAdapter = RecipesAdapter()
    var recipesViewModel: RecipeViewModel? = null

    var fragmentsPlace: View? = null
    val pictureFragment = ImageFragment(this)

    val logger = object : ToasterAndLogger {
        override fun printToast(message: String) {
            runOnUiThread{ makeToast(message) }
        }

        override fun printLog(message: String) {
            runOnUiThread{Log.i("bugfix-logger", message)}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentsPlace = findViewById(R.id.fragments_place)
        progressBar = findViewById(R.id.progress_bar)

        myRecyclerView = findViewById(R.id.recepies_recycer)
        val layoutManager = LinearLayoutManager(this)
        myRecyclerView!!.layoutManager = layoutManager

        myRecyclerView!!.setHasFixedSize(true) // нужно тупо для эффективности

        recipesAdapter.setOnClickListener(this) // передаем слушатель нажатий в адаптер для взаимодействия с элементами списка

        myRecyclerView!!.adapter = recipesAdapter

        // инициируем ViewModel и обсервер ливдаты VM
        recipesViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)

        pictureFragment.setViewModel(recipesViewModel!!)

        // готовим интерфейсы репозиториев для работы с контекстом при обновлении информации
        val storageRepository: RecipesStorageInterface = RecipesStorage(this, DataBaseHelper(this), logger = logger)
        // передаем зависимости во ВМ
        val errorProcessor = ErrorsProcessor()
        val imageDownloader: ImageDownloaderInterface = ImageDownloader(errorProcessor, recipesViewModel!!.viewModelScope)
        val imageLoader: ImageLoaderInterface = ImageLoader(this, logger)
        val imageSaver: ImageSaverInterface = ImageSaver(this, logger)

        recipesViewModel!!.initRepositoryManager(
            imageDownloader = imageDownloader,
            imageLoader = imageLoader,
            imageSaver = imageSaver,
            recipesStorage = storageRepository,
            logger = logger
        )

        recipesViewModel!!.setFragmentCallback(getRecipeFragmentCallback())


        // запуск обновления данных
        recipesViewModel!!.updateDataWhenActivityCreated() // инициируем обновление данных
        // todo: обновление запускается автоматически при onCreate. При перевороте экрана так же будет обновляться, а это не нужно

    }

    override fun onStart() {
        super.onStart()
        initObservers()
    }

    private fun initObservers() {
        recipesViewModel!!.publicDataLive.observe(
            this,
            Observer { recipesData ->
                recipesAdapter.recipesContent = recipesData
                if (progressBar!!.isVisible)
                    progressBar!!.visibility = View.GONE
            })

        recipesViewModel!!.publicCurrentRecipeLive.observe(
            this,
            {currentRecipe -> Log.i("bugfix - mainActivity", "CURRENT RECIPE DATA UPDATED. Full picture exist = ${currentRecipe!!.full_image.image != null}")}
        )
    }

    private fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onPictureClick(view: View, position: Int) {
        recipesViewModel!!.pictureWasClicked(position) // VM сама решает, что делать с инфой о нажатии на картинку
        Log.i ("bugfix: main", "picture in the item number $position in reciclerView was clicked")
    }

    override fun onTextClick(view: View, position: Int) {
        Log.i ("bugfix: main", "text in the item number $position in reciclerView was clicked")
    }

//    private fun openPictureFragment(picture: Drawable, recipeName: String) {
//        pictureFragment.setImage(picture)
//        pictureFragment.setRecipeName(recipeName)
//        val fTrans = supportFragmentManager.beginTransaction()
//        fTrans.add(R.id.fragments_place, pictureFragment)
//        fTrans.addToBackStack(null)
//        fTrans.commit()
//    }

    private fun getRecipeFragmentCallback(): RecipeFragmentCallback {
        return object: RecipeFragmentCallback {
            override fun setRecipeIntoFragmentByNumber(recipeNumber: Int?) {
                Log.i("bugfix: main", "called callback for item number $recipeNumber")
                recipesViewModel!!.setCurrentNumber(recipeNumber)
                val fTrans = supportFragmentManager.beginTransaction()
                fTrans.add(R.id.fragments_place, pictureFragment)
                fTrans.addToBackStack(null)
                fTrans.commit()
            }
        }
    }

    interface RecipeFragmentCallback { // интерфейс дл отображения во фрагменте информации
        fun setRecipeIntoFragmentByNumber(recipeNumber: Int?)
    }

}