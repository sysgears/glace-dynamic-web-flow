grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsCentral()
        mavenCentral()
    }
    dependencies {
        test "org.spockframework:spock-grails-support:0.7-groovy-2.0", {
            export = false
        }
    }

    plugins {
        build(":tomcat:$grailsVersion",
                ":hibernate:$grailsVersion",
                ':release:2.2.0') {
            export = false
        }
        test(':spock:0.7',
                ':codenarc:0.19',
                ':code-coverage:1.2.6') {
            exclude "spock-grails-support"
            export = false
        }
    }
}
