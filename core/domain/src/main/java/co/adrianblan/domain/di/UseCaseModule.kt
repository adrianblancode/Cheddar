package co.adrianblan.domain.di

import co.adrianblan.domain.StoryPreviewUseCase
import co.adrianblan.domain.StoryPreviewUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface UseCaseModule {

    @Binds
    fun StoryPreviewUseCaseImpl.bindStoryPreview(): StoryPreviewUseCase

}