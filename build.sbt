name := "url-cache"

organization := "com.cldellow"

version := "0.0.1"

scalaVersion := "2.12.8"

//Define dependencies. These ones are only required for Test and Integration Test scopes.
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "org.scalacheck" %% "scalacheck" % "1.14.0" % "test",
  "net.openhft" % "zero-allocation-hashing" % "0.9",
  "commons-io" % "commons-io" % "2.6",
)

// For Settings/Task reference, see http://www.scala-sbt.org/release/sxr/sbt/Keys.scala.html

// Compiler settings. Use scalac -X for other options and their description.
// See Here for more info http://www.scala-lang.org/files/archive/nightly/docs/manual/html/scalac.html 
scalacOptions ++= List("-feature","-deprecation", "-unchecked", "-Xlint")

// ScalaTest settings.
// Ignore tests tagged as @Slow (they should be picked only by integration test)
testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-l", "org.scalatest.tags.Slow", "-u","target/junit-xml-reports", "-oD", "-eS")

useGpg := true

// needed so jft doesn't get loaded 2x in same jvm
//fork := true

//coverageEnabled := true

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false, includeDependency = true)

ThisBuild / organization := "com.cldellow"
ThisBuild / organizationName := "com.cldellow"
ThisBuild / organizationHomepage := Some(url("https://github.com/cldellow"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/cldellow/url-cache"),
    "scm:git@github.com:cldellow/url-cache.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id    = "cldellow",
    name  = "Colin Dellow",
    email = "cldellow@gmail.com",
    url   = url("https://cldellow.com")
  )
)

ThisBuild / description := "Fetch URLs, transparently caching them locally if needed."
ThisBuild / licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/cldellow/url-cache"))

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true
