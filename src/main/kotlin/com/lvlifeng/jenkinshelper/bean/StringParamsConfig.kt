package com.lvlifeng.jenkinshelper.bean

/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-10 12:09
 */
class StringParamsConfig constructor(name: String, description: String, defaultValue: String){
    var name: String? = name
        get() = field
        set(value) {
            field = value
        }

    var description: String? = description
        get() = field
        set(value) {
            field = value
        }

    var defaultValue: String? = defaultValue
        get() = field
        set(value) {
            field = value
        }
}