package com.desktop.lumi.viewmodel

import androidx.lifecycle.ViewModel
import com.desktop.lumi.domain.repository.PersonRepository
import com.desktop.lumi.settings.SettingsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AndroidSettingsViewModel @Inject constructor(
    personRepository: PersonRepository
) : ViewModel() {
    val viewModel = SettingsViewModel(personRepository)
}

