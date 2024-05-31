package com.example.storyappku.ui.story

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.storyappku.R
import com.example.storyappku.databinding.ActivityAddStoryBinding
import com.example.storyappku.ui.ViewModelFactory
import com.example.storyappku.ui.otentikasi.login.LoginViewModel
import com.example.storyappku.ui.main.MainActivity
import com.example.storyappku.ui.main.MainViewModel
import com.example.storyappku.utils.getAddressName
import com.example.storyappku.utils.reduceFileImage
import com.example.storyappku.utils.rotateBitmap
import com.example.storyappku.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File

@Suppress("DEPRECATION")
class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding

    private val viewModelFactory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val loginViewModel: LoginViewModel by viewModels {
        viewModelFactory
    }
    private val mainViewModel: MainViewModel by viewModels {
        viewModelFactory
    }
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    private var lon = 0.0
    private var lat = 0.0

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.list_location)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnAddCamera.setOnClickListener { startCameraX() }
        binding.btnAddGalery.setOnClickListener { startGallery() }
        binding.buttonSetLocation.setOnClickListener {
            lon = mainViewModel.coordLon.value.toString().toDouble()
            lat = mainViewModel.coordLat.value.toString().toDouble()
            val location = getAddressName(this@AddStoryActivity, lat, lon).toString()
            binding.tvLocation.text = location
        }
        binding.buttonAdd.setOnClickListener {
            if (getFile != null) {
                if (binding.edAddDescription.text.toString().isNotEmpty()) {
                    val file = reduceFileImage(getFile as File)
                    loginViewModel.getUser().observe(this) { user ->
                        mainViewModel.postNewStory(
                            user.token,
                            file,
                            binding.edAddDescription.text.toString(),
                            lon.toString(),
                            lat.toString()
                        ).observe(this) { result ->
                            if (result.message == "") {
                                showLoading(true)
                            } else {
                                showLoading(false)
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        this@AddStoryActivity,
                        getString(R.string.description_mandatory),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@AddStoryActivity,
                    getString(R.string.image_mandatory),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getMyLastLocation()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarAdd.visibility = View.VISIBLE
        } else {
            binding.progressBarAdd.visibility = View.GONE

            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraStoryActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private var getFile: File? = null
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )

            binding.tvAddImg.setImageBitmap(result)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
            getFile = myFile

            binding.tvAddImg.setImageURI(selectedImg)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }

                else -> {
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    postCoordinate(location.latitude, location.longitude)
                } else {
                    postCoordinate(0.0, 0.0)
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun postCoordinate(latitude: Double, longitude: Double) {
        mainViewModel.coordLat.postValue(latitude)
        mainViewModel.coordLon.postValue(longitude)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}