package com.example.dudu.data.errors

class RequestException(
    val type: ErrorType
) : Exception() {

    // Пока нет идей как взять текста из strings без ссылки на context
    fun matchMessage(): String {
        return when(type) {
            ErrorType.NOT_FOUND -> "Данные не актуальны, сейчас произойдет синхронизация"
            ErrorType.SERVER -> "Ошибка сервера"
            ErrorType.UNKNOWN -> "Произошла ошибка, попробуйте позже"
            ErrorType.TIMEOUT -> "Запрос отменен. Проверьте интернет соединение"
            ErrorType.CONNECTION -> "Нет интернет соединения"
        }
    }
}