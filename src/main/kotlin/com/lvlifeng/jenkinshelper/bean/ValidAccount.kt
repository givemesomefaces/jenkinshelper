package com.lvlifeng.jenkinshelper.bean

import com.offbytwo.jenkins.JenkinsServer

/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-11 23:08
 */
class ValidAccount constructor(status: Boolean, jenkinsServer: JenkinsServer) {
    var status: Boolean = status
        get() = field

    var jenkinsServer: JenkinsServer = jenkinsServer
        get() = field
}