package com.lvlifeng.jenkinshelper.bean

/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-09 13:54
 */
class BuildConfig {
    var buildLastFailedFlag: Boolean? = false
        get() = field
        set(value) {
            field = value
        }

    var reBuildFlag: Boolean? = false
        get() = field
        set(value) {
            field = value
        }

    var reBuildTime: Integer? = null
        get() = field
        set(value) {
            field = value
        }

    var paramesMap: Map<String, String>? = null
        get() = field
        set(value) {
            field = value
        }
}