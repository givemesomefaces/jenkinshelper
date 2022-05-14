package com.lvlifeng.jenkinshelper.helper

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.sun.istack.Nullable

/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-12 18:49
 */
object NotifierHelper {

    fun notifyError(@Nullable project: Project?, content: String?) {
        NotificationGroupManager.getInstance().getNotificationGroup("JenkinsHelper Notification Group")
            .createNotification(content!!, NotificationType.ERROR)
            .notify(project)
    }

    fun notifyInfo(@Nullable project: Project?, content: String?) {
        NotificationGroupManager.getInstance().getNotificationGroup("JenkinsHelper Notification Group")
            .createNotification(content!!, NotificationType.INFORMATION)
            .notify(project)
    }

    fun notifyWarn(@Nullable project: Project?, content: String?) {
        NotificationGroupManager.getInstance().getNotificationGroup("JenkinsHelper Notification Group")
            .createNotification(content!!, NotificationType.WARNING)
            .notify(project)
    }
}