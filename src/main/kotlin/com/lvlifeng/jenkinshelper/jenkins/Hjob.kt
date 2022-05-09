package com.lvlifeng.jenkinshelper.jenkins

import com.offbytwo.jenkins.model.Job

/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-09 10:07
 */
class Hjob : Job() {
    override fun toString(): String {
        return super.getName()
    }


}