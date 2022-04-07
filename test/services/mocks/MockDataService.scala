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

package services.mocks

import org.mockito.Matchers
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import play.api.libs.json.JsValue
import repositories.models.{DataModel, MongoResponse}
import services.DataService
import testUtils.TestSupport

import scala.concurrent.Future

trait MockDataService extends TestSupport {

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDataService)
  }

  val mockDataService: DataService = mock[DataService]

  def mockAddEntry(vrn: String, key: String, json: JsValue)(response: MongoResponse): OngoingStubbing[Future[MongoResponse]] =
    when(mockDataService.update(Matchers.eq(vrn),Matchers.eq(key),Matchers.eq(json)))
      .thenReturn(Future.successful(response))

  def mockRemoveData(vrn: String, key: String)(response: MongoResponse): OngoingStubbing[Future[MongoResponse]] =
    when(mockDataService.removeData(Matchers.eq(vrn), Matchers.eq(key)))
      .thenReturn(Future.successful(response))

  def mockGetData(vrn: String, key: String)(response: Option[DataModel]): OngoingStubbing[Future[Option[DataModel]]] =
    when(mockDataService.getData(Matchers.eq(vrn), Matchers.eq(key)))
      .thenReturn(Future.successful(response))

  def mockRemove(vrn: String)(response: MongoResponse): OngoingStubbing[Future[MongoResponse]] =
    when(mockDataService.removeAll(Matchers.eq(vrn)))
      .thenReturn(Future.successful(response))


}
