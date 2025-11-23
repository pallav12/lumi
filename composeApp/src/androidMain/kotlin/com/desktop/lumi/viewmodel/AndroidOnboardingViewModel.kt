package com.desktop.lumi.viewmodel

import androidx.lifecycle.ViewModel
import com.desktop.lumi.domain.repository.PersonRepository
import com.desktop.lumi.onboarding.presentation.viewmodel.OnboardingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AndroidOnboardingViewModel @Inject constructor(
    personRepository: PersonRepository
) : ViewModel() {
    val viewModel = OnboardingViewModel(personRepository)
}

