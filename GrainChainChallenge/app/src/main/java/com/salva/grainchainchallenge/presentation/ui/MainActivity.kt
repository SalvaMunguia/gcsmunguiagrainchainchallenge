package com.salva.grainchainchallenge.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.replace
import com.salva.grainchainchallenge.R
import com.salva.grainchainchallenge.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

       supportFragmentManager.beginTransaction().add(R.id.main_container,MapsFragment()).commit()

    }
}