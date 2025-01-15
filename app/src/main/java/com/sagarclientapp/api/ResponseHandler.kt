package com.sagarclientapp.api

import com.google.gson.Gson

import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException

open class ResponseHandler {
    fun <T> handleSuccess(data: T, code: Any): Response<T> {
        return Response.Success(data, code)
    }

    fun <T> handleException(e: Exception, code: Int = 0): Response<T> {
        return when (e) {
            is HttpException -> {
                Response.Error(
                    getErrorMessageText(e.code(),e.message()),
                    code,
                    e.code(),
                    errorBody = getErrorBody(e)
                )
            }

            is ConnectException -> Response.Error(
                getErrorMessageText(1,e.message.toString()),
                code
            )

            is SocketTimeoutException -> Response.Error(
                getErrorMessageText(0,e.message.toString()),
                code
            )

            else -> Response.Error(
                getErrorMessageText(Int.MAX_VALUE,e.message.toString()),
                code
            )
        }
    }

    private fun getErrorBody(e: HttpException): ErrorResponse? {
        try {
            return Gson().fromJson(
                e.response()?.errorBody()?.byteStream()?.let {
                    String(it.readBytes())
                },
                ErrorResponse::class.java
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getErrorMessage(code: Int): String {
        return when (code) {
            TIME_OUT -> TIME_OUT_MSG
            UN_AUTHORIZED -> UN_AUTHORIZED_MSG
            NOT_FOUND -> NOT_FOUND_MSG
            CONNECTION -> CONNECTION_MSG
            INTERNAL_SERVER_ERROR -> INTERNAL_SERVER_ERROR_MSG
            else -> ERROR_MSG
        }
    }
    private fun getErrorMessageText(code: Int, message: String): String {
        return message

    }
}
