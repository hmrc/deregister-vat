/*
 * Copyright 2022 HM Revenue & Customs
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

package connectors.mocks

import assets.BaseTestConstants._
import org.mockito.Matchers
import org.mockito.Mockito._
import testUtils.TestSupport
import uk.gov.hmrc.auth.core.authorise.{EmptyPredicate, Predicate}
import uk.gov.hmrc.auth.core.retrieve.{Credentials, EmptyRetrieval, Retrieval, ~}
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.{AuthConnector, Enrolments}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockAuthConnector extends TestSupport {

  val mockAuthConnector: AuthConnector = mock[AuthConnector]

  def mockAuthorise[T](predicate: Predicate = EmptyPredicate,
                       retrievals: Retrieval[T] = EmptyRetrieval
                      )(response: Future[T]): Unit = {
    when(
      mockAuthConnector.authorise(
        Matchers.eq(predicate),
        Matchers.eq(retrievals)
      )(
        Matchers.any[HeaderCarrier],
        Matchers.any[ExecutionContext])
    ) thenReturn response
  }

  val retrievals: Retrieval[Enrolments ~ Option[Credentials]] = Retrievals.allEnrolments and Retrievals.credentials

  def mockAuthRetrieveAgentServicesEnrolled(predicate: Predicate = EmptyPredicate): Unit =
    mockAuthorise(predicate, retrievals)(
      Future.successful(
        new ~(Enrolments(Set(testAgentServicesEnrolment)), Some(testCredentials))
      )
    )

  def mockAuthRetrieveMtdVatEnrolled(predicate: Predicate = EmptyPredicate): Unit =
    mockAuthorise(predicate = predicate, retrievals = retrievals)(
      Future.successful(
        new ~(Enrolments(Set(testMtdVatEnrolment)), Some(testCredentials))
      )
    )

  def mockAuthRetrieveCredentialsNone(predicate: Predicate = EmptyPredicate): Unit =
    mockAuthorise(predicate = predicate, retrievals = retrievals)(
      Future.successful(
        new ~ (Enrolments(Set(testMtdVatEnrolment)), None)
      )
    )

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockAuthConnector)
  }

}
