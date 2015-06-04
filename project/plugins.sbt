resolvers += "Era7 maven releases" at "https://s3-eu-west-1.amazonaws.com/releases.era7.com"

//addSbtPlugin("ohnosequences" % "nice-sbt-settings" % "0.5.1")

// resolvers += "Era7 maven snapshots" at "https://s3-eu-west-1.amazonaws.com/snapshots.era7.com"

// addSbtPlugin("ohnosequences" % "sbt-github-release" % "0.1.2-SNAPSHOT")

resolvers += "Github-API" at "http://repo.jenkins-ci.org/public/"

addSbtPlugin("ohnosequences" % "sbt-github-release" % "0.2.1")

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.2.1")
