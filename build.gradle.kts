import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.ProjectLocalConfigurations
import org.jetbrains.kotlin.gradle.targets.js.KotlinJsCompilerAttribute
import org.jetbrains.kotlin.gradle.targets.js.yarn.yarn

plugins {
    id("com.github.sghill.distribution-sha") version "0.4.0"
    id("com.zegreatrob.coupling.plugins.versioning")
    id("com.zegreatrob.coupling.plugins.tagger")
    id("com.zegreatrob.coupling.plugins.linter")
    base
}

tagger {
    releaseBranch = "master"
}

yarn.ignoreScripts = false

val appConfiguration: Configuration by configurations.creating {
    attributes {
        attribute(
            KotlinJsCompilerAttribute.jsCompilerAttribute,
            KotlinJsCompilerAttribute.ir
        )
        attribute(
            ProjectLocalConfigurations.ATTRIBUTE,
            ProjectLocalConfigurations.PUBLIC_VALUE
        )
        attribute(
            KotlinPlatformType.attribute,
            KotlinPlatformType.js
        )
    }
}
