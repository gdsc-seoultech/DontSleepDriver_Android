package com.comye1.dontsleepdriver.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comye1.dontsleepdriver.data.model.LocalUser
import com.comye1.dontsleepdriver.repository.DSDRepository
import com.comye1.dontsleepdriver.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: DSDRepository
) : ViewModel() {

    private val _user: MutableStateFlow<LocalUser?> = MutableStateFlow(null)
    val user: StateFlow<LocalUser?> = _user

    init {
        viewModelScope.launch {
            repository.getUser().also {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let { data ->
                            _user.value =
                                LocalUser(
                                    email = data.data!!.email,
                                    id = data.data!!.id ?: -1
                                )
                        }

                    }
                    is Resource.Error -> {
                        _user.value =
                            LocalUser(
                                email = "no email",
                                id = -1
                            )
                    }
                }
            }
        }
    }
}