/*
 * Copyright 2021 HM Revenue & Customs
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

package repositories

import config.AppConfig
import javax.inject.{Inject, Singleton}
import play.api.libs.json.{Format, Json}
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.commands.UpdateWriteResult
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import repositories.models._
import reactivemongo.play.json._
import uk.gov.hmrc.mongo.ReactiveRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DataRepository @Inject()(mongo: ReactiveMongoComponent,
                               appConfig: AppConfig)(implicit ec: ExecutionContext)
  extends ReactiveRepository[DataModel, IdModel](
    "deregData",
    mongo.mongoConnector.db,
    implicitly[Format[DataModel]],
    implicitly[Format[IdModel]]
  ) {

  val creationTimestampKey = "creationTimestamp"

  private lazy val ttlIndex = Index(
    Seq((creationTimestampKey, IndexType.Ascending)),
    name = Some("deregDataExpires"),
    unique = false,
    background = false,
    dropDups = false,
    sparse = false,
    version = None,
    options = BSONDocument("expireAfterSeconds" -> appConfig.timeToLiveSeconds)
  )

  private def setIndex(): Unit = {
    collection.indexesManager.drop(ttlIndex.name.get) onComplete {
      _ => collection.indexesManager.ensure(ttlIndex)
    }
  }

  setIndex()

  override def drop(implicit ec: ExecutionContext): Future[Boolean] =
    collection.drop(failIfNotFound = false).map { r =>
      setIndex()
      r
    }

  def upsert(data: DataModel): Future[UpdateWriteResult] = {
    val selector = Json.obj(DataModel._id -> data._id)
    collection.update(ordered = false).one(selector, data, upsert = true)
  }

}
