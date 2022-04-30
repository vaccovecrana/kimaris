plugins { id("io.vacco.oss.gitflow") version "0.9.8" }

group = "io.vacco.kimaris"
version = "0.1.2"

configure<io.vacco.oss.gitflow.GsPluginProfileExtension> {
  addJ8Spec()
  addClasspathHell()
  sharedLibrary(true, false)
}

dependencies {
  testImplementation("com.google.code.gson:gson:2.9.0")
  testImplementation("io.vacco.sabnock:sabnock:0.1.0")
}
