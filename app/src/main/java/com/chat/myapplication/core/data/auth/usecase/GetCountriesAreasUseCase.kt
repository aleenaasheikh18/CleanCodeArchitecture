package com.chat.myapplication.core.data.auth.usecase

import com.chat.myapplication.core.data.auth.model.CountriesAreasResponse
import com.chat.myapplication.core.data.auth.remote.AuthRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class GetCountriesAreasUseCase @Inject constructor(private val authRepository: AuthRepository) {

    operator fun invoke(): Flow<CountriesAreasResponse> {
        return authRepository.getCountriesAreas()
    }
}
