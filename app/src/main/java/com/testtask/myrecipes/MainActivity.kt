package com.testtask.myrecipes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.testtask.myrecipes.presentation.MyAdapter

class MainActivity : AppCompatActivity() {
    var myRecyclerView: RecyclerView? = null
    //private val elementsCount = 20 - количесвто элементов в адаптере, а надо ли?
    val myAdapter = MyAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myRecyclerView = findViewById(R.id.recepies_recycer)
        val layoutManager = LinearLayoutManager(this)
        myRecyclerView!!.layoutManager = layoutManager

        myRecyclerView!!.setHasFixedSize(true) // нужно тупо для эффективности
        myRecyclerView!!.adapter = myAdapter

    }
}