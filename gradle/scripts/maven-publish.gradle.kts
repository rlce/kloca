apply(plugin = "maven-publish")

configure<PublishingExtension> {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri(project.findProperty("maven.github.packages.url") as String)
            credentials {
                username = project.findProperty(project.findProperty("maven.github.packages.username") as String) as String? ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty(project.findProperty("maven.github.packages.password") as String) as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
        mavenLocal()
    }

    publications.withType<MavenPublication> {
        pom {
            name.set(project.findProperty("${project.name.replace('-', '.')}.name") as String? 
                ?: project.name.split("-").joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } })
            description.set(project.findProperty("${project.name.replace('-', '.')}.description") as String? 
                ?: project.findProperty("project.description") as String)
            url.set(project.findProperty("project.url") as String)
            
            licenses {
                license {
                    name.set(project.findProperty("project.license.name") as String)
                    url.set(project.findProperty("project.license.url") as String)
                }
            }
            
            developers {
                developer {
                    id.set(project.findProperty("project.developer.id") as String)
                    name.set(project.findProperty("project.developer.name") as String)
                    email.set(project.findProperty("project.developer.email") as String)
                }
            }
            
            scm {
                connection.set(project.findProperty("project.vcs.connection") as String)
                developerConnection.set(project.findProperty("project.vcs.developerConnection") as String)
                url.set(project.findProperty("project.vcs.url") as String)
            }
        }
    }
}