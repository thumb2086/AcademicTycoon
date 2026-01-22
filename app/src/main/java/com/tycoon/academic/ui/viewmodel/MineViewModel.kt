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

    // 改名：專門負責從本地資料庫讀取
    fun loadLocalQuestions() {
        viewModelScope.launch {
            // 修正：使用新的 getAllQuestions 方法
            _questions.value = questionRepository.getAllQuestions()
            // 如果列表不為空，重置索引，否則保持 0
            _currentQuestionIndex.value = 0
        }
    }

    // 負責從網路下載指定科目的題目
    fun loadQuestionsFromUrl(url: String) {
        viewModelScope.launch {
            // 1. 先下載並同步到資料庫 (使用新的 syncQuestions 方法)
            questionRepository.syncQuestions(url)

            // 2. 下載完成後，從資料庫重新讀取最新資料
            _questions.value = questionRepository.getAllQuestions()
            _currentQuestionIndex.value = 0
        }
    }

    fun nextQuestion() {
        if (_questions.value.isNotEmpty() && _currentQuestionIndex.value < _questions.value.size - 1) {
            _currentQuestionIndex.value++
        } else {
            // 循環回到第一題，或者你可以在這裡處理結束邏輯
            _currentQuestionIndex.value = 0
        }
    }
}