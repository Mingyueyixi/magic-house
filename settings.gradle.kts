
pluginManagement {
    repositories {
        maven("https://repo.huaweicloud.com/repository/maven")
        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/central")
        maven("https://jitpack.io/") {
            content {
                includeGroupByRegex("com\\.github.*")
                includeGroupByRegex("com\\.gitee.*")
                includeGroupByRegex("com\\.gitlab.*")
                includeGroupByRegex("org\\.bitbucket.*")
                includeGroupByRegex("org\\.azure.*")
            }
        }

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
dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://repo.huaweicloud.com/repository/maven")
        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public")

        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/central")
        maven("https://jitpack.io/") {
            content {
                includeGroupByRegex("com\\.github.*")
                includeGroupByRegex("com\\.gitee.*")
                includeGroupByRegex("com\\.gitlab.*")
                includeGroupByRegex("org\\.bitbucket.*")
                includeGroupByRegex("org\\.azure.*")
            }
        }

        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "MagicHouse"
include(":base")
include(":magic:amap")
include(":magic:screen")
include(":magic:fuck-vibrator")
include(":magic:fuck-dialog")
include(":magic:catch-log")
include(":app")
include(":demo:TestDemo")
include(":witch-app")
include(":bridge")

