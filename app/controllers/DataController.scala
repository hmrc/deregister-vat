/*
 * Copyright 2018 HM Revenue & Customs
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

package controllers

import controllers.actions.VatAuthorised
import javax.inject.{Inject, Singleton}
import models.requests.User
import models.responses.ErrorModel
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import repositories.models.{MongoError, MongoSuccess}
import services.DataService
import uk.gov.hmrc.play.bootstrap.controller.BaseController

import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class DataController @Inject()(VatAuthorised: VatAuthorised, dataService: DataService)
                              (implicit ec: ExecutionContext) extends BaseController {

  private def parseJson(implicit user: User[_]): Either[ErrorModel, JsValue] = user.body match {
    case body: AnyContentAsJson => Right(body.json)
    case _ =>
      Logger.warn("[DataRepositoryController][parseJson] Body of request was not JSON")
      Left(ErrorModel("Body of request did not contain valid JSON"))
  }

  def storeData(vrn: String, key: String): Action[AnyContent] = VatAuthorised.async(vrn) {
    implicit user => parseJson match {
      case Left(err) =>
        Logger.debug("[DataRepositoryController][storeData] Error returned from parseJson")
        Future.successful(BadRequest(Json.toJson(err)))
      case Right(data) =>
        Logger.debug("[DataRepositoryController][storeData] Data parsed successfully, attempting to insert to Mongo")
        dataService.update(vrn, key, data) map {
          case MongoSuccess => NoContent
          case MongoError(err) => {
            Logger.debug(s"[DataRepositoryController][storeData] Error Returned from Mongo: $err")
            InternalServerError(Json.toJson(ErrorModel("Error when adding data to Mongo Repository")))
          }
        }
    }
  }

  def getData(vrn: String, key: String): Action[AnyContent] = VatAuthorised.async(vrn) {
    implicit user =>
      Logger.debug(s"[DataRepositoryController][getData] Attempting to retrieve data for vrn: $vrn and key: $key")
      dataService.getData(vrn, key).map {
        case Some(model) =>
          Logger.debug(s"[DataRepositoryController][getData] Successfully retrieved data: ${Json.toJson(model)}")
          Ok(model.data)
        case _ =>
          Logger.debug(s"[DataRepositoryController][getData] No data found for vrn: $vrn and key: $key")
          NotFound(Json.toJson(ErrorModel(s"No data found for vrn: $vrn and key: $key")))
      }
  }

  def removeData(vrn: String, key: String): Action[AnyContent] = VatAuthorised.async(vrn) {
    implicit user =>
      Logger.debug(s"[DataRepositoryController][removeData] Attempting to delete data for vrn: $vrn and key: $key")
      dataService.removeData(vrn, key) map {
        case MongoSuccess => NoContent
        case MongoError(err) =>
          Logger.debug(s"[DataRepositoryController][removeData] Error Returned from Mongo: $err")
          InternalServerError(Json.toJson(ErrorModel(s"Error when removing data for vrn: $vrn and key: $key")))
      }
  }

  def removeAll(vrn: String): Action[AnyContent] = VatAuthorised.async(vrn) {
    implicit user =>
      Logger.debug(s"[DataRepositoryController][removeAll] Attempting to delete all data for vrn: $vrn")
      dataService.removeAll(vrn) map {
        case MongoSuccess => NoContent
        case MongoError(err) =>
          Logger.debug(s"[DataRepositoryController][removeAll] Error Returned from Mongo: $err")
          InternalServerError(Json.toJson(ErrorModel(s"Error when removing all data for vrn: $vrn")))
      }
  }
}
