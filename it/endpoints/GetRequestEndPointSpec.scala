
package endpoints

import BaseSpec.IntegrationBaseSpec
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

class GetRequestEndPointSpec extends IntegrationBaseSpec{

  val testData :String = "testData"

  private trait Test {
    //def setupStubs(): StubMapping

    def request(testData :String , testVrn : String , testKey : String): WSRequest = {
      //setupStubs()
      buildRequest(s"/$testData/$testVrn/$testKey")
    }
  }

  s"Going to the url /$testData/$testVrn/$testKey and providing a key and vrn" should {

    s"get a 200 response back when going to the url /$testData/$testVrn/$testKey" in new Test {

      val response: WSResponse = await(request(testData,testVrn,testKey).get())
      response.status shouldBe 200

    }

  }

  s"Going to the url /data/vrn/key with no vrn or key" should {

    s"get a 404 response back " in new Test {

      val response: WSResponse = await(request(testData,"","").get())
      response.status shouldBe 404
    }

  }
}
