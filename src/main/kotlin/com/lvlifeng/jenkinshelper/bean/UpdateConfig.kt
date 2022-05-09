package com.lvlifeng.jenkinshelper.bean

import java.util.HashMap

/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-09 17:14
 */
class UpdateConfig {

    var newGitBranchName: String? = null
        get() = field
        set(value) {
            field = value
        }
    var stringParamsMap: HashMap<String, String>? = null
        get() = field
        set(value) {
            field = value
        }
}