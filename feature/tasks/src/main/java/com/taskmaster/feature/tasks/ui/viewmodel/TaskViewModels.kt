/**
 * @author Saad Khan
 * @date January 2025
 */
package com.taskmaster.feature.tasks.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmaster.core.common.Result
import com.taskmaster.core.data.local.TokenManager
import com.taskmaster.core.domain.model.Task
import com.taskmaster.core.domain.model.TaskPriority
import com.taskmaster.core.domain.model.TaskStatus
import com.taskmaster.core.domain.usecase.AuthUseCases
import com.taskmaster.core.domain.usecase.TaskUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TaskCompletionFilter {
    ALL, PENDING, COMPLETED
}

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases,
    private val authUseCases: AuthUseCases,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _allTasks = MutableStateFlow<List<Task>>(emptyList())
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks = _tasks.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _priorityFilter = MutableStateFlow<TaskPriority?>(null)
    val priorityFilter = _priorityFilter.asStateFlow()

    private val _completionFilter = MutableStateFlow(TaskCompletionFilter.ALL)
    val completionFilter = _completionFilter.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadTasks()
        viewModelScope.launch {
            combine(_allTasks, _searchQuery, _priorityFilter, _completionFilter) { tasks, query, priority, completion ->
                tasks.filter { task ->
                    val matchesQuery = query.isBlank() ||
                        task.title.contains(query, ignoreCase = true) ||
                        task.description?.contains(query, ignoreCase = true) == true
                    val matchesPriority = priority == null || task.priority == priority
                    val matchesCompletion = when (completion) {
                        TaskCompletionFilter.ALL -> true
                        TaskCompletionFilter.PENDING -> !task.isCompleted
                        TaskCompletionFilter.COMPLETED -> task.isCompleted
                    }
                    matchesQuery && matchesPriority && matchesCompletion
                }
            }.collect { filtered ->
                _tasks.value = filtered
            }
        }
    }

    private fun loadTasks() {
        if (tokenManager.getUserId().isNullOrBlank()) {
            _error.value = "Not logged in"
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            taskUseCases.getTasks()
        }
        viewModelScope.launch {
            taskUseCases.observeUserTasks().collectLatest { tasks ->
                _allTasks.value = tasks
                _isLoading.value = false
                _error.value = null
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onPriorityFilterChange(priority: TaskPriority?) {
        _priorityFilter.value = priority
    }

    fun onCompletionFilterChange(filter: TaskCompletionFilter) {
        _completionFilter.value = filter
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _priorityFilter.value = null
        _completionFilter.value = TaskCompletionFilter.ALL
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(
                isCompleted = !task.isCompleted,
                status = if (!task.isCompleted) TaskStatus.COMPLETED else TaskStatus.PENDING
            )
            taskUseCases.updateTask(updatedTask)
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authUseCases.logoutUser()
            onLoggedOut()
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

    private val _taskDeleted = MutableSharedFlow<Boolean>()
    val taskDeleted = _taskDeleted.asSharedFlow()

    fun loadTask(taskId: Int) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            taskUseCases.observeTaskById(taskId).collectLatest { task ->
                _task.value = task
                _isLoading.value = false
                if (task == null) {
                    _error.value = "Task not found"
                } else {
                    _error.value = null
                }
            }
        }
    }

    fun toggleCompletion() {
        val currentTask = _task.value ?: return
        viewModelScope.launch {
            val updatedTask = currentTask.copy(
                isCompleted = !currentTask.isCompleted,
                status = if (!currentTask.isCompleted) TaskStatus.COMPLETED else TaskStatus.PENDING
            )
            when (val result = taskUseCases.updateTask(updatedTask)) {
                is Result.Error -> _error.value = result.message
                else -> _error.value = null
            }
        }
    }

    fun deleteTask(taskId: Int) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            when (val result = taskUseCases.deleteTask(taskId.toString())) {
                is Result.Success -> _taskDeleted.emit(true)
                is Result.Error -> _error.value = result.message
                is Result.Loading -> Unit
            }
            _isLoading.value = false
        }
    }
}

@HiltViewModel
class CreateEditTaskViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases,
    private val tokenManager: TokenManager
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
                taskUseCases.observeTaskById(taskId).collectLatest { task ->
                    task?.let {
                        _title.value = it.title
                        _description.value = it.description
                        _dueDate.value = it.dueDate
                        _isCompleted.value = it.isCompleted
                    }
                    _isLoading.value = false
                    _error.value = null
                }
            }
        } else {
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

        val userId = tokenManager.getUserId()
        if (userId.isNullOrBlank()) {
            _error.value = "Not logged in"
            return
        }

        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            val task = Task(
                taskId = _taskId.value ?: 0,
                userId = userId,
                title = title.value,
                description = description.value,
                dueDate = dueDate.value,
                isCompleted = isCompleted.value
            )
            when (val result = taskUseCases.saveTask(task)) {
                is Result.Success -> _taskSaved.emit(true)
                is Result.Error -> _error.value = result.message
                is Result.Loading -> Unit
            }
            _isLoading.value = false
        }
    }
}
