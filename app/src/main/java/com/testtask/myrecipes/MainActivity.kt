package com.testtask.myrecipes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.testtask.myrecipes.data.storage.Storage
import com.testtask.myrecipes.presentation.RecipesAdapter
import com.testtask.myrecipes.presentation.RecipeViewModel

class MainActivity : AppCompatActivity() {
    var myRecyclerView: RecyclerView? = null
    val recipesAdapter = RecipesAdapter()
    var recipesViewModel: RecipeViewModel? = null


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
        val storageRepository = Storage()
        // запуск обновления данных
        recipesViewModel!!.updateDataWhenActivityStarted( // инициируем обновление данных
            repositoryStorage = storageRepository)
    }

    override fun onStart() {
        super.onStart()
        initObservers()
    }

    private fun initObservers() {
        recipesViewModel!!.recipesDataLive.observe(
            this,
            Observer{ recipesData -> recipesAdapter.recipesContent = recipesData})
    }
}