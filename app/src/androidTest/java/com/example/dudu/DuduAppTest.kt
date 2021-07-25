package com.example.dudu

class DuduAppTest : DuduApp() {

    var url = "http://localhost:8080"

    override fun getBaseUrl(): String {
        return url
    }
}