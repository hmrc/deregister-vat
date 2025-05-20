/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import play.sbt.routes.RoutesKeys
import uk.gov.hmrc.DefaultBuildSettings.*
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

val appName: String = "deregister-vat"
val mongoPlayVersion = "2.6.0"
val bootstrapVersion = "8.6.0"
ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "2.13.16"

lazy val appDependencies: Seq[ModuleID] = compile ++ test()
lazy val plugins: Seq[Plugins] = Seq.empty
lazy val playSettings: Seq[Setting[?]] = Seq.empty

lazy val coverageSettings: Seq[Setting[?]] = {
  import scoverage.ScoverageKeys

  val excludedPackages = Seq(
    "<empty>",
    ".*Reverse.*",
    "app.*",
    "prod.*",
    "config.*",
  )

  Seq(
    ScoverageKeys.coverageExcludedPackages := excludedPackages.mkString(";"),
    ScoverageKeys.coverageMinimumStmtTotal := 95,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}

val compile = Seq(
  "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30" % mongoPlayVersion,
  ws,
  "uk.gov.hmrc" %% "bootstrap-backend-play-30" % bootstrapVersion
)

def test(scope: String = "test"): Seq[ModuleID] = Seq(
  "uk.gov.hmrc"             %% "bootstrap-test-play-30"     % bootstrapVersion    % scope,
  "org.scalatestplus"       %% "scalatestplus-mockito"      % "1.0.0-SNAP5"          % scope,
  "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-30"    % mongoPlayVersion    % scope
)

lazy val microservice = Project(appName, file("."))
  .enablePlugins((Seq(play.sbt.PlayScala, SbtDistributablesPlugin) ++ plugins) *)
  .settings(PlayKeys.playDefaultPort := 9164)
  .settings(coverageSettings *)
  .settings(playSettings *)
  .settings(scalaSettings *)
  .settings(defaultSettings() *)
  .settings(
    Test / Keys.fork := true,
    Test / javaOptions += "-Dlogger.resource=logback-test.xml",
    libraryDependencies ++= appDependencies,
    retrieveManaged := true,
    PlayKeys.playDefaultPort := 9164,
    RoutesKeys.routesImport := Seq.empty
  )
  .settings(scalacOptions ++= Seq("-Wconf:cat=unused-imports&src=routes/.*:s", "-Wconf:cat=unused&src=routes/.*:s"))

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(itSettings())
  .settings(
    fork := false,
    addTestReportOption(Test, "int-test-reports")
  )

