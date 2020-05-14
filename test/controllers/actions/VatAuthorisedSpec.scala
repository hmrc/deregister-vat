/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers.actions

import assets.BaseTestConstants.testVatNumber
import config.Constants
import connectors.mocks.MockAuthConnector
import play.api.http.Status._
import play.api.mvc.{ControllerComponents, Result}
import play.api.mvc.Results._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.{Enrolment, InsufficientEnrolments, MissingBearerToken}
import play.api.test.Helpers.stubControllerComponents

import scala.concurrent.Future


class VatAuthorisedSpec extends MockAuthConnector {

  val cc: ControllerComponents = stubControllerComponents()
  object TestVatAuthorised extends VatAuthorised(mockAuthConnector, cc)

  def result: Future[Result] = TestVatAuthorised.async(testVatNumber) {
    implicit user =>
      Future.successful(Ok)
  }(ec)(fakeRequest)

  val authPredicate: Predicate =
    Enrolment(Constants.MtdVatEnrolmentKey)
      .withIdentifier(Constants.MtdVatReferenceKey, testVatNumber)
      .withDelegatedAuthRule(Constants.MtdVatDelegatedAuth)

  "The VatAuthorised.async method" should {

    "For a Principal User" when {

      "an authorised result is returned from the Auth Connector" should {

        "Successfully authenticate and process the request" in {
          mockAuthRetrieveMtdVatEnrolled(authPredicate)
          status(result) shouldBe OK
        }
      }
    }

    "For an Agent User" when {

      "they are Signed Up to MTD VAT" should {

        "Successfully authenticate and process the request" in {
          mockAuthRetrieveAgentServicesEnrolled(authPredicate)
          status(result) shouldBe OK
        }
      }
      "the Credentials.providerId cannot be retrieved from the auth profile" should {
        "return Forbidden" in {
          mockAuthRetrieveCredentialsNone(authPredicate)
          status(result) shouldBe FORBIDDEN
        }
      }
    }

    "For any type of user" when {

      "a NoActiveSession exception is returned from the Auth Connector" should {

        "Return a unauthorised response" in {
          mockAuthorise(authPredicate, retrievals)(Future.failed(MissingBearerToken()))
          status(result) shouldBe UNAUTHORIZED
        }
      }

      "an InsufficientAuthority exception is returned from the Auth Connector" should {

        "Return a forbidden response" in {
          mockAuthorise(authPredicate, retrievals)(Future.failed(InsufficientEnrolments()))
          status(result) shouldBe FORBIDDEN
        }
      }
    }
  }
}
