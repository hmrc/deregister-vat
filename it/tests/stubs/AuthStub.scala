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

package tests.stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status.{OK, UNAUTHORIZED}
import play.api.libs.json.{JsObject, Json}
import tests.helpers.WireMockMethods

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

  def unauthenticated(): StubMapping = {
    when(method = POST, uri = authoriseUri)
      .thenReturn(
        status = UNAUTHORIZED,
        headers = Map("WWW-Authenticate" -> """MDTP detail="MissingBearerToken"""")
      )
  }

  def forbidden(): StubMapping = {
    when(method = POST, uri = authoriseUri)
      .thenReturn(
        status = UNAUTHORIZED,
        headers = Map("WWW-Authenticate" -> """MDTP detail="InsufficientEnrolments"""")
      )
  }

  private def authResponse(enrolments: JsObject*): JsObject = {
    Json.obj(
      "allEnrolments" -> enrolments,
      "optionalCredentials" -> Json.obj(
        "providerId" -> "12345",
        "providerType" -> "abcd"
      )
    )
  }
}
