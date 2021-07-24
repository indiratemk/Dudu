package com.example.dudu.ui.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.dudu.data.TasksRepository
import com.example.dudu.data.helpers.Resource
import com.example.dudu.data.models.Task
import com.example.dudu.rules.MainCoroutineRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class TasksViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    @MockK
    lateinit var mockRepository: TasksRepository
    @MockK
    lateinit var mockUpdateTaskObserver: Observer<Resource<Task>>
    @MockK
    lateinit var mockRemoveTaskObserver: Observer<Resource<Task>>
    @MockK
    lateinit var mockSyncTasksObserver: Observer<Resource<List<Task>>>
    @MockK
    lateinit var mockShowDoneObserver: Observer<Boolean>

    lateinit var viewModel: TasksViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        viewModel = TasksViewModel(mockRepository)
        viewModel.updateTask.observeForever(mockUpdateTaskObserver)
        viewModel.removeTask.observeForever(mockRemoveTaskObserver)
        viewModel.syncTasks.observeForever(mockSyncTasksObserver)
        viewModel.showDone.observeForever(mockShowDoneObserver)
    }

    private fun getTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        for (i in 1..10) {
            val task = Task("test$i", "test",
                0L, "basic", i % 2 == 0)
            tasks.add(task)
        }
        return tasks
    }

    @Test
    fun `onTaskChanged should set resource loaded status when successful update task`() {
        mainCoroutineRule.runBlockingTest {
            val task = Task("test", "test", 0L, "basic", true)
            val updatedTask = Task("test", "test", 0L,
                "basic", false)
            val resource = Resource.Loaded(updatedTask)
            coEvery { mockRepository.updateTask(updatedTask) }.returns(resource)

            viewModel.onTaskCheckedChanged(task, false)

            verify { mockUpdateTaskObserver.onChanged(resource) }
            confirmVerified(mockUpdateTaskObserver)
        }
    }

    @Test
    fun `onTaskChanged should set resource error status when failed update task`() {
        mainCoroutineRule.runBlockingTest {
            val task = Task("test", "test", 0L, "basic", true)
            val updatedTask = Task("test", "test", 0L,
                "basic", false)
            val resource = Resource.Error("test error")
            coEvery { mockRepository.updateTask(updatedTask) }.returns(resource)

            viewModel.onTaskCheckedChanged(task, false)

            verify { mockUpdateTaskObserver.onChanged(resource) }
            confirmVerified(mockUpdateTaskObserver)
        }
    }

    @Test
    fun `removeTask should set resource loaded status when successful remove task`() {
        val task = Task("test", "test", 0L, "basic", true)
        val resource = Resource.Loaded(task)
        coEvery { mockRepository.removeTask(task) }.returns(resource)

        viewModel.removeTask(task)

        verify { mockRemoveTaskObserver.onChanged(resource) }
        confirmVerified(mockRemoveTaskObserver)
    }

    @Test
    fun `removeTask should set resource error status when failed remove task`() {
        val task = Task("test", "test", 0L, "basic", true)
        val resource = Resource.Error("test error")
        coEvery { mockRepository.removeTask(task) }.returns(resource)

        viewModel.removeTask(task)

        verify { mockRemoveTaskObserver.onChanged(resource) }
        confirmVerified(mockRemoveTaskObserver)
    }

    @Test
    fun `synchronizeTasks should do nothing when no tasks to synchronize`() {
        coEvery { mockRepository.shouldSynchronizeTasks() }.returns(false)

        viewModel.synchronizeTasks()

        confirmVerified(mockSyncTasksObserver)
    }

    @Test
    fun `synchronizeTasks should set resource loaded when need sync and successful tasks sync`() {
        val resource = Resource.Loaded(getTasks())
        coEvery { mockRepository.synchronizeTasks() }.returns(resource)
        coEvery { mockRepository.shouldSynchronizeTasks() }.returns(true)

        viewModel.synchronizeTasks()

        verify { mockSyncTasksObserver.onChanged(Resource.Loading) }
        verify { mockSyncTasksObserver.onChanged(resource) }
        confirmVerified(mockSyncTasksObserver)
    }

    @Test
    fun `synchronizeTasks should set resource error when need sync and failed tasks sync`() {
        val resource = Resource.Error("test error")
        coEvery { mockRepository.synchronizeTasks() }.returns(resource)
        coEvery { mockRepository.shouldSynchronizeTasks() }.returns(true)

        viewModel.synchronizeTasks()

        verify { mockSyncTasksObserver.onChanged(Resource.Loading) }
        verify { mockSyncTasksObserver.onChanged(resource) }
        confirmVerified(mockSyncTasksObserver)
    }
}