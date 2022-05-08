package com.lvlifeng.jenkinshelper.window

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.lvlifeng.jenkinshelper.JenkinsHelperWindow

class JenkinsHelperFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val jenkinsHelperWindow = JenkinsHelperWindow(project)
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(jenkinsHelperWindow.component, "", false)
        toolWindow.contentManager.addContent(content)
    }
}