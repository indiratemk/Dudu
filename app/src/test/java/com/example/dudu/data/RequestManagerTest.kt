package com.example.dudu.data

import com.example.dudu.data.errors.ErrorType
import com.example.dudu.data.errors.RequestException
import com.example.dudu.data.helpers.RequestManager
import com.example.dudu.data.helpers.Resource
import com.example.dudu.data.models.Task
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class RequestManagerTest {

    @MockK
    lateinit var onBeforeRequest: suspend () -> Unit
    lateinit var onAfterRequest: suspend () -> Unit
    lateinit var onRevertOptimistic: suspend () -> Unit
    lateinit var onSaveRequest: suspend () -> Task
    lateinit var onSynchronization: suspend () -> Unit
    lateinit var requestManager: RequestManager

    @Before
    fun setUp() {
        onBeforeRequest = mockk(relaxed = true)
        onAfterRequest = mockk(relaxed = true)
        onRevertOptimistic = mockk(relaxed = true)
        onSaveRequest = mockk(relaxed = true)
        onSynchronization = mockk(relaxed = true)
        requestManager = RequestManager()
    }

    @Test
    fun `executeRequest check operations order when success request`() {
        runBlockingTest {
            val task = Task("test", "test", 0L, "basic", false)
            val makeRequest: suspend () -> Task = { task }

            requestManager.executeRequest(
                onBeforeRequest = onBeforeRequest,
                makeRequest = makeRequest,
                onAfterRequest = onAfterRequest,
                onRevertOptimisticUpdate = onRevertOptimistic,
                onSaveRequest = onSaveRequest,
                onSynchronization = onSynchronization
            )

            coVerifyOrder {
                onBeforeRequest()
                makeRequest()
                onAfterRequest()
            }
            coVerify(exactly = 0) { onRevertOptimistic() }
            coVerify(exactly = 0) { onSynchronization() }
            coVerify(exactly = 0) { onSaveRequest() }
        }
    }

    @Test
    fun `executeRequest check operations order when not found request error`() {
        runBlockingTest {
            val exception = RequestException(ErrorType.NOT_FOUND)
            val makeRequest: suspend () -> Task = { throw exception }

            requestManager.executeRequest(
                onBeforeRequest = onBeforeRequest,
                makeRequest = makeRequest,
                onAfterRequest = onAfterRequest,
                onRevertOptimisticUpdate = onRevertOptimistic,
                onSaveRequest = onSaveRequest,
                onSynchronization = onSynchronization
            )

            coVerifyOrder {
                onBeforeRequest()
                onSynchronization()
            }
            coVerify(exactly = 0) { onAfterRequest() }
            coVerify(exactly = 0) { onRevertOptimistic() }
            coVerify(exactly = 0) { onSaveRequest() }
        }
    }

    @Test
    fun `executeRequest check operations order when unknown request error`() {
        runBlockingTest {
            val exception = RequestException(ErrorType.UNKNOWN)
            val makeRequest: suspend () -> Task = { throw exception }

            requestManager.executeRequest(
                onBeforeRequest = onBeforeRequest,
                makeRequest = makeRequest,
                onAfterRequest = onAfterRequest,
                onRevertOptimisticUpdate = onRevertOptimistic,
                onSaveRequest = onSaveRequest,
                onSynchronization = onSynchronization
            )

            coVerifyOrder {
                onBeforeRequest()
                onRevertOptimistic()
            }
            coVerify(exactly = 0) { onAfterRequest() }
            coVerify(exactly = 0) { onSynchronization() }
            coVerify(exactly = 0) { onSaveRequest() }
        }
    }

    @Test
    fun `executeRequest check operations order when timeout request error`() {
        runBlockingTest {
            val exception = RequestException(ErrorType.TIMEOUT)
            val makeRequest: suspend () -> Task = { throw exception }

            requestManager.executeRequest(
                onBeforeRequest = onBeforeRequest,
                makeRequest = makeRequest,
                onAfterRequest = onAfterRequest,
                onRevertOptimisticUpdate = onRevertOptimistic,
                onSaveRequest = onSaveRequest,
                onSynchronization = onSynchronization
            )

            coVerifyOrder {
                onBeforeRequest()
                onRevertOptimistic()
            }
            coVerify(exactly = 0) { onAfterRequest() }
            coVerify(exactly = 0) { onSynchronization() }
            coVerify(exactly = 0) { onSaveRequest() }
        }
    }

    @Test
    fun `executeRequest check operations order when server request error`() {
        runBlockingTest {
            val exception = RequestException(ErrorType.SERVER)
            val makeRequest: suspend () -> Task = { throw exception }

            requestManager.executeRequest(
                onBeforeRequest = onBeforeRequest,
                makeRequest = makeRequest,
                onAfterRequest = onAfterRequest,
                onRevertOptimisticUpdate = onRevertOptimistic,
                onSaveRequest = onSaveRequest,
                onSynchronization = onSynchronization
            )

            coVerifyOrder {
                onBeforeRequest()
                onAfterRequest()
                onSaveRequest()
            }
            coVerify(exactly = 0) { onRevertOptimistic() }
            coVerify(exactly = 0) { onSynchronization() }
        }
    }

    @Test
    fun `executeRequest check operations order when connection request error`() {
        runBlockingTest {
            val exception = RequestException(ErrorType.CONNECTION)
            val makeRequest: suspend () -> Task = { throw exception }

            requestManager.executeRequest(
                onBeforeRequest = onBeforeRequest,
                makeRequest = makeRequest,
                onAfterRequest = onAfterRequest,
                onRevertOptimisticUpdate = onRevertOptimistic,
                onSaveRequest = onSaveRequest,
                onSynchronization = onSynchronization
            )

            coVerifyOrder {
                onBeforeRequest()
                onAfterRequest()
                onSaveRequest()
            }
            coVerify(exactly = 0) { onRevertOptimistic() }
            coVerify(exactly = 0) { onSynchronization() }
        }
    }

    @Test
    fun `executeRequest should return resource loaded status when success request`() {
        runBlockingTest {
            val task = Task("test", "test", 0L, "basic", false)
            val makeRequest: suspend () -> Task = { task }

            val result = requestManager.executeRequest(
                onBeforeRequest = onBeforeRequest,
                makeRequest = makeRequest,
                onAfterRequest = onAfterRequest,
                onRevertOptimisticUpdate = onRevertOptimistic,
                onSaveRequest = onSaveRequest,
                onSynchronization = onSynchronization
            )

            assertThat(result, `is`(Resource.Loaded(task)))
        }
    }

    @Test
    fun `executeRequest should return resource error status when not found error request`() {
        runBlockingTest {
            val exception = RequestException(ErrorType.NOT_FOUND)
            val makeRequest: suspend () -> Task = { throw exception }

            val result = requestManager.executeRequest(
                onBeforeRequest = onBeforeRequest,
                makeRequest = makeRequest,
                onAfterRequest = onAfterRequest,
                onRevertOptimisticUpdate = onRevertOptimistic,
                onSaveRequest = onSaveRequest,
                onSynchronization = onSynchronization
            )

            assertThat(result, `is`(Resource.Error(exception.matchMessage())))
        }
    }

    @Test
    fun `executeRequest should return resource error status when unknown error request`() {
        runBlockingTest {
            val exception = RequestException(ErrorType.UNKNOWN)
            val makeRequest: suspend () -> Task = { throw exception }

            val result = requestManager.executeRequest(
                onBeforeRequest = onBeforeRequest,
                makeRequest = makeRequest,
                onAfterRequest = onAfterRequest,
                onRevertOptimisticUpdate = onRevertOptimistic,
                onSaveRequest = onSaveRequest,
                onSynchronization = onSynchronization
            )

            assertThat(result, `is`(Resource.Error(exception.matchMessage())))
        }
    }

    @Test
    fun `executeRequest should return resource error status when timeout error request`() {
        runBlockingTest {
            val exception = RequestException(ErrorType.TIMEOUT)
            val makeRequest: suspend () -> Task = { throw exception }

            val result = requestManager.executeRequest(
                onBeforeRequest = onBeforeRequest,
                makeRequest = makeRequest,
                onAfterRequest = onAfterRequest,
                onRevertOptimisticUpdate = onRevertOptimistic,
                onSaveRequest = onSaveRequest,
                onSynchronization = onSynchronization
            )

            assertThat(result, `is`(Resource.Error(exception.matchMessage())))
        }
    }

    @Test
    fun `executeRequest should return resource loaded status when server error request`() {
        runBlockingTest {
            val exception = RequestException(ErrorType.SERVER)
            val makeRequest: suspend () -> Task = { throw exception }
            val task = Task("test", "test", 0L, "basic", false)
            coEvery { onSaveRequest() }.returns(task)

            val result = requestManager.executeRequest(
                onBeforeRequest = onBeforeRequest,
                makeRequest = makeRequest,
                onAfterRequest = onAfterRequest,
                onRevertOptimisticUpdate = onRevertOptimistic,
                onSaveRequest = onSaveRequest,
                onSynchronization = onSynchronization
            )

            assertThat(result, `is`(Resource.Loaded(task)))
        }
    }

    @Test
    fun `executeRequest should return resource loaded status when connection error request`() {
        runBlockingTest {
            val exception = RequestException(ErrorType.CONNECTION)
            val makeRequest: suspend () -> Task = { throw exception }
            val task = Task("test", "test", 0L, "basic", false)
            coEvery { onSaveRequest() }.returns(task)

            val result = requestManager.executeRequest(
                onBeforeRequest = onBeforeRequest,
                makeRequest = makeRequest,
                onAfterRequest = onAfterRequest,
                onRevertOptimisticUpdate = onRevertOptimistic,
                onSaveRequest = onSaveRequest,
                onSynchronization = onSynchronization
            )

            assertThat(result, `is`(Resource.Loaded(task)))
        }
    }

    @Test
    fun `executeRequest should return resource loaded status and synchronize when success request`() {
        runBlockingTest {
            val task = Task("test", "test", 0L, "basic", false)
            val makeRequest: suspend () -> Task = { task }
            val onSynchronizeNewData: suspend (Task) -> Unit = mockk(relaxed = true)

            val result = requestManager.executeRequest(
                makeRequest = makeRequest,
                onSynchronization = onSynchronizeNewData
            )

            assertThat(result, `is`(Resource.Loaded(task)))
            coVerify(exactly = 1) { onSynchronizeNewData(task) }
        }
    }

    @Test
    fun `executeRequest should return resource error status when throw request exception`() {
        runBlockingTest {
            val exception = RequestException(ErrorType.NOT_FOUND)
            val makeRequest: suspend () -> Task = { throw exception }
            val onSynchronizeNewData: suspend (Task) -> Unit = mockk(relaxed = true)

            val result = requestManager.executeRequest(
                makeRequest = makeRequest,
                onSynchronization = onSynchronizeNewData
            )

            assertThat(result, `is`(Resource.Error(exception.matchMessage())))
        }
    }
}