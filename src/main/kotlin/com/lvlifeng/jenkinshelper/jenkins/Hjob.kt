package com.lvlifeng.jenkinshelper.jenkins

import com.offbytwo.jenkins.model.Job


class Hjob : Job() {
    override fun toString(): String {
        return super.getName()
    }


}