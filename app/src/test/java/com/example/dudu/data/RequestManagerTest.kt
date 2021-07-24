package com.example.dudu.data

import com.example.dudu.data.errors.ErrorType
import com.example.dudu.data.errors.RequestException
import com.example.dudu.data.helpers.RequestManager
import com.example.dudu.data.helpers.Resource
import com.example.dudu.data.models.Task
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class RequestManagerTest {

    lateinit var onMockBeforeRequest: suspend () -> Unit
    lateinit var onMockAfterRequest: suspend () -> Unit
    lateinit var onMockRevertOptimistic: suspend () -> Unit
    lateinit var onMockSaveRequest: suspend () -> Task
    lateinit var onMockSynchronization: suspend () -> Unit
    lateinit var requestManager: RequestManager

    @Before
    fun setUp() {
        onMockBeforeRequest = mockk(relaxed = true)
        onMockAfterRequest = mockk(relaxed = true)
        onMockRevertOptimistic = mockk(relaxed = true)
        onMockSaveRequest = mockk(relaxed = true)
        onMockSynchronization = mockk(relaxed = true)
        requestManager = RequestManager()
    }

    @Test
    fun `executeRequest check operations order when success request`() {
        runBlockingTest {
            val task = Task("test", "test", 0L, "basic", false)
            val makeRequest: suspend () -> Task = { task }

            requestManager.executeRequest(
                onBeforeRequest = onMockBeforeRequest,
                makeRequest = makeRequest,
                onAfterRequest = onMockAfterRequest,
                onRevertOptimisticUpdate = onMockRevertOptimistic,
                onSaveRequest = onMockSaveRequest,
                onSynchronization = onMockSynchronization
            )

            coVerifyOrder {
                onMockBeforeRequest()
                makeRequest()
                onMockAfterRequest()
            }
            coVerify(exactly = 0) { onMockRevertOptimistic() }
            coVerify(exactly = 0) { onMockSynchronization() }
            coVerify(exactly = 0) { onMockSaveRequest() }
        }
    }

    @Test
    fun `executeRequest check operations order when not found request error`() {
        runBlockingTest {
            val exception = RequestException(ErrorType.NOT_FOUND)
            val makeRequest: suspend () -> Task = { throw exception }

            requestManager.executeRequest(
                onBeforeRequest = onMockBeforeRequest,
                makeRequest = makeRequest,
                onAfterRequest = onMockAfterRequest,
                onRevertOptimisticUpdate = onMockRevertOptimistic,
                onSaveRequest = onMockSaveRequest,
                onSynchronization = onMockSynchronization
            )

            coVerifyOrder {
                onMockBeforeRequest()
                onMockSynchronization()
            }
            coVerify(exactly = 0) { onMockAfterRequest() }
            coVerify(exactly = 0) { onMockRevertOptimistic() }
            coVerify(exactly = 0) { onMockSaveRequest() }
        }
    }

    @Test
    fun `executeRequest check operations order when unknown request error`() {
        runBlockingTest {
            val exception = RequestException(ErrorType.UNKNOWN)
            val makeRequest: suspend () -> Task = { throw exception }

            requestManager.executeRequest(
                onBeforeRequest = onMockBeforeRequest,
                makeRequest = makeRequest,
                onAfterRequest = onMockAfterRequest,
                onRevertOptimisticUpdate = onMockRevertOptimistic,
                onSaveRequest = onMockSaveRequest,
                onSynchronization = onMockSynchronization
            )

            coVerifyOrder {
                onMockBeforeRequest()
                onMockRevertOptimistic()
            }
            coVerify(exactly = 0) { onMockAfterRequest() }
            coVerify(exactly = 0) { onMockSynchronization() }
            coVerify(exactly = 0) { onMockSaveRequest() }
        }
    }

    @Test
    fun `executeRequest check operations order when timeout request error`() {
        runBlockingTest {
            val exception = RequestException(ErrorType.TIMEOUT)
            val makeRequest: suspend () -> Task = { throw exception }

            requestManager.executeRequest(
                onBeforeRequest = onMockBeforeRequest,
                makeRequest = makeRequest,
                onAfterRequest = onMockAfterRequest,
                onRevertOptimisticUpdate = onMockRevertOptimistic,
                onSaveRequest = onMockSaveRequest,
                onSynchronization = onMockSynchronization
            )

            coVerifyOrder {
                onMockBeforeRequest()
                onMockRevertOptimistic()
            }
            coVerify(exactly = 0) { onMockAfterRequest() }
            coVerify(exactly = 0) { onMockSynchronization() }
            coVerify(exactly = 0) { onMockSaveRequest() }
        }
    }

    @Test
    fun `executeRequest check operations order when server request error`() {
        runBlockingTest {
            val exception = RequestException(ErrorType.SERVER)
            val makeRequest: suspend () -> Task = { throw exception }

            requestManager.executeRequest(
                onBeforeRequest = onMockBeforeRequest,
                makeRequest = makeRequest,
                onAfterRequest = onMockAfterRequest,
                onRevertOptimisticUpdate = onMockRevertOptimistic,
                onSaveRequest = onMockSaveRequest,
                onSynchronization = onMockSynchronization
            )

            coVerifyOrder {
                onMockBeforeRequest()
                onMockAfterRequest()
                onMockSaveRequest()
            }
            coVerify(exactly = 0) { onMockRevertOptimistic() }
            coVerify(exactly = 0) { onMockSynchronization() }
        }
    }

    @Test
    fun `executeRequest check operations order when connection request error`() {
        runBlockingTest {
            val exception = RequestException(ErrorType.CONNECTION)
            val makeRequest: suspend () -> Task = { throw exception }

            requestManager.executeRequest(
                onBeforeRequest = onMockBeforeRequest,
                makeRequest = makeRequest,
                onAfterRequest = onMockAfterRequest,
                onRevertOptimisticUpdate = onMockRevertOptimistic,
                onSaveRequest = onMockSaveRequest,
                onSynchronization = onMockSynchronization
            )

            coVerifyOrder {
                onMockBeforeRequest()
                onMockAfterRequest()
                onMockSaveRequest()
            }
            coVerify(exactly = 0) { onMockRevertOptimistic() }
            coVerify(exactly = 0) { onMockSynchronization() }
        }
    }

    @Test
    fun `executeRequest should return resource loaded status when success request`() {
        runBlockingTest {
            val task = Task("test", "test", 0L, "basic", false)
            val makeRequest: suspend () -> Task = { task }

            val result = requestManager.executeRequest(
                onBeforeRequest = onMockBeforeRequest,
                makeRequest = makeRequest,
                onAfterRequest = onMockAfterRequest,
                onRevertOptimisticUpdate = onMockRevertOptimistic,
                onSaveRequest = onMockSaveRequest,
                onSynchronization = onMockSynchronization
            )

            assertEquals(Resource.Loaded(task), result)
        }
    }

    @Test
    fun `executeRequest should return resource error status when not found error request`() {
        runBlockingTest {
            val exception = RequestException(ErrorType.NOT_FOUND)
            val makeRequest: suspend () -> Task = { throw exception }

            val result = requestManager.executeRequest(
                onBeforeRequest = onMockBeforeRequest,
                makeRequest = makeRequest,
                onAfterRequest = onMockAfterRequest,
                onRevertOptimisticUpdate = onMockRevertOptimistic,
                onSaveRequest = onMockSaveRequest,
                onSynchronization = onMockSynchronization
            )

            assertEquals(Resource.Error(exception.matchMessage()), result)
        }
    }

    @Test
    fun `executeRequest should return resource error status when unknown error request`() {
        runBlockingTest {
            val exception = RequestException(ErrorType.UNKNOWN)
            val makeRequest: suspend () -> Task = { throw exception }

            val result = requestManager.executeRequest(
                onBeforeRequest = onMockBeforeRequest,
                makeRequest = makeRequest,
                onAfterRequest = onMockAfterRequest,
                onRevertOptimisticUpdate = onMockRevertOptimistic,
                onSaveRequest = onMockSaveRequest,
                onSynchronization = onMockSynchronization
            )

            assertEquals(Resource.Error(exception.matchMessage()), result)
        }
    }

    @Test
    fun `executeRequest should return resource error status when timeout error request`() {
        runBlockingTest {
            val exception = RequestException(ErrorType.TIMEOUT)
            val makeRequest: suspend () -> Task = { throw exception }

            val result = requestManager.executeRequest(
                onBeforeRequest = onMockBeforeRequest,
                makeRequest = makeRequest,
                onAfterRequest = onMockAfterRequest,
                onRevertOptimisticUpdate = onMockRevertOptimistic,
                onSaveRequest = onMockSaveRequest,
                onSynchronization = onMockSynchronization
            )

            assertEquals(Resource.Error(exception.matchMessage()), result)
        }
    }

    @Test
    fun `executeRequest should return resource loaded status when server error request`() {
        runBlockingTest {
            val exception = RequestException(ErrorType.SERVER)
            val makeRequest: suspend () -> Task = { throw exception }
            val task = Task("test", "test", 0L, "basic", false)
            coEvery { onMockSaveRequest() }.returns(task)

            val result = requestManager.executeRequest(
                onBeforeRequest = onMockBeforeRequest,
                makeRequest = makeRequest,
                onAfterRequest = onMockAfterRequest,
                onRevertOptimisticUpdate = onMockRevertOptimistic,
                onSaveRequest = onMockSaveRequest,
                onSynchronization = onMockSynchronization
            )

            assertEquals(Resource.Loaded(task), result)
        }
    }

    @Test
    fun `executeRequest should return resource loaded status when connection error request`() {
        runBlockingTest {
            val exception = RequestException(ErrorType.CONNECTION)
            val makeRequest: suspend () -> Task = { throw exception }
            val task = Task("test", "test", 0L, "basic", false)
            coEvery { onMockSaveRequest() }.returns(task)

            val result = requestManager.executeRequest(
                onBeforeRequest = onMockBeforeRequest,
                makeRequest = makeRequest,
                onAfterRequest = onMockAfterRequest,
                onRevertOptimisticUpdate = onMockRevertOptimistic,
                onSaveRequest = onMockSaveRequest,
                onSynchronization = onMockSynchronization
            )

            assertEquals(Resource.Loaded(task), result)
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

            assertEquals(Resource.Loaded(task), result)
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

            assertEquals(Resource.Error(exception.matchMessage()), result)
        }
    }
}