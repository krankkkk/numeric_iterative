plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("org.beryx.jlink") version "2.24.4"
    id("org.openjfx.javafxplugin") version "0.0.10"
}

group = project.group
version = project.version

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":algorithm"))
}

application {
    mainClass.set("dev.tkrause.iterations.client.Main")
    mainModule.set("linear")
    applicationDefaultJvmArgs = listOf("-Dfile.encoding=UTF-8")
}


jlink {
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "linear"
    }
}

javafx {
    modules("javafx.controls", "javafx.fxml")
    version = "17.0.1"
}