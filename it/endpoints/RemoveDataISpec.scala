
package endpoints

import helpers.IntegrationBaseSpec
import play.api.libs.ws.WSResponse
import stubs.AuthStub

class RemoveDataISpec extends IntegrationBaseSpec {

  val path = "/data/123456789/testKey"

  "DELETE /data/:vrn/:key" when {

    "user is authorised" when {

      "mongo successfully deletes the data" should {

        "return 204 (No Content)" in {

          AuthStub.authorised()

          val response: WSResponse = delete(path)
          response.status shouldBe 204
        }
      }
    }
  }
}
