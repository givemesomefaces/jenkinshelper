package com.lvlifeng.jenkinshelper.helper

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import java.util.function.Supplier

/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-13 12:17
 */
class ProgressHelper {


    companion object {
        fun show(project: Project, title: String, message1: String, message2: String, canBeCancelled: Boolean, method: Supplier<Void>) {
            ProgressManager.getInstance().run(object : Task.Modal(project, title, canBeCancelled) {
                override fun run(indicator: ProgressIndicator) {
                    indicator.text = message1
                    method.get()
                    indicator.text = message2
                }

                override fun onSuccess() {
                    super.onSuccess()
                }
            })
        }
    }
}