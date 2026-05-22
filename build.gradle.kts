import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	kotlin("jvm") version "2.3.21"
	id("org.jetbrains.dokka-javadoc") version "2.2.0"
	`java-library`
	`maven-publish`
}

group = "com.subhrodip"
version = "0.1.0-SNAPSHOT"
description = "Fast Kotlin UUIDv7 generator and utilities following RFC 9562."

kotlin {
	explicitApi()
	jvmToolchain(17)

	compilerOptions {
		jvmTarget.set(JvmTarget.JVM_1_8)
		allWarningsAsErrors.set(true)
	}
}

java {
	withSourcesJar()
}

val dokkaJavadocJar by tasks.registering(Jar::class) {
	description = "Assembles Kotlin API documentation in Javadoc format."
	group = JavaBasePlugin.DOCUMENTATION_GROUP
	archiveClassifier.set("javadoc")
	from(tasks.named("dokkaGeneratePublicationJavadoc"))
}

tasks.withType<JavaCompile>().configureEach {
	options.release.set(8)
	options.encoding = "UTF-8"
}

dependencies {
	implementation(kotlin("stdlib"))

	testImplementation(platform("org.junit:junit-bom:5.10.3"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test>().configureEach {
	useJUnitPlatform()

	val testJavaVersion = providers.gradleProperty("testJavaVersion").orElse("17")
	javaLauncher.set(
		javaToolchains.launcherFor {
			languageVersion.set(JavaLanguageVersion.of(testJavaVersion.get().toInt()))
		},
	)
}

tasks.withType<AbstractArchiveTask>().configureEach {
	isPreserveFileTimestamps = false
	isReproducibleFileOrder = true
}

tasks.jar {
	manifest {
		attributes("Automatic-Module-Name" to "com.subhrodip.uuidv7")
	}
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"])
			artifact(dokkaJavadocJar)

			pom {
				name.set("uuidv7-kt")
				description.set(project.description)
				url.set("https://github.com/ohbus/uuidv7-kt")

				licenses {
					license {
						name.set("MIT License")
						url.set("https://opensource.org/license/mit")
					}
				}

				developers {
					developer {
						id.set("ohbus")
						name.set("Subhrodip")
						url.set("https://github.com/ohbus")
					}
				}

				scm {
					connection.set("scm:git:https://github.com/ohbus/uuidv7-kt.git")
					developerConnection.set("scm:git:ssh://git@github.com/ohbus/uuidv7-kt.git")
					url.set("https://github.com/ohbus/uuidv7-kt")
				}
			}
		}
	}

	repositories {
		maven {
			name = "GitHubPackages"
			url = uri("https://maven.pkg.github.com/ohbus/uuidv7-kt")
			credentials {
				username = System.getenv("GITHUB_ACTOR")
				password = System.getenv("GITHUB_TOKEN")
			}
		}
	}
}
