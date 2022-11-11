plugins { id("org.openjfx.javafxplugin") version "0.0.10" }

configure<io.vacco.oss.gitflow.GsPluginProfileExtension> {
  addJ8Spec()
  sharedLibrary(true, false)
}

val api by configurations

dependencies {
  api(project(":km-core"))
  api("io.vacco.gemory:gemory:0.3.1")
}

javafx {
  version = "17"
  modules("javafx.controls", "javafx.media")
}
