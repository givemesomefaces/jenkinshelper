package com.lvlifeng.jenkinshelper.jenkins

/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-15 16:52
 */
class JenkinsData constructor(name: String, serverUrl: String) {

    var name: String? = name
        get() = field
        set(value) {
            field = value
        }

    var serverUrl: String? = serverUrl
        get() = field
        set(value) {
            field = value
        }

    var jobs: List<Job>? = null
        get() = field
        set(value) {
            field = value
        }

    var views: List<View>? = null
        get() = field
        set(value) {
            field = value
        }

    var primaryView: View? = null
        get() = field
        set(value) {
            field = value
        }

}