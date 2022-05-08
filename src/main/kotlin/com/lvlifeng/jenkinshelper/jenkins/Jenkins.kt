package com.lvlifeng.jenkinshelper.jenkins

import com.offbytwo.jenkins.JenkinsServer
import java.net.URI
import java.net.URISyntaxException

/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-08 10:27
 */
class Jenkins constructor() {

    var nickName: String? = null
        get() = field
        set(value) {
            field = value
        }

    var apiUrl: String? = null
        get() = field
        set(value) {
            field = value
        }
    var userName: String? = null
        get() = field
        set(value) {
            field = value
        }
    var password: String? = null
        get() = field
        set(value) {
            field = value
        }

    constructor(nickName: String?, apiUrl: String?, userName: String?, password: String?) : this() {
        this.nickName = nickName
        this.apiUrl = apiUrl
        this.userName = userName
        this.password = password
    }


    fun server(jk: Jenkins): JenkinsServer {
        var jenkinsServer: JenkinsServer? = null
        try {
            jenkinsServer = JenkinsServer(
                URI(jk.apiUrl),
                jk.userName,
                jk.password
            )
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        return jenkinsServer!!
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Jenkins

        if (apiUrl != other.apiUrl) return false
        if (userName != other.userName) return false
        if (password != other.password) return false

        return true
    }

    override fun hashCode(): Int {
        var result = apiUrl?.hashCode() ?: 0
        result = 31 * result + (userName?.hashCode() ?: 0)
        result = 31 * result + (password?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "$nickName"
    }


}