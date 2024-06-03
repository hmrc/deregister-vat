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

package services

import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.ReplaceOptions
import org.mongodb.scala.result.{DeleteResult, UpdateResult}
import play.api.libs.json.JsValue
import repositories.DataRepository
import repositories.models._
import uk.gov.hmrc.mongo.play.json.Codecs

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton()
class DataService @Inject()(dataRepository: DataRepository) {

  def update(vrn: String, key: String, data: JsValue): Future[UpdateResult] = {
    val document = DataModel(IdModel(vrn, key), data)
    val filter = equal(DataModel._id, Codecs.toBson(document._id))
    dataRepository.collection.replaceOne(filter, document, ReplaceOptions().upsert(true)).toFuture()
  }

  def removeData(vrn: String, key: String): Future[DeleteResult] =
    dataRepository.collection.deleteOne(
      equal(DataModel._id, Codecs.toBson(IdModel(vrn, key)))
    ).toFuture()

  def removeAll(vrn: String): Future[DeleteResult] =
    dataRepository.collection.deleteMany(
      equal(s"${DataModel._id}.${IdModel.vrn}", vrn)
    ).toFuture()

  def getData(vrn: String, key: String): Future[Option[DataModel]] =
    dataRepository.collection.find(
      equal(DataModel._id, Codecs.toBson(IdModel(vrn, key)))
    ).first().toFutureOption()
}
