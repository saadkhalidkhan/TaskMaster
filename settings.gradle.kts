pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "TaskMaster"

// Core modules
include(":app")
include(":core")
include(":core-ui")
include(":networking")
include(":database")
include(":data")

// Feature modules
include(":feature:auth")
include(":feature:profile")
include(":feature:tasks")
include(":feature:dashboard")

// Shared modules
include(":shared:analytics")
include(":shared:utils")
include(":shared:theme")
