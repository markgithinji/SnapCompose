pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ComposeGallery"
include(":app")
include(":baselineprofile")

// Core Modules
include(":core:domain")
include(":core:network")
include(":core:ui")
include(":core:database")
include(":core:common")
include(":core:navigation")

// Feature Modules
include(":feature:home")
include(":feature:search")
include(":feature:profile")
include(":feature:photodetail")
