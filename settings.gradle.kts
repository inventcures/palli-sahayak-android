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

dependencyResolution {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PalliSahayak"

include(":app")
include(":core:core-common")
include(":core:core-model")
include(":core:core-data")
include(":core:core-network")
include(":core:core-security")
include(":core:core-ui")
include(":core:core-voice")
include(":feature:feature-onboarding")
include(":feature:feature-query")
include(":feature:feature-home")
include(":feature:feature-medication")
include(":feature:feature-patient")
include(":feature:feature-careteam")
include(":core:core-sync")
