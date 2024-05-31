package com.example.storyappku.ui.otentikasi.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.storyappku.R
import com.example.storyappku.databinding.ActivityRegisterBinding
import com.example.storyappku.ui.ViewModelFactory
import com.example.storyappku.ui.otentikasi.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val factory: ViewModelFactory by lazy { ViewModelFactory.getInstance(this) }
    private val registerViewModel: RegisterViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        setupAction()
        playAnimation()

        binding.haveAccountTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun playAnimation() {
        val viewsToAnimate = listOf(
            binding.tvRegister,
            binding.tvRegisterName,
            binding.edRegisterName,
            binding.tvRegisterEmail,
            binding.edRegisterEmail,
            binding.tvRegisterPassword,
            binding.edRegisterPassword,
            binding.haveAccountTextView,
            binding.registerButton
        )

        AnimatorSet().apply {
            playSequentially(
                *viewsToAnimate.map { view ->
                    ObjectAnimator.ofFloat(view, View.ALPHA, 1f).setDuration(400)
                }.toTypedArray()
            )
            startDelay = 500
        }.start()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarRegister.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setupAction() {
        binding.registerButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            when {
                name.isEmpty() -> binding.edRegisterName.error = getString(R.string.input_name)
                email.isEmpty() -> binding.edRegisterEmail.error = getString(R.string.input_email)
                password.isEmpty() -> binding.edRegisterPassword.error = getString(R.string.input_password)
                password.length < 8 -> binding.edRegisterPassword.error = getString(R.string.label_validation_password)
                else -> {
                    registerViewModel.registerUser(name, email, password).observe(this) { result ->
                        when (result.message) {
                            "201" -> {
                                showSuccessDialog()
                                Handler(Looper.getMainLooper()).postDelayed({
                                    navigateToLogin()
                                }, 2000)
                            }
                            "400" -> {
                                showErrorDialog()
                            }
                            else -> {
                                showLoading(true)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showSuccessDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.info)
            .setMessage(R.string.validate_register_success)
            .setIcon(R.drawable.ic_ceklis)

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            alertDialog.dismiss()
        }, 2000)
    }

    private fun showErrorDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.info)
            .setMessage(R.string.validate_register_failed)
            .setIcon(R.drawable.ic_close_red)

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            alertDialog.dismiss()
        }, 2000)
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}