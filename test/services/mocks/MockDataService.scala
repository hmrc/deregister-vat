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

package services.mocks

import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import org.mongodb.scala.result.{DeleteResult, UpdateResult}
import play.api.libs.json.JsValue
import repositories.models.DataModel
import services.DataService
import testUtils.TestSupport

import scala.concurrent.Future

trait MockDataService extends TestSupport {

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDataService)
  }

  val mockDataService: DataService = mock[DataService]

  def mockAddEntry(vrn: String, key: String, json: JsValue)(response: UpdateResult): OngoingStubbing[Future[UpdateResult]] =
    when(mockDataService.update(ArgumentMatchers.eq(vrn),ArgumentMatchers.eq(key),ArgumentMatchers.eq(json)))
      .thenReturn(Future.successful(response))

  def mockRemoveData(vrn: String, key: String)(response: DeleteResult): OngoingStubbing[Future[DeleteResult]] =
    when(mockDataService.removeData(ArgumentMatchers.eq(vrn), ArgumentMatchers.eq(key)))
      .thenReturn(Future.successful(response))

  def mockGetData(vrn: String, key: String)(response: Option[DataModel]): OngoingStubbing[Future[Option[DataModel]]] =
    when(mockDataService.getData(ArgumentMatchers.eq(vrn), ArgumentMatchers.eq(key)))
      .thenReturn(Future.successful(response))

  def mockRemoveAll(vrn: String)(response: DeleteResult): OngoingStubbing[Future[DeleteResult]] =
    when(mockDataService.removeAll(ArgumentMatchers.eq(vrn)))
      .thenReturn(Future.successful(response))
}
