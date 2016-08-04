name := """creditperfection"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "mysql" % "mysql-connector-java" % "5.1.18",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "javax.mail" % "mail" % "1.5.0-b01",
  "net.authorize" % "anet-java-sdk" % "1.8.8",
  "be.objectify" %% "deadbolt-java" % "2.5.0",
  "org.mockito" % "mockito-core" % "1.9.5" % "test",
  "com.typesafe.play" %% "play-test" % "2.5.0",
  "org.hamcrest" % "hamcrest-library" % "1.3"
)

// Java project. Don't expect Scala IDE
 EclipseKeys.projectFlavor := EclipseProjectFlavor.Java           
 // Use .class files instead of generated .scala files for views and routes
 EclipseKeys.createSrc := EclipseCreateSrc.ValueSet(EclipseCreateSrc.ManagedClasses, EclipseCreateSrc.ManagedResources)
 // Compile the project before generating Eclipse files, so that .class files for views and routes are present   
 EclipseKeys.preTasks := Seq(compile in Compile)                  



fork in run := true