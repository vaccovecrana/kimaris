configure<io.vacco.oss.gitflow.GsPluginProfileExtension> {
  addJ8Spec()
  addClasspathHell()
}

dependencies {
  testImplementation(project(":km-obj"))
  testImplementation("com.google.code.gson:gson:2.9.0")
  testImplementation("io.vacco.sabnock:sabnock:0.1.0")
  testImplementation("io.vacco.oruzka:oruzka:0.1.5.1")
}
