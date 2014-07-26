

import AssemblyKeys._


name := "scala_hbase_mapreduce"

version := "0.1.0"

scalaVersion := "2.11.1"


/* Turn on lint checking and other annoying warnings for the OCD among us */
scalacOptions ++= Seq("-Xlint", "-deprecation", "-feature", "-unchecked")


/* HBase dependencies */
resolvers += "Apache HBase" at "https://repository.apache.org/content/repositories/releases"

resolvers += "Thrift" at "http://people.apache.org/~rawson/repo/"

// Hbase 0.98 / Hadoop 2.4
//    munged from a bunch of crazy Maven optional dependencies but it works
libraryDependencies ++= Seq(
  "org.apache.hadoop" % "hadoop-common" % "2.4.0" exclude("javax.servlet.jsp", "jsp-api") exclude("com.sun.jersey", "jersey-server") exclude("javax.servlet", "servlet-api") exclude("tomcat", "jasper-compiler") exclude("tomcat", "jasper-runtime"),
  "org.apache.hadoop" % "hadoop-hdfs" % "2.4.0",
  "org.apache.hadoop" % "hadoop-auth" % "2.4.0",
  "org.apache.hadoop" % "hadoop-mapreduce-client-core" % "2.4.0" exclude("com.sun.jersey.jersey-test-framework",
  "jersey-test-framework-grizzly2"),
  "org.apache.hadoop" % "hadoop-annotations" % "2.4.0",
  "org.apache.hbase" % "hbase-server" % "0.98.1-hadoop2",
  "org.apache.hbase" % "hbase-common" % "0.98.1-hadoop2",
  "org.apache.hbase" % "hbase-protocol" % "0.98.1-hadoop2",
  "org.apache.hbase" % "hbase-client" % "0.98.1-hadoop2",
  "commons-codec" % "commons-codec" % "1.9",
  "commons-io" % "commons-io" % "2.4",
  "commons-lang" % "commons-lang" % "2.6",
  "commons-logging" % "commons-logging" % "1.1.3",
  "com.google.guava" % "guava" % "17.0",
  "com.google.protobuf" % "protobuf-java" % "2.5.0",
  "io.netty" % "netty" % "3.9.1.Final",
  "org.apache.zookeeper" % "zookeeper" % "3.4.6",
  "org.cloudera.htrace" % "htrace-core" % "2.05",
  "org.codehaus.jackson" % "jackson-mapper-asl" % "1.9.13"
)



/* Assembly settings to build fat jars */
assemblySettings

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case x if Assembly.isConfigFile(x) => MergeStrategy.concat
    case PathList(ps @ _*) if Assembly.isReadme(ps.last) || Assembly.isLicenseFile(ps.last) =>
      MergeStrategy.rename
    case PathList("META-INF", xs @ _*) =>
      (xs map {_.toLowerCase}) match {
        case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) =>
          MergeStrategy.discard
        case ps @ (x :: xs) if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") =>
          MergeStrategy.discard
        case "plexus" :: xs =>
          MergeStrategy.discard
        case "services" :: xs =>
          MergeStrategy.filterDistinctLines
        case ("spring.schemas" :: Nil) | ("spring.handlers" :: Nil) =>
          MergeStrategy.filterDistinctLines
        case _ => MergeStrategy.deduplicate
      }
    case _ => MergeStrategy.first
  }
}



