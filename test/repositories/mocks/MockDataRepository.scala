/*
 * Copyright 2020 HM Revenue & Customs
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

import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import repositories.DataRepository
import repositories.models.{DataModel, IdModel}
import testUtils.TestSupport

import scala.concurrent.Future

trait MockDataRepository extends TestSupport {

  val successUpdateWriteResult: UpdateWriteResult = mock[UpdateWriteResult]
  val successWriteResult: WriteResult = mock[WriteResult]
  val errMsg = "Mongo Err"
  val errorResult = Future.failed(new Exception(errMsg))

  lazy val mockDataRepository: DataRepository = mock[DataRepository]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDataRepository)
  }

  def mockAddEntry(data: DataModel)(response: Future[UpdateWriteResult]): OngoingStubbing[Future[UpdateWriteResult]] =
    when(mockDataRepository.upsert(ArgumentMatchers.eq(data)))
      .thenReturn(response)

  def mockRemoveById(id: IdModel)(response: Future[WriteResult]): OngoingStubbing[Future[WriteResult]] =
    when(mockDataRepository.removeById(ArgumentMatchers.eq(id), ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(response)

  def mockFindById(id: IdModel)(response: Option[DataModel]): OngoingStubbing[Future[Option[DataModel]]] =
    when(mockDataRepository.findById(ArgumentMatchers.eq(id), ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(response)

  def mockRemove(response: Future[WriteResult]): OngoingStubbing[Future[WriteResult]] =
    when(mockDataRepository.remove(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(response)

}
