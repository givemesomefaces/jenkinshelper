package com.lvlifeng.jenkinshelper.helper

import cn.hutool.core.map.MapUtil
import com.lvlifeng.jenkinshelper.bean.BuildConfig
import com.offbytwo.jenkins.JenkinsServer
import com.offbytwo.jenkins.model.Job
import com.offbytwo.jenkins.model.JobWithDetails

/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-10 11:42
 */
class JobBuildHelper {

    companion object {
        fun build(config: BuildConfig, jb: Job) {
            if (config.buildLastFailedFlag == true) {
                val details: JobWithDetails = jb.details()
                if (details.lastBuild.number != details.lastFailedBuild.number) {
                    return
                }
                if (details.queueItem != null && details.lastCompletedBuild.number == details.lastBuild.number) {
                    return
                }
            }
            if (MapUtil.isNotEmpty(config.paramesMap)) {
                jb.build(config.paramesMap, true)
            } else {
                jb.build(true)
            }
        }
    }
}