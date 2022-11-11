plugins { id("io.vacco.oss.gitflow") version "0.9.8" apply(false) }

subprojects {
  apply(plugin = "io.vacco.oss.gitflow")
  group = "io.vacco.kimaris"
  version = "0.1.2"
}