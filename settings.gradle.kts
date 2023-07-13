pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Cheddar"

include(":app")
include(":feature:storynavigation")
include(":feature:storyfeed")
include(":feature:storydetail")
include(":core:common")
include(":core:model")
include(":core:ui")
include(":core:domain")
include(":core:network")
include(":core:hackernews")
include(":core:webpreview")
include(":core:matryoshka")
include(":core:testing")
include(":convention")
