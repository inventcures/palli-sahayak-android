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
