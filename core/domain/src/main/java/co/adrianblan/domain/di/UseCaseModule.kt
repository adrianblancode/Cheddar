package co.adrianblan.domain.di

import co.adrianblan.domain.StoryPreviewUseCase
import co.adrianblan.domain.StoryPreviewUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface UseCaseModule {

    @Binds
    fun StoryPreviewUseCaseImpl.bindStoryPreview(): StoryPreviewUseCase

}