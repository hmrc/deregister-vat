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

package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WireMockMethods
import play.api.http.Status.OK
import play.api.libs.json.{JsObject, Json}

object AuthStub extends WireMockMethods {

  private val authoriseUri = "/auth/authorise"

  private val mtdVatEnrolment = Json.obj(
    "key" -> "HMRC-MTD-VAT",
    "identifiers" -> Json.arr(
      Json.obj(
        "key" -> "VRN",
        "value" -> "123456789"
      )
    )
  )

  def authorised(): StubMapping = {
    when(method = POST, uri = authoriseUri)
      .thenReturn(
        status = OK,
        body = authResponse(mtdVatEnrolment)
      )
  }

  private def authResponse(enrolments: JsObject*): JsObject = {
    Json.obj(
      "allEnrolments" -> enrolments,
      "credentials" -> Json.obj(
        "providerId" -> "12345",
        "providerType" -> "abcd"
      )
    )
  }
}
