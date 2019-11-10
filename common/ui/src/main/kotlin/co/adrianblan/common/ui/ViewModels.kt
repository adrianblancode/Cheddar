package co.adrianblan.common.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import javax.inject.Provider

/*
TODO fails due to backend IR issues with compose
https://youtrack.jetbrains.com/issue/KT-34583

class ViewModelFactory<V : ViewModel>(private val provider: Provider<V>) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = provider.get() as T
}

inline fun <reified V : ViewModel> Fragment.provideViewModel(provider: Provider<V>): V =
    ViewModelProvider(this, ViewModelFactory(provider)).get()

inline fun <reified V : ViewModel> FragmentActivity.provideViewModel(provider: Provider<V>): V =
    ViewModelProvider(this, ViewModelFactory(provider)).get()

 */