package com.artonov.recipebro.ui

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.artonov.recipebro.R
import com.artonov.recipebro.adapter.RecipeAdapter
import com.artonov.recipebro.data.network.handler.NetworkResult
import com.artonov.recipebro.databinding.ActivityMainBinding
import com.artonov.recipebro.model.MealsItem
import com.artonov.recipebro.viewmodel.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {
    private val mainViewModel by viewModels<MainViewModel>()
    private val recipeAdapter by lazy {
        RecipeAdapter()
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)


        mainViewModel.recipeList.observe(this@MainActivity) { res ->
            when (res) {
                is NetworkResult.Loading -> {
                    handleUi(
                        recyclerView = false,
                        progressbar = true,
                        errorTv = false,
                    )
                }
                is NetworkResult.Error -> {
                    binding.errorText.text = res.errorMessage
                    handleUi(
                        recyclerView = false,
                        progressbar = false,
                        errorTv = true,
                    )
                }
                is NetworkResult.Success -> {
                    val recipeAdapter = RecipeAdapter()
                    Log.d("SUCCESS", "Success retrieved data")
                    recipeAdapter.setData(res.data?.meals as List<MealsItem>)

                    binding.rvRecipesList.apply {
                        layoutManager = LinearLayoutManager(this@MainActivity)
                        setHasFixedSize(true)
                        adapter = recipeAdapter
                    }

                    recipeAdapter.setOnItemClickCallback(object : RecipeAdapter.IOnItemCallBack {
                        override fun onItemClickCallback(data: MealsItem) {
                            val intent = Intent(this@MainActivity, DetailActivity::class.java)
                            intent.putExtra(DetailActivity.EXTRA_RECIPE, data)
                            startActivity(intent)
                        }
                    })

                    handleUi(
                        recyclerView = true,
                        progressbar = false,
                        errorTv = false,
                    )
                }
            }
        }

        val colorStateList = ContextCompat.getColorStateList(this, R.color.bottom_nav_item_color)
        binding.apply {
            bottomNavigationView.itemTextColor = colorStateList
            bottomNavigationView.itemIconTintList = colorStateList
            bottomNavigationView.setOnNavigationItemSelectedListener {item ->
                when(item.itemId) {
                    R.id.MainActivity -> {
                        true
                    }
                    R.id.FavoriteActivity -> {
                        val intent = Intent(this@MainActivity, FavoriteActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(0, 0)
                        true
                    }
                    else -> {true}
                }

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_appbar, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.aboutActivity -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleUi(
        recyclerView: Boolean,
        progressbar: Boolean,
        errorTv: Boolean
    ) {
        binding.apply {
            rvRecipesList.isVisible = recyclerView
            progressBar.isVisible = progressbar
            errorText.isVisible = errorTv
        }
    }
}