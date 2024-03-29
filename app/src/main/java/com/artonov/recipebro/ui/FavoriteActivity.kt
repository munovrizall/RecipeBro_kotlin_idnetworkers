package com.artonov.recipebro.ui

import android.content.Intent
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
import com.artonov.recipebro.adapter.FavoriteAdapter
import com.artonov.recipebro.data.database.MealEntity
import com.artonov.recipebro.databinding.ActivityFavoriteBinding
import com.artonov.recipebro.viewmodel.FavoriteViewModel

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private val favoriteViewModel by viewModels<FavoriteViewModel>()
    private val favoriteAdapter by lazy { FavoriteAdapter() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val colorStateList = ContextCompat.getColorStateList(this, R.color.bottom_nav_item_color)

        binding.apply {
            bottomNavigationView.menu.getItem(1).setChecked(true)
            bottomNavigationView.itemTextColor = colorStateList
            bottomNavigationView.itemIconTintList = colorStateList
            bottomNavigationView.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.MainActivity -> {
                        val intent = Intent(this@FavoriteActivity, MainActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(0, 0)
                        true
                    }
                    R.id.FavoriteActivity -> {
                        true
                    }
                    else -> {
                        true
                    }
                }
            }
        }

        favoriteViewModel.favoriteMealList.observe(this) { res ->
            if (res.isEmpty()) {
                binding.apply {
                    rvFavoriteList.isVisible = false
                    errorText.isVisible = true
                }
            } else {
                Log.d("test", res.toString())
                binding.rvFavoriteList.apply {
                    adapter = favoriteAdapter
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(
                        this@FavoriteActivity
                    )
                }

                favoriteAdapter.apply {
                    setData(res)
                    setOnItemClickCallback(object:FavoriteAdapter.IOnFavoriteItemCallBack{
                        override fun onFavoriteItemClickCallback(data: MealEntity) {
                            val detailFavorite = Intent(this@FavoriteActivity, FavoriteDetailActivity::class.java)
                            detailFavorite.putExtra(FavoriteDetailActivity.EXTRA_FAVORITE_MEAL,data)
                            startActivity(detailFavorite)
                        }
                    })
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
}