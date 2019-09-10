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
import play.api.libs.ws.WSResponse
import stubs.AuthStub

class StoreDataSpec extends IntegrationBaseSpec {

  val path = "/data/123456789/testKey"
  val invalidPath = "/data/123456789/testKey2"

  val body: JsValue = Json.parse("""{"forename" : "john"}""")

  "PUT /data/:vrn/:key" when {

    "the user is authorised" when {

      "mongo successful receives the sent data" should {

        "get a 204 response and no body is returned" in {

          AuthStub.authorised()
          val response: WSResponse = put(path)(body)
          response.status shouldBe 204
          response.body shouldBe ""

        }

      }
      "user is trying to add a currently existing record" should {

        "get a 204 response and no body returned" in {

          val response: WSResponse = put(invalidPath)(body)
          response.status shouldBe 204
          response.body.toString shouldBe ""

        }
      }
    }

    "user is unauthorised" should {

      "put a 401 response and no body is returned " in {

        AuthStub.unauthenticated()
        val response: WSResponse = put(path)(body)
        response.status shouldBe 401
        response.body shouldBe ""

      }

    }

    "user is trying to access forbidden data " should {

      "put a 403 response and no body is returned" in {

        AuthStub.forbidden()
        val response: WSResponse = put(path)(body)
        response.status shouldBe 403
        response.body shouldBe ""

      }
    }
  }
}
