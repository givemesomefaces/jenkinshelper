package com.lvlifeng.jenkinshelper.services

import com.intellij.openapi.project.Project
import com.lvlifeng.jenkinshelper.Bundle

class MyProjectService(project: Project) {

    init {
        println(Bundle.message("projectService", project.name))
    }
}
