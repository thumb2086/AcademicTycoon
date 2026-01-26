package com.tycoon.academic.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tycoon.academic.data.local.model.Question
import com.tycoon.academic.data.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MiningViewModel @Inject constructor(
    private val questionRepository: QuestionRepository
) : ViewModel() {

    // 監聽資料庫中的所有題目
    val allQuestions: StateFlow<List<Question>> = questionRepository.getAllQuestionsFlow()
        .onEach { Log.d("MiningViewModel", "Database updated, total questions: ${it.size}") }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 選中的科目
    private val _selectedSubject = MutableStateFlow<String?>(null)
    val selectedSubject = _selectedSubject.asStateFlow()

    // 目前題目索引
    private val _currentIndex = MutableStateFlow(0)
    val currentIndex = _currentIndex.asStateFlow()

    // 動態科目列表
    val subjects: StateFlow<List<String>> = allQuestions
        .map { list -> list.map { it.subject }.distinct() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 篩選後的題目列表
    val filteredQuestions: StateFlow<List<Question>> = combine(allQuestions, _selectedSubject) { all, selected ->
        if (selected == null) all else all.filter { it.subject == selected }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun filterBySubject(subject: String?) {
        Log.d("MiningViewModel", "Filtering by subject: $subject")
        _selectedSubject.value = subject
        _currentIndex.value = 0
    }

    fun nextQuestion() {
        val currentSize = filteredQuestions.value.size
        if (currentSize > 0) {
            _currentIndex.value = (_currentIndex.value + 1) % currentSize
        }
    }

    fun loadQuestions() {
        // 重置索引
        _currentIndex.value = 0
    }
}
