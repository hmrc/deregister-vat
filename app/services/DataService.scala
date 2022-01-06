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

package services

import javax.inject.{Inject, Singleton}
import play.api.libs.json.JsValue
import reactivemongo.api.commands.WriteResult
import repositories.DataRepository
import repositories.models._

import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class DataService @Inject()(dataRepository: DataRepository)(implicit ec: ExecutionContext) {

  private def handleResult: Future[WriteResult] => Future[MongoResponse] = _ map {
    _ => MongoSuccess
  } recover {
    case err => MongoError(err.getMessage)
  }

  def update(vrn: String, key: String, data: JsValue): Future[MongoResponse] = {
    val document = DataModel(IdModel(vrn, key), data)
    handleResult(dataRepository.upsert(document))
  }

  def removeData(vrn: String, key: String): Future[MongoResponse] =
    handleResult(dataRepository.removeById(IdModel(vrn, key)))

  def removeAll(vrn: String): Future[MongoResponse] = {
    handleResult(dataRepository.remove(s"${DataModel._id}.${IdModel.vrn}" -> vrn))
  }

  def getData(vrn: String, key: String): Future[Option[DataModel]] =
    dataRepository.findById(IdModel(vrn, key))
}
