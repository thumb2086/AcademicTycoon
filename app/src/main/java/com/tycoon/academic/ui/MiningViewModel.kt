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
class MiningViewModel @Inject constructor(
    private val questionRepository: QuestionRepository
) : ViewModel() {

    // 這是給 UI 觀察用的 StateFlow
    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions.asStateFlow()

    // 這是給 MiningScreen 顯示進度條用的 (如果有用到的話)
    data class MiningUiState(val progress: Int = 0)
    private val _uiState = MutableStateFlow(MiningUiState())
    val uiState: StateFlow<MiningUiState> = _uiState.asStateFlow()

    init {
        // 預設載入一次
        loadQuestions()
    }

    // 這是舊的無參數方法
    fun loadQuestions() {
        viewModelScope.launch {
            _questions.value = questionRepository.getAllQuestions()
        }
    }

    // 這是 MiningScreen 需要的新方法：根據 URL 下載題目
    fun loadQuestionsFromUrl(url: String) {
        viewModelScope.launch {
            try {
                // 這裡呼叫 Repository 去同步資料
                questionRepository.syncQuestions(url)
                // 同步完後更新列表
                _questions.value = questionRepository.getAllQuestions()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 如果你有用到 startMining
    fun startMining() {
        // 模擬挖掘邏輯
    }
}