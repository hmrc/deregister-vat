/*
 * Copyright 2021 HM Revenue & Customs
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

import play.core.PlayVersion
import play.sbt.routes.RoutesKeys
import sbt.Tests.{Group, SubProcess}
import uk.gov.hmrc.DefaultBuildSettings._
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

val appName: String = "deregister-vat"

lazy val appDependencies: Seq[ModuleID] = compile ++ test()
lazy val plugins: Seq[Plugins] = Seq.empty
lazy val playSettings: Seq[Setting[_]] = Seq.empty

lazy val coverageSettings: Seq[Setting[_]] = {
  import scoverage.ScoverageKeys

  val excludedPackages = Seq(
    "<empty>",
    "Reverse.*",
    ".*standardError*.*",
    ".*govuk_wrapper*.*",
    ".*main_template*.*",
    "uk.gov.hmrc.BuildInfo",
    "app.*",
    "prod.*",
    "config.*",
    "testOnlyDoNotUseInAppConf.*",
    "partials.*",
    "com.kenshoo.play.metrics.*",
    "controllers.javascript",
    "controllers.ReverseDataController")

  Seq(
    ScoverageKeys.coverageExcludedPackages := excludedPackages.mkString(";"),
    ScoverageKeys.coverageMinimum := 90,
    ScoverageKeys.coverageFailOnMinimum := false,
    ScoverageKeys.coverageHighlighting := true
  )
}

val compile = Seq(
  "uk.gov.hmrc" %% "simple-reactivemongo" % "7.30.0-play-26",
  ws,
  "uk.gov.hmrc" %% "bootstrap-backend-play-26" % "5.3.0"
)

def test(scope: String = "test,it"): Seq[ModuleID] = Seq(
  "uk.gov.hmrc"             %% "hmrctest"           % "3.9.0-play-26"     % scope,
  "org.scalatest"           %% "scalatest"          % "3.0.8"             % scope,
  "org.pegdown"             % "pegdown"             % "1.6.0"             % scope,
  "org.scalatestplus.play"  %% "scalatestplus-play" % "3.1.3"             % scope,
  "com.typesafe.play"       %% "play-test"          % PlayVersion.current % scope,
  "org.mockito"             % "mockito-core"        % "2.9.0"             % scope,
  "com.github.tomakehurst"  % "wiremock-jre8"       % "2.26.3"            % scope
)

def oneForkedJvmPerTest(tests: Seq[TestDefinition]): Seq[Group] = tests map {
  test => Group(test.name, Seq(test), SubProcess(
    ForkOptions().withRunJVMOptions(Vector("-Dtest.name=" + test.name, "-Dlogger.resource=logback-test.xml"))
  ))
}

lazy val microservice = Project(appName, file("."))
  .enablePlugins(Seq(play.sbt.PlayScala, SbtDistributablesPlugin) ++ plugins: _*)
  .settings(PlayKeys.playDefaultPort := 9164)
  .settings(coverageSettings: _*)
  .settings(playSettings: _*)
  .settings(scalaSettings: _*)
  .settings(publishingSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(majorVersion := 0)
  .settings(
    Keys.fork in Test := true,
    javaOptions in Test += "-Dlogger.resource=logback-test.xml",
    scalaVersion := "2.12.11",
    libraryDependencies ++= appDependencies,
    retrieveManaged := true,
    PlayKeys.playDefaultPort := 9164,
    evictionWarningOptions in update := EvictionWarningOptions.default.withWarnScalaVersionEviction(false),
    RoutesKeys.routesImport := Seq.empty
  )
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    Keys.fork in IntegrationTest := false,
    unmanagedSourceDirectories in IntegrationTest := (baseDirectory in IntegrationTest) (base => Seq(base / "it")).value,
    resourceDirectory in IntegrationTest := baseDirectory.value / "it" / "resources",
    addTestReportOption(IntegrationTest, "int-test-reports"),
    testGrouping in IntegrationTest := oneForkedJvmPerTest((definedTests in IntegrationTest).value),
    parallelExecution in IntegrationTest := false
  )
