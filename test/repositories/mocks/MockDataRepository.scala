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

package repositories.mocks

import com.mongodb.client.result.{DeleteResult, UpdateResult}
import org.mongodb.scala.bson.BsonObjectId
import org.mongodb.scala.result.{DeleteResult, UpdateResult}
import testUtils.TestSupport

trait MockDataRepository extends TestSupport {

  val successWriteResult: UpdateResult = UpdateResult.acknowledged(1, 1, BsonObjectId())
  val successDeleteResult: DeleteResult = DeleteResult.acknowledged(1)
  val errorWriteResult: UpdateResult = UpdateResult.unacknowledged()
  val errorDeleteResult: DeleteResult = DeleteResult.unacknowledged()

}
