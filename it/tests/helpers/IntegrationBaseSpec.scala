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

package tests.helpers

import org.scalatest._
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.JsValue
import play.api.{Application, Environment, Mode}
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import org.scalatest.matchers.should.Matchers
import play.api.test.Helpers.{await, defaultAwaitTimeout}

trait IntegrationBaseSpec extends AnyWordSpecLike
  with WireMockHelper
  with Matchers
  with GuiceOneServerPerSuite
  with TestSuite
  with BeforeAndAfterEach
  with BeforeAndAfterAll
  with GivenWhenThen {

  val mockHost: String = WireMockHelper.host
  val mockPort: String = WireMockHelper.wmPort.toString
  val appRouteContext: String = "/deregister-vat"

  override lazy val client: WSClient = app.injector.instanceOf[WSClient]

  def servicesConfig: Map[String, String] = Map()

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(servicesConfig)
    .build()

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWireMock()
  }

  override def afterAll(): Unit = {
    stopWireMock()
    super.afterAll()
  }

  def get(path: String): WSResponse = await(
    buildRequest(path).get()
  )

  def put(path: String)(body: JsValue): WSResponse = await(
    buildRequest(path).put(body)
  )

  def delete(path: String): WSResponse = await(
    buildRequest(path).delete()
  )

  def buildRequest(path: String): WSRequest =
    client.url(s"http://localhost:$port$appRouteContext$path")
      .withHttpHeaders("Authorization" -> "localToken").withFollowRedirects(false)
}
