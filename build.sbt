//Nice.scalaProject

name := "sbt-github-release"
organization := "ohnosequences"
description := "sbt plugin using github releases api"

sbtPlugin := true
scalaVersion := "2.10.5"
//bucketSuffix := "era7.com"

resolvers += "Github-API" at "http://repo.jenkins-ci.org/public/"
libraryDependencies += "org.kohsuke" % "github-api" % "1.49"

// libraryDependencies += "com.github.xuwei-k" %% "ghscala" % "0.2.14"




publishMavenStyle := true

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/Kevin-Lee/sbt-github-release</url>
    <licenses>
      <license>
        <name>The MIT License</name>
        <url>https://github.com/Kevin-Lee/sbt-github-release/blob/master/LICENSE</url>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:Kevin-Lee/sbt-github-release.git</url>
      <connection>scm:git:git@github.com:Kevin-Lee/sbt-github-release.git</connection>
    </scm>)

import bintray.Keys._
import ohnosequences.sbt.SbtGithubReleasePlugin._
import org.apache.commons.io.filefilter.WildcardFileFilter

licenses += ("AGPL-V3", url("http://www.gnu.org/licenses/agpl-3.0.en.html"))

Seq(bintrayPublishSettings:_*)

repository in bintray := "maven"


val projectVersion = "0.2.2"

lazy val writeVersion = inputKey[Unit]("Write Version in File'")

writeVersion := {
  println("\n== Writing Version File ==")
  val args: Seq[String] = Def.spaceDelimited("filename").parsed
  println(s"The project version is ${projectVersion}.")

  import IO._

  val filename = args.headOption.map("target/" + _).getOrElse("target/version.sbt")
  val versionFile = new File(filename)
  println(s"write ${projectVersion} into the file: $versionFile")

  write(versionFile, projectVersion, utf8, false)
  println("Done: Writing Version File\n")
}

def wildcardFilter(name: String): java.io.FileFilter = new WildcardFileFilter(name).asInstanceOf[java.io.FileFilter]

def getAllSubDirs(dir: File): Array[File] = dir.listFiles(DirectoryFilter).flatMap(x => x +: getAllSubDirs(x))

def fileList(dir: File, name: String): List[File] = {
  def fileList0(dir: File, name: String): List[File] = dir.listFiles(wildcardFilter(name)).toList
  (dir :: getAllSubDirs(dir).toList).flatMap(fileList0(_, name))
}
def pathNameAndFileList(base: File, dir: String, name: String): List[(File, String)] = {
  val basePath = base.getPath
  val basePathLength = basePath.length + (if (basePath.endsWith(java.io.File.separator)) 0 else 1)
  println(
    s"""
       |basePath: $basePath
       |basePathLength: $basePathLength
     """.stripMargin)
  fileList(base / dir, name).map(f => (f, f.getPath)).map { case (file, parent) => (file, parent.drop(basePathLength)) }
}

/* GitHub Release { */
GithubRelease.repo := "Kevin-Lee/sbt-github-release"

GithubRelease.tag := s"v${projectVersion}"

GithubRelease.releaseName := GithubRelease.tag.value

GithubRelease.commitish := "release"

GithubRelease.notesFile := GithubRelease.notesDir.value / s"${projectVersion}.markdown"

GithubRelease.assets := {

  val binNames = fileList(target.value / "ci", "*.jar")

  println(s"files to release: $binNames")

  binNames
}
/* } GitHub Release */
