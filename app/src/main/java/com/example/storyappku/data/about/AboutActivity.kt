package com.example.storyappku.data.about

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.storyappku.R

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        supportActionBar?.title = "About"
    }
}