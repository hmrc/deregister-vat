
package endpoints

import helpers.IntegrationBaseSpec
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSResponse
import stubs.AuthStub

class StoreDataSpec extends IntegrationBaseSpec {

  val path = "/data/123456789/testKey"
  val invalidPath = "/data/123456789/testKey2"

  val body :JsValue = Json.parse("""{"forename" : "john"}""")

  "PUT /data/:vrn/:key" when {

    "the user is authorised" when {

      s"mongo successful receives the sent data" should {

        s"get a 204 response back " in {

          AuthStub.authorised()
          val response :WSResponse = put(path)(body)
          response.status shouldBe 204

        }

        s"get no content returned " in {

          AuthStub.authorised()
          val response = put(path)(body)
          response.body shouldBe ""

        }
      }
      s"Going to the url $invalidPath and trying to add a currently existing record" should {

        s"get a 204 response back " in {

          val response: WSResponse = put(invalidPath)(body)
          response.status shouldBe 204

        }

        s"get no content returned " in {

          val response: WSResponse = put(invalidPath)(body)
          response.body.toString shouldBe ""

        }

      }
    }

      s"Going to the url $path and not being authorized" should {

        s"put a 401 response back " in {

          AuthStub.unauthenticated()
          val response: WSResponse = put(path)(body)
          response.status shouldBe 401

        }

        s"get no body returned" in {

          AuthStub.unauthenticated()
          val response: WSResponse = put(path)(body)
          response.body shouldBe ""

        }

      }

      s"Going to the url $path and trying to access forbidden data " should {

        s"put a 403 response back " in {

          AuthStub.forbidden()
          val response: WSResponse = put(path)(body)
          response.status shouldBe 403

        }

        s"get no body returned " in {

          AuthStub.forbidden()
          val response: WSResponse = put(path)(body)
          response.body shouldBe ""

        }

      }

    //
    //  s"Going to the url /$testData/$testVrn/$testKey and a down streaming issue happens " should {
    //
    //    s"put a 500 response back " in {
    //
    //      val response: WSResponse = await(request(testData, "", "").put())
    //      response.status shouldBe 500
    //
    //    }
    //
    //    s"put the error message back " in {
    //
    //      val response: WSResponse = await(request(testData, "", "").put())
    //      response.body.contains("Error Message") shouldBe true
    //
    //    }
    //
    //  }
  }
}
