package co.adrianblan.network

import dagger.Module
import dagger.Provides
import dagger.multibindings.Multibinds
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
abstract class NetworkModule {

    @Module
    companion object {

        @JvmStatic
        @Singleton
        @Provides
        fun provideOkHttp(
            applicationInterceptors: Set<@JvmSuppressWildcards ApplicationInterceptor>,
            networkInterceptors: Set<@JvmSuppressWildcards NetworkInterceptor>
        ): OkHttpClient =
            OkHttpClient.Builder()
                .apply {

                    addInterceptor(
                        HttpLoggingInterceptor { message ->
                            Timber.tag("OkHttp").d(message)
                        }.apply {
                            level = HttpLoggingInterceptor.Level.BASIC
                        }
                    )

                    applicationInterceptors.forEach {
                        addInterceptor(it)
                    }
                    networkInterceptors.forEach {
                        addNetworkInterceptor(it)
                    }
                }
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build()
    }

    @Multibinds
    abstract fun provideApplicationInterceptors(): Set<ApplicationInterceptor>

    @Multibinds
    abstract fun provideNetworkInterceptors(): Set<NetworkInterceptor>
}