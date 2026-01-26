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
        loadLocalQuestions()
    }

    /**
     * 從本地資料庫讀取題目。
     * 同步邏輯已移至啟動時的 SyncRepository，這裡只負責呈現。
     */
    fun loadLocalQuestions() {
        viewModelScope.launch {
            _questions.value = questionRepository.getAllQuestions()
            _currentQuestionIndex.value = 0
        }
    }

    // 移除 loadQuestionsFromUrl 方法，因為題目同步現在由 SyncRepository 統一管控

    fun nextQuestion() {
        if (_questions.value.isNotEmpty() && _currentQuestionIndex.value < _questions.value.size - 1) {
            _currentQuestionIndex.value++
        } else {
            _currentQuestionIndex.value = 0
        }
    }
}
