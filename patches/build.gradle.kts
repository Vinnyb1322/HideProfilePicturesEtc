group = "io.github.vinnyb1322"

patches {
    about {
        name = "Vince's Patches"
        description = "Unofficial Reddit patches for use with Morphe"
        source = "git@github.com:Vinnyb1322/HideProfilePicturesEtc.git"
        author = "Vinnyb1322"
        contact = "https://github.com/Vinnyb1322/HideProfilePicturesEtc/issues"
        website = "https://github.com/Vinnyb1322/HideProfilePicturesEtc"
        license = "GPLv3"
    }
}

// Separate configuration so gson is available at runtime for the
// generatePatchesList task but never bundled into the APK.
val patchListGeneratorClasspath = configurations.create("patchListGeneratorClasspath")

dependencies {
    compileOnly(libs.gson)
    patchListGeneratorClasspath(libs.gson)
}

tasks {
    register<JavaExec>("generatePatchesList") {
        description = "Build patch with patch list"

        dependsOn(build)

        classpath = sourceSets["main"].runtimeClasspath + patchListGeneratorClasspath
        mainClass.set("util.PatchListGeneratorKt")
    }

    // Used by gradle-semantic-release-plugin.
    publish {
        dependsOn("generatePatchesList")
    }
}
