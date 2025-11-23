package com.desktop.lumi.viewmodel

import androidx.lifecycle.ViewModel
import com.desktop.lumi.domain.repository.InteractionRepository
import com.desktop.lumi.home.presentation.InteractionViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AndroidInteractionViewModel @Inject constructor(
    interactionRepository: InteractionRepository
) : ViewModel() {
    val viewModel = InteractionViewModel(interactionRepository)
}

