package com.dicoding.asclepius.view

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private val binding by lazy { ActivityResultBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getData()
    }

    private fun getData() {
        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI).orEmpty())
        val label = intent.getStringExtra(EXTRA_LABEL).orEmpty()
        val score = intent.getStringExtra(EXTRA_SCORE).orEmpty()
        val inferenceTime = intent.getStringExtra(EXTRA_INFERENCE_TIME).orEmpty()

        binding.apply {
            imageUri?.let {
                binding.resultImage.setImageURI(it)
            }
            resultText.text = getString(R.string.result, label, score, inferenceTime)
        }
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_LABEL = "label"
        const val EXTRA_SCORE = "score"
        const val EXTRA_INFERENCE_TIME = "inferenceTime"
    }
}