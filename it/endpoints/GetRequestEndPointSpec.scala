
package endpoints

import BaseSpec.IntegrationBaseSpec
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

class GetRequestEndPointSpec extends IntegrationBaseSpec {

  val testData: String = "testData"

  private trait test {
    def request(testData: String, testVrn: String, testKey: String): WSRequest = {
      //setupStubs()
      buildRequest(s"/$testData/$testVrn/$testKey")
    }
  }


  s"Going to the url /$testData/$testVrn/$testKey and providing a key and vrn" should {

    s"get a 200 response back " in new test{

      val response: WSResponse = await(request(testData, testVrn, testKey).get())
      response.status shouldBe 200

    }

    s"get the correct JSON back " in new test{

      val response: WSResponse = await(request(testData, testVrn, testKey).get())
      response.body shouldBe "testDataReturned"

    }

  }

  s"Going to the url /$testData/$testVrn/$testKey and not being authorized" should {

    s"get a 401 response back " in new test{

      val response: WSResponse = await(request(testData, "", "").get())
      response.status shouldBe 401

    }

    s"get no body returned" in new test{

      val response: WSResponse = await(request(testData, testVrn, testKey).get())
      response.body shouldBe ""

    }

  }

  s"Going to the url /$testData/$testVrn/$testKey and trying to access forbidden data " should {

    s"get a 403 response back " in new test{

      val response: WSResponse = await(request(testData, "", "").get())
      response.status shouldBe 403

    }

    s"get no body returned " in new test{

      val response: WSResponse = await(request(testData, "", "").get())
      response.body shouldBe ""

    }

  }

  s"Going to the url /$testData/$testVrn/$testKey and trying to access a non-existing record" should {

    s"get a 404 response back " in new test {

      val response: WSResponse = await(request(testData, "", "").get())
      response.status shouldBe 404

    }

    s"get a error message for No data found " in new test{

      val response: WSResponse = await(request(testData, "", "").get())
      response.body shouldBe s"No data found for vrn: $testVrn and key: $testKey"

    }

  }

  s"Going to the url /$testData/$testVrn/$testKey and a down streaming issue happens " should {

    s"get a 500 response back " in new test{

      val response: WSResponse = await(request(testData, "", "").get())
      response.status shouldBe 500

    }

    s"get the error message back " in new test{

      val response: WSResponse = await(request(testData, "", "").get())
      response.body.contains("Error Message") shouldBe true

    }

  }

}
