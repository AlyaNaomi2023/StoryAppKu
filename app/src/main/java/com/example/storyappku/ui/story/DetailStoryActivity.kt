package com.example.storyappku.ui.story

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.example.storyappku.R
import com.example.storyappku.databinding.ActivityDetailStoryBinding
import com.example.storyappku.utils.getAddressName
import com.example.storyappku.utils.withDateFormat

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    companion object {
        const val NAME = "name"
        const val CREATE_AT = "create_at"
        const val DESCRIPTION = "description"
        const val PHOTO_URL = "photoUrl"
        const val LONGITUDE = "lon"
        const val LATITUDE = "lat"
    }

    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.detail_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val photoUrl = intent.getStringExtra(PHOTO_URL)
        val name = intent.getStringExtra(NAME)
        val createAt = intent.getStringExtra(CREATE_AT)
        val description = intent.getStringExtra(DESCRIPTION)
        val lon = intent.getStringExtra(LONGITUDE)!!.toDouble()
        val lat = intent.getStringExtra(LATITUDE)!!.toDouble()
        val location = getAddressName(this@DetailStoryActivity, lat, lon)

        Glide.with(binding.root.context)
            .load(photoUrl)
            .into(binding.ivDetailPhoto)
        binding.tvDetailName.text = name
        binding.tvDetailCreatedTime.text = createAt?.withDateFormat()
        binding.tvDetailDescription.text = description
        binding.tvDetailLocation.text = location

        if (lon == 0.0 && lat == 0.0) {
            binding.avatar2.visibility = View.INVISIBLE
        } else {
            binding.avatar2.visibility = View.VISIBLE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
