package com.testtask.myrecipes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.testtask.myrecipes.data.network.ImageDownloader
import com.testtask.myrecipes.data.storage.DataBaseHelper
import com.testtask.myrecipes.data.storage.Storage
import com.testtask.myrecipes.data.storage.image_load_save.ImageLoader
import com.testtask.myrecipes.data.storage.image_load_save.ImageSaver
import com.testtask.myrecipes.domain.ErrorsProcessor
import com.testtask.myrecipes.presentation.RecipesAdapter
import com.testtask.myrecipes.presentation.RecipeViewModel
import com.testtask.myrecipes.presentation.interfaces.ToasterAndLogger

class MainActivity : AppCompatActivity() {
    var myRecyclerView: RecyclerView? = null
    val recipesAdapter = RecipesAdapter()
    var recipesViewModel: RecipeViewModel? = null

    val logger = object : ToasterAndLogger {
        override fun printToast(message: String) {
            makeToast(message)
        }

        override fun printLog(messge: String) {
            println(messge)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myRecyclerView = findViewById(R.id.recepies_recycer)
        val layoutManager = LinearLayoutManager(this)
        myRecyclerView!!.layoutManager = layoutManager

        myRecyclerView!!.setHasFixedSize(true) // нужно тупо для эффективности
        myRecyclerView!!.adapter = recipesAdapter

        // инициируем ViewModel и обсервер ливдаты VM
        recipesViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)

        // готовим интерфейсы репозиториев для работы с контекстом при обновлении информации
        val storageRepository = Storage(this, DataBaseHelper(this), logger = logger)
        // передаем зависимости во ВМ
        val errorProcessor = ErrorsProcessor()
        val imageDownloader = ImageDownloader(errorProcessor)
        val imageLoader = ImageLoader(this, logger)
        val imageSaver = ImageSaver(this, logger)

        recipesViewModel!!.initRepositoryManager(imageDownloader = imageDownloader, imageLoader = imageLoader, imageSaver = imageSaver)

        // запуск обновления данных
        recipesViewModel!!.updateDataWhenActivityCreated( // инициируем обновление данных
            repositoryStorage = storageRepository)
        // todo: обновление запускается автоматически при onCreate. При перевороте экрана так же будет обновляться, а это не нужно

    }

    override fun onStart() {
        super.onStart()
        initObservers()
    }

    private fun initObservers() {
        recipesViewModel!!.publicDataLive.observe(
            this,
            Observer{ recipesData -> recipesAdapter.recipesContent = recipesData })
    }

    private fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}