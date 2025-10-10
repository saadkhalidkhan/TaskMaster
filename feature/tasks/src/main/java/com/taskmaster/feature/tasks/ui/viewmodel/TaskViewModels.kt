package com.taskmaster.feature.tasks.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmaster.core.common.Result
import com.taskmaster.core.domain.model.Task
import com.taskmaster.core.domain.usecase.TaskUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks = _tasks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            taskUseCases.getTasksForUser("current_user_id") // TODO: Replace with actual user ID
                .collectLatest { result ->
                    when (result) {
                        is Result.Loading -> _isLoading.value = true
                        is Result.Success -> {
                            _tasks.value = result.data ?: emptyList()
                            _isLoading.value = false
                            _error.value = null
                        }
                        is Result.Error -> {
                            _error.value = result.message
                            _isLoading.value = false
                        }
                    }
                }
        }
    }
}

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases
) : ViewModel() {

    private val _task = MutableStateFlow<Task?>(null)
    val task = _task.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadTask(taskId: Int) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            taskUseCases.getTaskById(taskId).collectLatest { result ->
                when (result) {
                    is Result.Loading -> _isLoading.value = true
                    is Result.Success -> {
                        _task.value = result.data
                        _isLoading.value = false
                        _error.value = null
                    }
                    is Result.Error -> {
                        _error.value = result.message
                        _isLoading.value = false
                    }
                }
            }
        }
    }
}

@HiltViewModel
class CreateEditTaskViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases
) : ViewModel() {

    private val _taskId = MutableStateFlow<Int?>(null)
    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _description = MutableStateFlow<String?>(null)
    val description = _description.asStateFlow()

    private val _dueDate = MutableStateFlow<Long?>(null)
    val dueDate = _dueDate.asStateFlow()

    private val _isCompleted = MutableStateFlow(false)
    val isCompleted = _isCompleted.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _taskSaved = MutableSharedFlow<Boolean>()
    val taskSaved = _taskSaved.asSharedFlow()

    fun loadTask(taskId: Int?) {
        _taskId.value = taskId
        if (taskId != null) {
            _isLoading.value = true
            viewModelScope.launch {
                taskUseCases.getTaskById(taskId).collectLatest { result ->
                    when (result) {
                        is Result.Loading -> _isLoading.value = true
                        is Result.Success -> {
                            result.data?.let { task ->
                                _title.value = task.title
                                _description.value = task.description
                                _dueDate.value = task.dueDate
                                _isCompleted.value = task.isCompleted
                            }
                            _isLoading.value = false
                            _error.value = null
                        }
                        is Result.Error -> {
                            _error.value = result.message
                            _isLoading.value = false
                        }
                    }
                }
            }
        } else {
            // Reset for new task
            _title.value = ""
            _description.value = null
            _dueDate.value = null
            _isCompleted.value = false
        }
    }

    fun onTitleChange(newTitle: String) {
        _title.value = newTitle
        _error.value = null
    }

    fun onDescriptionChange(newDescription: String) {
        _description.value = newDescription
        _error.value = null
    }

    fun onDueDateChange(newDueDate: Long?) {
        _dueDate.value = newDueDate
        _error.value = null
    }

    fun onIsCompletedChange(newIsCompleted: Boolean) {
        _isCompleted.value = newIsCompleted
        _error.value = null
    }

    fun saveTask() {
        if (title.value.isBlank()) {
            _error.value = "Title cannot be empty"
            return
        }

        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            val task = Task(
                taskId = _taskId.value ?: 0, // 0 for new task, Room will auto-generate
                userId = "current_user_id", // TODO: Replace with actual user ID
                title = title.value,
                description = description.value,
                dueDate = dueDate.value,
                isCompleted = isCompleted.value
            )
            when (val result = taskUseCases.saveTask(task)) {
                is Result.Success -> {
                    _taskSaved.emit(true)
                }
                is Result.Error -> {
                    _error.value = result.message
                }
                is Result.Loading -> {
                    // Handle loading state if needed
                }
            }
            _isLoading.value = false
        }
    }
}