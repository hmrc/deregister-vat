/*
 * Copyright 2023 HM Revenue & Customs
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

package tests.endpoints

import play.api.libs.ws.WSResponse
import tests.helpers.IntegrationBaseSpec
import tests.stubs.AuthStub

class RemoveAllDataISpec extends IntegrationBaseSpec {

  val path = "/data/123456789"

  "DELETE /data/:vrn" when {

    "user is authorised" when {

      "mongo successfully deletes the data" should {

        "return 204 (No Content)" in {

          AuthStub.authorised()

          val response: WSResponse = delete(path)
          response.status shouldBe 204
        }
      }
    }

    "user is unauthenticated" should {

      "return 401 (Unauthorized)" in {

        AuthStub.unauthenticated()

        val response: WSResponse = delete(path)
        response.status shouldBe 401
      }
    }

    "user is forbidden" should {

      "return 403 (Forbidden)" in {

        AuthStub.forbidden()

        val response: WSResponse = delete(path)
        response.status shouldBe 403
      }
    }
  }
}
