package com.example.dudu.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.dudu.rules.MainCoroutineRule
import com.example.dudu.data.TasksRepository
import com.example.dudu.data.helpers.Resource
import com.example.dudu.data.models.Task
import com.example.dudu.ui.task.CreateTaskViewModel
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
class CreateTaskViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    @MockK
    lateinit var mockRepository: TasksRepository
    @MockK
    lateinit var mockTaskObserver: Observer<Resource<Task>>

    lateinit var viewModel: CreateTaskViewModel
    lateinit var task: Task


    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        task = Task("test", "test", 0L, "basic", false)
        viewModel = CreateTaskViewModel(mockRepository)
        viewModel.task.observeForever(mockTaskObserver)
    }

    @Test
    fun `createTask should set resource loaded status when repository successful add task`() {
        mainCoroutineRule.runBlockingTest {
            val resource = Resource.Loaded(task)
            coEvery { mockRepository.addTask(task) }.returns(resource)

            viewModel.createTask(task)

            verify { mockTaskObserver.onChanged(Resource.Loading) }
            verify { mockTaskObserver.onChanged(resource) }
            confirmVerified(mockTaskObserver)
        }
    }

    @Test
    fun `createTask should set resource error status when repository failed add task`() {
        mainCoroutineRule.runBlockingTest {
            val resource = Resource.Error("test error")
            coEvery { mockRepository.addTask(task) }.returns(resource)

            viewModel.createTask(task)

            verify { mockTaskObserver.onChanged(Resource.Loading) }
            verify { mockTaskObserver.onChanged(resource) }
            confirmVerified(mockTaskObserver)
        }
    }

    @Test
    fun `updateTask should set resource loaded status when repository successful update task`() {
        mainCoroutineRule.runBlockingTest {
            val resource = Resource.Loaded(task)
            coEvery { mockRepository.updateTask(task) }.returns(resource)

            viewModel.updateTask(task)

            verify { mockTaskObserver.onChanged(Resource.Loading) }
            verify { mockTaskObserver.onChanged(resource) }
            confirmVerified(mockTaskObserver)
        }
    }

    @Test
    fun `updateTask should set resource error status when repository failed update task`() {
        mainCoroutineRule.runBlockingTest {
            val resource = Resource.Error("test error")
            coEvery { mockRepository.updateTask(task) }.returns(resource)

            viewModel.updateTask(task)

            verify { mockTaskObserver.onChanged(Resource.Loading) }
            verify { mockTaskObserver.onChanged(resource) }
            confirmVerified(mockTaskObserver)
        }
    }

    @Test
    fun `removeTask should set resource loaded status when repository successful remove task`() {
        mainCoroutineRule.runBlockingTest {
            val resource = Resource.Loaded(task)
            coEvery { mockRepository.removeTask(task) }.returns(resource)

            viewModel.removeTask(task)

            verify { mockTaskObserver.onChanged(Resource.Loading) }
            verify { mockTaskObserver.onChanged(resource) }
            confirmVerified(mockTaskObserver)
        }
    }

    @Test
    fun `removeTask should set resource error status when repository failed remove task`() {
        mainCoroutineRule.runBlockingTest {
            val resource = Resource.Error("test error")
            coEvery { mockRepository.removeTask(task) }.returns(resource)

            viewModel.removeTask(task)

            verify { mockTaskObserver.onChanged(Resource.Loading) }
            verify { mockTaskObserver.onChanged(resource) }
            confirmVerified(mockTaskObserver)
        }
    }
}