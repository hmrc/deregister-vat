/*
 * Copyright 2019 HM Revenue & Customs
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

package endpoints

import helpers.IntegrationBaseSpec
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import stubs.AuthStub

class GetDataSpec extends IntegrationBaseSpec {

  val path = "/data/123456789/testKey"
  val invalidPath = "/data/12345678901/testKey2"

  val body: JsValue = Json.parse("""{"forename" : "john"}""")

  "GET /data/:vrn/:key" when {

    "the user is authorised" when {

      "mongo successful receives the data" should {

        "get a 200 response and the correct JSON body" in {

          AuthStub.authorised()
          put(path)(body)
          val response: WSResponse = get(path)
          response.status shouldBe 200
          response.body shouldBe "{\"forename\":\"john\"}"

        }
      }
      "user is unsuccessful" should {

        "get a 404 response and the correct error message" in {

          val response: WSResponse = get(invalidPath)
          response.status shouldBe 404
          response.body shouldBe "{\"message\":\"No data found for vrn: 12345678901 and key: testKey2\"}"

        }
      }
    }

    "user is unauthorised" should {

      "get a 401 response and no body is returned" in {

        AuthStub.unauthenticated()
        val response: WSResponse = get(path)
        response.status shouldBe 401
        response.body shouldBe ""

      }

    }

    "user is trying to access forbidden data " should {

      "get a 403 response and no body is returned" in {

        AuthStub.forbidden()
        val response: WSResponse = get(path)
        response.status shouldBe 403
        response.body shouldBe ""

      }

    }
  }
}
