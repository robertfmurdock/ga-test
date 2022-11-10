
import com.fasterxml.jackson.databind.ObjectMapper
import com.zegreatrob.coupling.plugins.NodeExec
import com.zegreatrob.coupling.plugins.setup
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

plugins {
    id("com.zegreatrob.coupling.plugins.jstools")
    id("com.zegreatrob.coupling.plugins.serialization")
}

kotlin {
    js {
        browser {
            webpackTask {
                dependsOn("additionalResources")
                inputs.files("${project.projectDir}/src/main/resources")
                val profile: String? by project
                if (!profile.isNullOrBlank()) {
                    this.args.add("--profile")
                    this.args.add("--json=$buildDir/reports/stats.json")
                }
            }
        }
    }
    sourceSets {
        val main by getting {
            resources.srcDir("src/main/javascript")
        }
        all {
            languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
        }
    }
}

val clientConfiguration: Configuration by configurations.creating

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation("com.zegreatrob.testmints:action")
    implementation("com.zegreatrob.testmints:action-async")
    implementation("com.zegreatrob.jsmints:minreact")
    implementation("com.zegreatrob.jsmints:react-data-loader")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-extensions")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-css")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-styled-next")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-react-router-dom")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-react-popper")
    implementation("com.soywiz.korlibs.klock:klock:3.4.0")
    implementation("com.benasher44:uuid:0.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.5")

    testImplementation("org.jetbrains.kotlin:kotlin-test-common")
    testImplementation("org.jetbrains.kotlin:kotlin-test-js")
    testImplementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
    testImplementation("com.zegreatrob.testmints:standard")
    testImplementation("com.zegreatrob.testmints:async")
    testImplementation("com.zegreatrob.testmints:minassert")
    testImplementation("com.zegreatrob.testmints:minspy")
    testImplementation("com.zegreatrob.jsmints:minenzyme")
}

val nodeEnv = System.getenv("COUPLING_NODE_ENV") ?: "production"

tasks {
    val cdnBuildOutput = "${project.buildDir.absolutePath}/cdn.json"
    val browserDistribution = named("browserDistribution")
    val browserProductionWebpack = named("browserProductionWebpack", KotlinWebpack::class) {
        outputs.dir(destinationDirectory.absolutePath + "/html")
        outputs.cacheIf { true }
        artifacts {
            add(clientConfiguration.name, destinationDirectory) {
                builtBy(this@named, browserDistribution)
            }
        }
    }

    val release by registering {
        dependsOn(":release")
    }

    val additionalResources by registering(Copy::class) {
        outputs.cacheIf { true }
        val javascriptConfig = configurations["runtimeClasspath"]
        dependsOn(javascriptConfig)
        duplicatesStrategy = DuplicatesStrategy.WARN
        val additionalResourcesPath = "${project.buildDir.absolutePath}/additionalResources"
        into(additionalResourcesPath)
        from({
            javascriptConfig.files.map {
                if (!it.isFile || !it.name.endsWith(".klib")) {
                    null
                } else {
                    zipTree(it).matching {
                        exclude(
                            "default",
                            "default/**/*",
                            "kotlin",
                            "kotlin/**/*",
                            "kotlin-test",
                            "kotlin-test/**/*",
                            "META-INF",
                            "META-INF/**/*",
                            "org",
                            "org/**/*",
                            "kotlin.js",
                            "kotlin.js.map",
                            "kotlin.meta.js",
                            "kotlin-test.js",
                            "kotlin-test.js.map",
                            "kotlin-test.meta.js",
                            "package.json",
                        )
                    }
                }
            }
        })
    }

    named("browserTest") {
        outputs.cacheIf { true }
    }

}
