package com.chat.myapplication.ui.fragments.settings.legalStuff.utility

import androidx.lifecycle.viewModelScope
import com.chat.myapplication.base.BaseViewModel
import com.chat.myapplication.core.data.settings.model.UtilityResponse
import com.chat.myapplication.core.data.settings.usecase.UtilityUseCase
import com.chat.myapplication.core.domain.State
import com.chat.myapplication.core.domain.onApiError
import com.chat.myapplication.core.domain.onApiSuccess
import com.chat.myapplication.di.IODispatcher
import com.chat.myapplication.utility.collectAsResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class LegalStuffViewModel @Inject constructor(
    private val utilityUseCase: UtilityUseCase,
    @param:IODispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    private val _utilityResponse = MutableStateFlow<State<UtilityResponse>>(State.idle())
    val utilityResponse: StateFlow<State<UtilityResponse>> = _utilityResponse

    private var isDataLoaded = false

    fun getPolicyDocuments(forceRefresh: Boolean = false) {
        if (isDataLoaded && !forceRefresh) return

        utilityUseCase().collectAsResult()
            .flowOn(ioDispatcher)
            .onApiSuccess { response ->
                if (response.status) {
                    isDataLoaded = true
                    _utilityResponse.value = State.Success(response)
                } else {
                    _utilityResponse.value = State.Error(response.message)
                }
            }
            .onApiError { error ->
                _utilityResponse.value = State.Error(error.errorMessage)
            }
            .onStart {
                _utilityResponse.value = State.Loading()
            }
            .launchIn(viewModelScope)
    }
}