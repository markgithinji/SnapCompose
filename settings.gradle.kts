pluginManagement {
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
include(":core:model")
include(":core:data")
include(":core:ui")
include(":core:database")
include(":core:navigation")

// Feature Modules
include(":feature:home")
include(":feature:search")
include(":feature:profile")
include(":feature:photodetail")
