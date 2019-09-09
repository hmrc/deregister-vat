
package endpoints

import com.sksamuel.scapegoat.inspections.string.StripMarginOnRegex
import controllers.DataController
import controllers.actions.mocks.MockVatAuthorised
import org.scalatest.{Matchers, WordSpec}
import play.api.http.Status
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import services.mocks.MockDataService
import uk.gov.hmrc.play.it.ServiceSpec

class GetRequestEndPointSpec extends IntegrationBaseSpec{

  val userVrn :String = "vrn"
  val userKey :String = "key"
  val userData :String = "testData"

  private trait Test {
    //def setupStubs(): StubMapping

    def request(userData :String , userVrn : String , userKey : String): WSRequest = {
      //setupStubs()
      buildRequest(s"/$userData/$userVrn/$userKey")
    }
  }

  s"Going to the url /$userData/$userVrn/$userKey " should {

    s"get a 200 response back when going to the url /$data/$userVrn/$userKey" in new Test {

      val response: WSResponse = await(request().get())
      response.status shouldBe 404

    }

  }

  s"Going to the url /data/vrn/key " should {

    s"get a 404 response back " in new Test {

      val response: WSResponse = await(request().get())
      response.status shouldBe 404

    }

  }
}
