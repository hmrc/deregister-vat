
package endpoints

import controllers.actions.mocks.MockVatAuthorised
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.ws.{WSClient, WSRequest}
import services.mocks.MockDataService

case class IntegrationBaseSpec() extends WordSpec with Matchers with MockVatAuthorised with MockDataService {

  lazy val client: WSClient = app.injector.instanceOf[WSClient]


  val port :String = "9165"
  val appRouteContext: String = "/deregister-vat"

  def buildRequest(path: String): WSRequest = client.url(s"http://localhost:$port$appRouteContext$path").withFollowRedirects(false)
}
