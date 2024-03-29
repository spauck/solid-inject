import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
}

version = "0.1-SNAPSHOT"

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation(project(":solid-inject-core"))

  testImplementation("org.assertj:assertj-core:3.12.2")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")

  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.2")
}

tasks.withType<Test> {
  useJUnitPlatform()
}
tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}
