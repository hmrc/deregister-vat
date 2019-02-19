/*
 * Copyright 2019 HM Revenue & Customs
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

import assets.BaseTestConstants._
import repositories.mocks.MockDataRepository
import repositories.models.{DataModel, IdModel, MongoError, MongoSuccess}

class DataServiceSpec extends MockDataRepository {

  object TestDataService extends DataService(mockDataRepository)

  "The .update method" when {

    "a successful response is returned from the Mongo insert" should {

      "return MongoSuccess case object" in {
        mockAddEntry(DataModel(IdModel(testVatNumber, testStoreDataKey), testStoreDataJson))(successUpdateWriteResult)
        await(TestDataService.update(testVatNumber, testStoreDataKey, testStoreDataJson)) shouldBe MongoSuccess
      }
    }

    "an error response is returned from the Mongo insert" should {

      "return MongoError case object" in {
        mockAddEntry(DataModel(IdModel(testVatNumber, testStoreDataKey), testStoreDataJson))(errorResult)
        await(TestDataService.update(testVatNumber, testStoreDataKey, testStoreDataJson)) shouldBe MongoError(errMsg)
      }
    }
  }

  "The .getData method" when {

    "a document is returned from the Mongo findById method" should {

      "return MongoSuccess case object" in {
        mockFindById(IdModel(testVatNumber, testStoreDataKey))(Some(testStoreDataModel))
        await(TestDataService.getData(testVatNumber, testStoreDataKey)) shouldBe Some(testStoreDataModel)
      }
    }

    "no document is returned from the Mongo findById method" should {

      "return MongoError case object" in {
        mockFindById(IdModel(testVatNumber, testStoreDataKey))(None)
        await(TestDataService.getData(testVatNumber, testStoreDataKey)) shouldBe None
      }
    }
  }

  "The .removeData method" when {

    "a document is successfully deleted" should {

      "return MongoSuccess case object" in {
        mockRemoveById(IdModel(testVatNumber, testStoreDataKey))(successWriteResult)
        await(TestDataService.removeData(testVatNumber, testStoreDataKey)) shouldBe MongoSuccess
      }
    }

    "an error is returned from mongo" should {

      "return MongoError case object" in {
        mockRemoveById(IdModel(testVatNumber, testStoreDataKey))(errorResult)
        await(TestDataService.removeData(testVatNumber, testStoreDataKey)) shouldBe MongoError(errMsg)
      }
    }
  }

  "The .removeAll method" when {

    "documents are successfully deleted" should {

      "return MongoSuccess case object" in {
        mockRemove(successWriteResult)
        await(TestDataService.removeAll(testVatNumber)) shouldBe MongoSuccess
      }
    }

    "an error is returned from mongo" should {

      "return MongoError case object" in {
        mockRemove(errorResult)
        await(TestDataService.removeAll(testVatNumber)) shouldBe MongoError(errMsg)
      }
    }
  }
}
