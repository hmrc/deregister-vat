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
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.result.{DeleteResult, UpdateResult}
import play.api.libs.json.JsValue
import repositories.DataRepository
import repositories.models._
import uk.gov.hmrc.mongo.play.json.Codecs

import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class DataService @Inject()(dataRepository: DataRepository)(implicit ec: ExecutionContext) {

  private def handleUpdateResult: Future[UpdateResult] => Future[MongoResponse] = _ map {
    case r if r.wasAcknowledged => MongoSuccess
    case _ => MongoError("Update was unacknowledged")
  } recover {
    case err => MongoError(err.getMessage)
  }

  private def handleDeleteResult: Future[DeleteResult] => Future[MongoResponse] = _ map {
    case r if r.getDeletedCount > 0 => MongoSuccess
    case _ => MongoError("Failed to delete")
  } recover {
    case err => MongoError(err.getMessage)
  }

  def update(vrn: String, key: String, data: JsValue): Future[MongoResponse] = {
    val document = DataModel(IdModel(vrn, key), data)
    handleUpdateResult(dataRepository.upsert(document))
  }

  def removeData(vrn: String, key: String): Future[MongoResponse] = {
    handleDeleteResult(dataRepository.collection.deleteOne(
      equal(DataModel._id, Codecs.toBson(IdModel(vrn, key)))
    ).toFuture())
  }

  def removeAll(vrn: String): Future[MongoResponse] = {
    handleDeleteResult(dataRepository.collection.deleteMany(
      equal(DataModel._id,equal(IdModel.vrn, vrn))).toFuture()
    )
  }

  def getData(vrn: String, key: String): Future[Option[DataModel]] = {
    dataRepository.collection.find(
      equal(DataModel._id, Codecs.toBson(IdModel(vrn, key)))
    ).first().toFutureOption()
  }
}
