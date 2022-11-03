package com.swameal.screentimespent.data.di

import android.content.Context
import androidx.room.Room
import com.swameal.screentimespent.data.DataConstants
import com.swameal.screentimespent.data.db.ScreenTimeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideScreenTimeDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        ScreenTimeDatabase::class.java,
        DataConstants.SCREEN_TIME_DATABASE
    ).build()
}