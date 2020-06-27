package co.adrianblan.core.di

import co.adrianblan.core.StoryPreviewUseCase
import co.adrianblan.core.StoryPreviewUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface UseCaseModule {

    @Binds
    fun StoryPreviewUseCaseImpl.bindStoryPreview(): StoryPreviewUseCase

}