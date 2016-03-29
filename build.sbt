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
  "javax.mail" % "mail" % "1.5.0-b01"
)

// Java project. Don't expect Scala IDE
 EclipseKeys.projectFlavor := EclipseProjectFlavor.Java           
 // Use .class files instead of generated .scala files for views and routes
 EclipseKeys.createSrc := EclipseCreateSrc.ValueSet(EclipseCreateSrc.ManagedClasses, EclipseCreateSrc.ManagedResources)
 // Compile the project before generating Eclipse files, so that .class files for views and routes are present   
 EclipseKeys.preTasks := Seq(compile in Compile)                  

