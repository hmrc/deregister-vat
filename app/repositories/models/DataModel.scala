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

package repositories.models

import java.time.Instant

import play.api.libs.json._

case class DataModel(_id: IdModel, data: JsValue)

object DataModel {

  val _id = "_id"
  val data = "data"
  val creationTimestamp = "creationTimestamp"

  implicit val writes: OWrites[DataModel] = OWrites { model =>
    Json.obj(
      _id -> model._id,
      data -> model.data,
      creationTimestamp -> Json.obj("$date" -> Instant.now.toEpochMilli)
    )
  }
  implicit val reads: Reads[DataModel] = Json.reads[DataModel]
}
