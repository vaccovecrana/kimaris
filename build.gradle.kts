plugins { id("io.vacco.oss.gitflow") version "0.9.8" }

group = "io.vacco.kimaris"
version = "0.5.1"

configure<io.vacco.oss.gitflow.GsPluginProfileExtension> {
  addJ8Spec()
  addClasspathHell()
  sharedLibrary(true, false)
}

dependencies {
  testImplementation("io.vacco.uvcj:uvc:0.0.6")
  testImplementation("io.vacco.uvcj:uvc-jni-linux-x86_64:0.0.6")
  testImplementation("io.vacco.oruzka:oruzka:0.1.5.1")
  testImplementation("com.google.code.gson:gson:2.10.1")
}

tasks.withType<Test> {
  minHeapSize = "512m"
  maxHeapSize = "16384m"
}
