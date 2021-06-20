package com.example.dudu

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.dudu.databinding.MainActivityBinding
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    companion object {
        const val PERCENTAGE_TO_HIDE_HEADER = 0.3f
        const val PERCENTAGE_TO_SHOW_FIXED_HEADER = 0.9f
    }

    private lateinit var binding: MainActivityBinding
    private var isHeaderVisible = true
    private var isFixedHeaderVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        binding.appBarLayout.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                handleHeadersVisibility(verticalOffset)
            }
        )
    }

    private fun handleHeadersVisibility(offset: Int) {
        val maxScroll = binding.appBarLayout.totalScrollRange
        val percentage = abs(offset) / maxScroll.toFloat()
        with(binding) {
            when {
                percentage >= PERCENTAGE_TO_SHOW_FIXED_HEADER -> {
                    if (!isFixedHeaderVisible) {
                        tvToolbarTitle.startAlphaAnimation(100, View.VISIBLE)
                        ibMenuVisibility.startAlphaAnimation(100, View.VISIBLE)
                        tbFixedHeader.elevation =
                            this@MainActivity.resources.getDimensionPixelSize(R.dimen.fixed_toolbar_elevation)
                                .toFloat()
                        isFixedHeaderVisible = true
                    }
                }
                percentage >= PERCENTAGE_TO_HIDE_HEADER -> {
                    if (isHeaderVisible) {
                        clHeader.startAlphaAnimation(200, View.INVISIBLE)
                        isHeaderVisible = false
                    }
                }
                else -> {
                    if (isFixedHeaderVisible) {
                        tvToolbarTitle.startAlphaAnimation(100, View.INVISIBLE)
                        ibMenuVisibility.startAlphaAnimation(100, View.INVISIBLE)
                        tbFixedHeader.elevation = 0f
                        isFixedHeaderVisible = false
                    }
                    if (!isHeaderVisible) {
                        clHeader.startAlphaAnimation(200, View.VISIBLE)
                        isHeaderVisible = true
                    }
                }
            }
        }
    }
}