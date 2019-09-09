
package endpoints

import helpers.IntegrationBaseSpec
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import stubs.AuthStub

class GetRequestEndPointSpec extends IntegrationBaseSpec {

  val path = "/data/123456789/testKey"
  val invalidPath = "/data/123456789/testKey2"

  "GET /data/:vrn/:key" when {

    "the user is authorised" when {

      s"mongo successful receives the data" should {

        s"get a 200 response back " in {

          AuthStub.authorised()
          val newRecord :WSResponse = put(path)("forename" -> "de")
          val response: WSResponse = get(path)
          response.status shouldBe 200

        }

        s"get the correct JSON back " in {

          AuthStub.authorised()
          val response = get("testData, testVrn, testKey")
          response.body should not be ""

        }
      }
      s"Going to the url $invalidPath and trying to access a non-existing record" should {

        s"get a 404 response back " in {

          val response: WSResponse = get(invalidPath)
          response.status shouldBe 404

        }

        s"get a error message for No data found " in {

          val response: WSResponse = get(invalidPath)
          response.body shouldBe "{\"message\":\"No data found for vrn: 123456789 and key: testKey2\"}"

        }

      }
    }

      s"Going to the url $path and not being authorized" should {

        s"get a 401 response back " in {

          AuthStub.unauthenticated()
          val response: WSResponse = get(path)
          response.status shouldBe 401

        }

        s"get no body returned" in {

          AuthStub.unauthenticated()
          val response: WSResponse = get(path)
          response.body shouldBe ""

        }

      }

      s"Going to the url $path and trying to access forbidden data " should {

        s"get a 403 response back " in {

          AuthStub.forbidden()
          val response: WSResponse = get(path)
          response.status shouldBe 403

        }

        s"get no body returned " in {

          AuthStub.forbidden()
          val response: WSResponse = get(path)
          response.body shouldBe ""

        }

      }

    //
    //  s"Going to the url /$testData/$testVrn/$testKey and a down streaming issue happens " should {
    //
    //    s"get a 500 response back " in {
    //
    //      val response: WSResponse = await(request(testData, "", "").get())
    //      response.status shouldBe 500
    //
    //    }
    //
    //    s"get the error message back " in {
    //
    //      val response: WSResponse = await(request(testData, "", "").get())
    //      response.body.contains("Error Message") shouldBe true
    //
    //    }
    //
    //  }
  }
}
