package co.adrianblan.network

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.multibindings.Multibinds
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
abstract class NetworkModule {

    @Module
    companion object {

        @Singleton
        @Provides
        fun provideOkHttp(
            applicationInterceptors: Set<ApplicationInterceptor>,
            networkInterceptors: Set<NetworkInterceptor>
        ): OkHttpClient =
            OkHttpClient.Builder()
                .apply {

                    if (BuildConfig.DEBUG) {
                        addInterceptor(
                            HttpLoggingInterceptor(
                                object : HttpLoggingInterceptor.Logger {
                                    override fun log(message: String) {
                                        Timber.tag("OkHttp").v(message)
                                    }
                                }
                            ).apply {
                                level = HttpLoggingInterceptor.Level.BASIC
                            }
                        )
                    }

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

        @JvmStatic
        @Provides
        @Singleton
        fun provideRetrofit(
            okHttpClient: Lazy<OkHttpClient>
        ): Retrofit =
            Retrofit.Builder()
                // TODO replace with own URL
                .baseUrl("https://example.com/")
                // .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                // Defer expensive initialization of OkHttp until first network call
                .callFactory(
                    object : Call.Factory {
                        override fun newCall(request: Request): Call {
                            return okHttpClient.get().newCall(request)
                        }
                    }
                )
                .build()
    }

    @Multibinds
    abstract fun provideApplicationInterceptors(): Set<ApplicationInterceptor>

    @Multibinds
    abstract fun provideNetworkInterceptors(): Set<NetworkInterceptor>
}