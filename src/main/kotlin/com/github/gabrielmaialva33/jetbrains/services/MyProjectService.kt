package com.github.gabrielmaialva33.jetbrains.services

import com.github.gabrielmaialva33.jetbrains.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {
  init {
    println(MyBundle.message("projectService", project.name))
  }
}
