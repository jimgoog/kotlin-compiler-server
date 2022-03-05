package com.compiler.server.compiler.components

import com.compiler.server.model.bean.LibrariesFile
import component.KotlinEnvironment
import org.jetbrains.kotlin.library.impl.isKotlinLibrary
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KotlinEnvironmentConfiguration(val librariesFile: LibrariesFile) {
  @Bean
  fun kotlinEnvironment(): KotlinEnvironment {
    val classPath =
      listOfNotNull(librariesFile.jvm)
        .flatMap {
          it.listFiles()?.toList()
            ?: throw error("No kotlin libraries found in: ${librariesFile.jvm.absolutePath}")
        }

    // isKotlinLibrary is not so fast, possibly it is worth to cache results
    // In our Gradle plugin:
    // https://github.com/JetBrains/kotlin/blob/41fc5e24e9598ca39e6ca1521330a7c1d9503879/libraries/tools/kotlin-gradle-plugin/src/main/kotlin/org/jetbrains/kotlin/gradle/tasks/Tasks.kt#L1085
    val additionalJsClasspath = librariesFile.js
      .listFiles()!!
      .toList()
      .filter { isKotlinLibrary(it) }
    return KotlinEnvironment(classPath, additionalJsClasspath)
  }
}
