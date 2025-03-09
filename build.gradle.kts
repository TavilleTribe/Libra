plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.7.5"
}

group = "com.tavillecode"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
    compileOnly(files("D:/魔法世界2/plugins/ItemStorage-1.0-SNAPSHOT.jar"))
    compileOnly("com.tavillecode:centaurus:1.0")
}

tasks.assemble {
    dependsOn(tasks.reobfJar)
}