/*
 * Copyright 2024 HM Revenue & Customs
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

import config.Constants
import javax.inject.{Inject, Singleton}
import models.requests.User
import play.api.mvc._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.auth.core.NoActiveSession
import utils.LoggerUtil
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatAuthorised @Inject()(val authConnector: AuthConnector,
                              cc: ControllerComponents) extends BackendController(cc) with AuthorisedFunctions with LoggerUtil {

  private def delegatedAuthRule(vrn: String): Enrolment =
    Enrolment(Constants.MtdVatEnrolmentKey)
      .withIdentifier(Constants.MtdVatReferenceKey, vrn)
      .withDelegatedAuthRule(Constants.MtdVatDelegatedAuth)

  private val arn: Enrolments => Option[String] = _.getEnrolment(Constants.AgentServicesEnrolment) flatMap {
    _.getIdentifier(Constants.AgentServicesReference).map(_.value)
  }

  def async(vrn: String)(f: User[_] => Future[Result])(implicit ec: ExecutionContext): Action[AnyContent] = Action.async {
    implicit request =>
      authorised(delegatedAuthRule(vrn)).retrieve(Retrievals.allEnrolments and Retrievals.credentials) {
        case enrolments ~ Some(credentials) =>
          f(User(vrn, arn(enrolments), credentials.providerId)(request))
        case _ =>
          logger.warn(s"[VatAuthorised][async] - Unable to retrieve Credentials.providerId from auth profile")
          Future(Forbidden)
      } recover {
        case _: NoActiveSession =>
          logger.debug(s"[VatAuthorised][async] - User has no active session, unauthorised")
          Unauthorized
        case _: AuthorisationException =>
          logger.debug(s"[VatAuthorised][async] - User has an active session, but does not have sufficient authority")
          Forbidden
      }
  }
}
