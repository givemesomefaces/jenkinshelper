package com.lvlifeng.jenkinshelper.bean

/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-09 13:54
 */
class BuildConfig constructor(buildLastFailedFlag: Boolean, reBuildFlag: Boolean, reBuildTime: Integer, paramesMap: Map<String, String>){
    var buildLastFailedFlag: Boolean? = buildLastFailedFlag
        get() = field
        set(value) {
            field = value
        }

    var reBuildFlag: Boolean? = reBuildFlag
        get() = field
        set(value) {
            field = value
        }

    var reBuildTime: Integer? = reBuildTime
        get() = field
        set(value) {
            field = value
        }

    var parames: Map<String, String>? = paramesMap
        get() = field
        set(value) {
            field = value
        }
}