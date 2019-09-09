
package helpers

import org.scalatest._
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import uk.gov.hmrc.play.test.UnitSpec

case class IntegrationBaseSpec() extends UnitSpec
  with WireMockHelper
  with Matchers
  with GuiceOneServerPerSuite
  with TestSuite
  with BeforeAndAfterEach
  with BeforeAndAfterAll
  with GivenWhenThen {

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

  def put(path: String)(body: Map[String, Seq[String]]): WSResponse = await(
    buildRequest(path).put(body)
  )

  def delete(path: String)(body: String): WSResponse = await(
    buildRequest(path).put(body)
  )

  def deleteAll(path: String) : WSResponse = await(
    buildRequest(path).put("")
  )


  lazy val testVrn = "testVrn"
  lazy val testKey = "forename"

  override lazy val client: WSClient = app.injector.instanceOf[WSClient]

  val testPort :String = "1111"
  val appRouteContext: String = "/deregister-vat"

  def buildRequest(path: String): WSRequest = client.url(s"http://localhost:$testPort$appRouteContext$path").withFollowRedirects(false)
}
