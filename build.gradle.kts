plugins { id("io.vacco.oss.gitflow") version "1.8.2" }

group = "io.vacco.kimaris"
version = "0.1.2"

configure<io.vacco.oss.gitflow.GsPluginProfileExtension> {
  sharedLibrary(true, false)
  addJ8Spec()
}

dependencies {
  testImplementation("com.google.code.gson:gson:2.9.0")
  testImplementation("io.vacco.sabnock:sabnock:0.1.0")
  testImplementation("io.vacco.oruzka:oruzka:0.1.5.1")
}
