package com.example.myapplication

import java.io.Serializable

data class Student(
    var id: String = "",
    var name: String = "",
    var code: String = "",
    var email: String = "",
    var avatarUrl: String = ""
) : Serializable
