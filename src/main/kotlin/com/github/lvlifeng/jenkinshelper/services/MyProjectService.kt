package com.github.lvlifeng.jenkinshelper.services

import com.intellij.openapi.project.Project
import com.github.lvlifeng.jenkinshelper.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
