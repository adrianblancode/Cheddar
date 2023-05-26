pluginManagement {
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

rootProject.name="Cheddar"
include(":app")
include(":core")
include(":hackernews")
include(":feature-storynavigation")
include(":feature-storyfeed")
include(":feature-storydetail")
include(":network")
include(":webpreview")
include(":domain")
include(":matryoshka")
include(":common-test")
include(":common")
include(":common-ui")