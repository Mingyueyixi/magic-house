import java.net.URI

pluginManagement {
    repositories {
        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public")
        maven("https://repo.huaweicloud.com/repository/maven")
        maven("https://repository.mulesoft.org/nexus/content/repositories/public/")
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
//        maven("http://127.0.0.1:8888/repository/maven-public/")

    }
}
dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public")
        maven("https://repo.huaweicloud.com/repository/maven")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://repository.mulesoft.org/nexus/content/repositories/public/")
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
//        maven("http://127.0.0.1:8888/repository/maven-public/")
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

