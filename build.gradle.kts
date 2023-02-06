import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "1.7.10"
  id("org.jetbrains.intellij") version "1.12.0"
  id("org.jetbrains.changelog") version "1.3.1"
  id("io.gitlab.arturbosch.detekt") version "1.22.0"
  id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

repositories {
  mavenCentral()
  jcenter()
}
dependencies {
  detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.20.0")
}

intellij {
  pluginName.set(properties("pluginName"))
  version.set(properties("platformVersion"))
  type.set(properties("platformType"))
  downloadSources.set(properties("platformDownloadSources").toBoolean())
  updateSinceUntilBuild.set(true)

}

changelog {
  version.set(properties("pluginVersion"))
  groups.set(emptyList())
}

detekt {
  config = files("./detekt-config.yml")
  buildUponDefaultConfig = true

  reports {
    html.enabled = false
    xml.enabled = false
    txt.enabled = false
  }
}

tasks {
  withType<JavaCompile> {
    sourceCompatibility = "11"
    targetCompatibility = "11"
  }
  withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
  }

  withType<Detekt> {
    jvmTarget = "11"
  }

  patchPluginXml {
    version.set(properties("pluginVersion"))
    sinceBuild.set(properties("pluginSinceBuild"))
    untilBuild.set(properties("pluginUntilBuild"))

    pluginDescription.set(File("/Users/gabrielmaia/Documents/my/jetbrains-owl-theme/README.md").readText().lines().run {
      val start = "<!-- Plugin description -->"
      val end = "<!-- Plugin description end -->"

      if (!containsAll(listOf(start, end))) {
        throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
      }
      subList(indexOf(start) + 1, indexOf(end))
    }.joinToString("\n").run { markdownToHTML(this) })

    changeNotes.set(changelog.getUnreleased().toHTML())
  }

  runPluginVerifier {
    ideVersions.set((properties("pluginVerifierIdeVersions").split(',').map(String::trim).filter(String::isNotEmpty)))
  }

  publishPlugin {
    dependsOn("patchChangelog")
    token.set("mrootx")
  }
}
