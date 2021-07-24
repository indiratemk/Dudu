package com.example.dudu.data

import com.example.dudu.data.errors.ErrorType
import com.example.dudu.data.errors.RequestException
import com.example.dudu.data.helpers.ResponseHandler
import com.example.dudu.data.models.Task
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@ExperimentalCoroutinesApi
class ResponseHandlerTest {

    private lateinit var responseHandler: ResponseHandler

    @Before
    fun setUp() {
        responseHandler = ResponseHandler()
    }

    @Test
    fun `handleResponse should return data when response successful`() {
        runBlockingTest {
            val task = Task("test", "test", 0L, "basic", false)
            val request: suspend () -> Response<Task> = { Response.success(task) }

            val data = responseHandler.handleResponse(request)

            assertEquals(task, data)
        }
    }

    @Test
    fun `handleResponse should throw request exception with not found error type when response code 404`() {
        runBlockingTest {
            val request: suspend () -> Response<Task> =
                { Response.error(404, mockk(relaxed = true)) }

            try {
                responseHandler.handleResponse(request)
            } catch (exception: RequestException) {
                assertEquals(ErrorType.NOT_FOUND, exception.type)
            }
        }
    }

    @Test
    fun `handleResponse should throw request exception with server error type when response code 500`() {
        runBlockingTest {
            val request: suspend () -> Response<Task> =
                { Response.error(500, mockk(relaxed = true)) }

            try {
                responseHandler.handleResponse(request)
            } catch (exception: RequestException) {
                assertEquals(ErrorType.SERVER, exception.type)
            }
        }
    }

    @Test
    fun `handleResponse should throw request exception with unknown error type when response error and code not 404 and 500`() {
        runBlockingTest {
            val request: suspend () -> Response<Task> =
                { Response.error(403, mockk(relaxed = true)) }

            try {
                responseHandler.handleResponse(request)
            } catch (exception: RequestException) {
                assertEquals(ErrorType.UNKNOWN, exception.type)
            }
        }
    }

    @Test
    fun `handleResponse should throw request exception with timeout error type when request throws socket timeout exception`() {
        runBlockingTest {
            val request: suspend () -> Response<Task> =
                { throw mockk<SocketTimeoutException>() }

            try {
                responseHandler.handleResponse(request)
            } catch (exception: RequestException) {
                assertEquals(ErrorType.TIMEOUT, exception.type)
            }
        }
    }

    @Test
    fun `handleResponse should throw request exception with connection error type when request throws unknown host exception`() {
        runBlockingTest {
            val request: suspend () -> Response<Task> =
                { throw mockk<UnknownHostException>() }

            try {
                responseHandler.handleResponse(request)
            } catch (exception: RequestException) {
                assertEquals(ErrorType.CONNECTION, exception.type)
            }
        }
    }

    @Test
    fun `handleResponse should throw request exception with unknown error type when request throws io exception`() {
        runBlockingTest {
            val request: suspend () -> Response<Task> =
                { throw mockk<IOException>() }

            try {
                responseHandler.handleResponse(request)
            } catch (exception: RequestException) {
                assertEquals(ErrorType.UNKNOWN, exception.type)
            }
        }
    }
}