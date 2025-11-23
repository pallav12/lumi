package com.desktop.lumi.viewmodel

import androidx.lifecycle.ViewModel
import com.desktop.lumi.domain.repository.ReflectionRepository
import com.desktop.lumi.home.presentation.ReflectionViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AndroidReflectionViewModel @Inject constructor(
    reflectionRepository: ReflectionRepository
) : ViewModel() {
    val viewModel = ReflectionViewModel(reflectionRepository)
}

