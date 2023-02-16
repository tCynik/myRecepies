package com.testtask.myrecipes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.testtask.myrecipes.data.network.NetRepository
import com.testtask.myrecipes.data.network.URLConstantsSet
import com.testtask.myrecipes.data.storage.StorageRepository
import com.testtask.myrecipes.presentation.RecepiesAdapter
import com.testtask.myrecipes.presentation.RecipeViewModel

class MainActivity : AppCompatActivity() {
    var myRecyclerView: RecyclerView? = null
    //private val elementsCount = 20 - количесвто элементов в адаптере, а надо ли?
    val recepiesAdapter = RecepiesAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myRecyclerView = findViewById(R.id.recepies_recycer)
        val layoutManager = LinearLayoutManager(this)
        myRecyclerView!!.layoutManager = layoutManager

        myRecyclerView!!.setHasFixedSize(true) // нужно тупо для эффективности
        myRecyclerView!!.adapter = recepiesAdapter

        // инициируем ViewModel обсервер ливдаты VM
        val recipesViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)
        recipesViewModel.recipesData.observe(this, Observer{ recipesData -> recepiesAdapter.recipesContent = recipesData})

        // готовим интерфейсы репозиториев для работы с контекстом при обновлении информации
        val netRepository = NetRepository()
        val storageRepository = StorageRepository()
        // запуск обновления данных
        recipesViewModel.updateDataWhenActivityStarted(
            repositoryNetwork = netRepository,
            repositoryStorage = storageRepository)

        val constantsURL = URLConstantsSet(
            baseURL = getString(R.string.base_url),
            recipesList = getString(R.string.type_url))

    }
}