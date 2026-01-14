package com.example.academictycoon.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.academictycoon.data.local.dao.QuestionDao
import com.example.academictycoon.data.local.model.Question
import com.example.academictycoon.data.repository.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MiningViewModel @Inject constructor(
    private val questionDao: QuestionDao,
    private val syncRepository: SyncRepository
) : ViewModel() {

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            var questionsFromDb = questionDao.getAllQuestions()
            if (questionsFromDb.isEmpty()) {
                // Using the repository from the project plan. The user should replace with the actual URL.
                syncRepository.syncQuestions("https://raw.githubusercontent.com/user/repo/main/bundle.json")
                questionsFromDb = questionDao.getAllQuestions()
            }
            _questions.value = questionsFromDb
        }
    }
}
