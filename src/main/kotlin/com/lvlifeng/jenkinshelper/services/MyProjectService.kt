package com.lvlifeng.jenkinshelper.services

import com.intellij.openapi.project.Project
import com.lvlifeng.jenkinshelper.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
