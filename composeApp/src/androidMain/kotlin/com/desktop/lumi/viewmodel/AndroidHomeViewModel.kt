package com.desktop.lumi.viewmodel

import androidx.lifecycle.ViewModel
import com.desktop.lumi.domain.repository.InsightsRepository
import com.desktop.lumi.domain.repository.PersonRepository
import com.desktop.lumi.domain.repository.ReflectionRepository
import com.desktop.lumi.home.HomeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AndroidHomeViewModel @Inject constructor(
    personRepository: PersonRepository,
    insightsRepository: InsightsRepository,
    reflectionRepository: ReflectionRepository
) : ViewModel() {
    val viewModel = HomeViewModel(
        personRepository,
        insightsRepository,
        reflectionRepository
    )
}

