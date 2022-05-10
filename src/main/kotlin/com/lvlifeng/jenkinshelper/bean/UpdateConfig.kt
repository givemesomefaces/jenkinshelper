package com.lvlifeng.jenkinshelper.bean

import java.util.HashMap

/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-09 17:14
 */
class UpdateConfig constructor(newGitBranchName: String, stringParamsMap: HashMap<String, String>){

    var newGitBranchName: String? = newGitBranchName
        get() = field
        set(value) {
            field = value
        }
    var stringParamsMap: HashMap<String, String>? = stringParamsMap
        get() = field
        set(value) {
            field = value
        }
}