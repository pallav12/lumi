package com.desktop.lumi.viewmodel

import androidx.lifecycle.ViewModel
import com.desktop.lumi.domain.repository.InsightsRepository
import com.desktop.lumi.insights.InsightsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AndroidInsightsViewModel @Inject constructor(
    insightsRepository: InsightsRepository
) : ViewModel() {
    val viewModel = InsightsViewModel(insightsRepository)
}

