package com.lvlifeng.jenkinshelper.jenkins

import com.offbytwo.jenkins.model.Job
import lombok.Getter
import lombok.Setter

/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-09 10:07
 */
@Getter
@Setter
class Job : Job() {
    override fun toString(): String {
        return super.getName()
    }


}