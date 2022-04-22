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

package repositories

import java.util.concurrent.TimeUnit

import config.AppConfig
import javax.inject.{Inject, Singleton}
import org.mongodb.scala.model.Indexes.ascending
import org.mongodb.scala.model.{IndexModel, IndexOptions}
import play.api.libs.json.Format
import repositories.models._
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import scala.concurrent.ExecutionContext

@Singleton
class DataRepository @Inject()(mongo: MongoComponent,
                               appConfig: AppConfig)(implicit ec: ExecutionContext)
  extends PlayMongoRepository[DataModel](
    mongoComponent = mongo,
    collectionName = "deregData",
    domainFormat = implicitly[Format[DataModel]],
    indexes = Seq(
      IndexModel(
        ascending(DataModel.creationTimestamp),
        IndexOptions()
          .name("deregDataExpires")
          .expireAfter(appConfig.timeToLiveSeconds, TimeUnit.SECONDS)
          .unique(false)
          .background(false)
          .sparse(false)
      ),
      IndexModel(
        ascending(s"${DataModel._id}.${IdModel.vrn}"),
        IndexOptions().name("VRNIndex")
      )
    ),
    replaceIndexes = true
  ) {

  collection.createIndexes(indexes)
}
