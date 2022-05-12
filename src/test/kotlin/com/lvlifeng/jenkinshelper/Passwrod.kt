package com.lvlifeng.jenkinshelper

import com.lvlifeng.jenkinshelper.jenkins.Credentials

fun main() {
    Credentials.saveCredential("lvlifeng123", "lvlifeng", "123456")
    val password = Credentials.getPassword("lvlifeng123")
    println(password)

}
class Passwrod {

}