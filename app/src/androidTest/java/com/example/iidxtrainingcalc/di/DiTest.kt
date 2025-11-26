package com.example.iidxtrainingcalc.di

import com.example.iidxtrainingcalc.domain.GreenNumberCalculator
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class DiTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var calculator: GreenNumberCalculator

    @Test
    fun verifyInjection() {
        // Inject dependencies
        hiltRule.inject()

        // Verify that the calculator is injected successfully
        assertNotNull("GreenNumberCalculator should be injected", calculator)
    }
}
