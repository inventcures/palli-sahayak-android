package com.pallisahayak.core.voice.di

import com.pallisahayak.core.voice.OnDeviceVoiceEngine
import com.pallisahayak.core.voice.ServerVoiceEngine
import com.pallisahayak.core.voice.VoiceEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VoiceModule {

    @Provides
    @Singleton
    @Named("server")
    fun provideServerVoiceEngine(engine: ServerVoiceEngine): VoiceEngine = engine

    @Provides
    @Singleton
    @Named("onDevice")
    fun provideOnDeviceVoiceEngine(engine: OnDeviceVoiceEngine): VoiceEngine = engine
}
