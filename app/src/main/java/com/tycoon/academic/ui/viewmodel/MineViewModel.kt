package com.tycoon.academic.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tycoon.academic.data.local.model.Question
import com.tycoon.academic.data.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MineViewModel @Inject constructor(
    private val questionRepository: QuestionRepository
) : ViewModel() {

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    init {
        loadQuestions(true) // Initially load from network
    }

    fun loadQuestions(isOnline: Boolean) {
        viewModelScope.launch {
            _questions.value = questionRepository.getQuestions(isOnline)
            _currentQuestionIndex.value = 0
        }
    }

    @Suppress("unused")
    fun loadQuestionsFromUrl(url: String) {
        viewModelScope.launch {
            _questions.value = questionRepository.getQuestions(true, url)
            _currentQuestionIndex.value = 0
        }
    }

    fun nextQuestion() {
        if (_currentQuestionIndex.value < _questions.value.size - 1) {
            _currentQuestionIndex.value++
        } else {
            // Handle case where there are no more questions, e.g., show a message or reload
            // For now, let's just loop back to the start
            _currentQuestionIndex.value = 0
        }
    }
}
