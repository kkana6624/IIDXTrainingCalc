package com.example.iidxtrainingcalc.di

import com.example.iidxtrainingcalc.domain.GreenNumberCalculator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGreenNumberCalculator(): GreenNumberCalculator {
        return GreenNumberCalculator()
    }
}
