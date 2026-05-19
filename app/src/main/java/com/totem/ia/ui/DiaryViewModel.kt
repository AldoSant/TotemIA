package com.totem.ia.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.totem.ia.data.local.ReflectionDao
import com.totem.ia.data.local.ReflectionEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val reflectionDao: ReflectionDao
) : ViewModel() {

    private val _reflections = MutableStateFlow<List<ReflectionEntity>>(emptyList())
    val reflections: StateFlow<List<ReflectionEntity>> = _reflections.asStateFlow()

    init {
        viewModelScope.launch {
            reflectionDao.getAllReflections().collect { list ->
                _reflections.value = list
            }
        }
    }
}
