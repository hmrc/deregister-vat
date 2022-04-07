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

import assets.BaseTestConstants._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import repositories.DataRepository
import repositories.models.{DataModel, MongoError, MongoSuccess}
import testUtils.TestSupport
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

class DataServiceSpec extends TestSupport with AnyWordSpecLike with Matchers with DefaultPlayMongoRepositorySupport[DataModel] {

  override lazy val repository: DataRepository = new DataRepository(mongoComponent, mockConfig)
  lazy val service = new DataService(repository)

  val deleteError = MongoError("Failed to delete")
  val updateError = MongoError("Update was unacknowledged")

  "The .update method" when {

    "the data is valid" should {

      "ensure indexes are created" in {
        await(repository.collection.listIndexes().toFuture()).size shouldBe 2
      }

      "return MongoSuccess case object" in {
        await(service.update(testVatNumber, testStoreDataKey, testStoreDataJson)) shouldBe MongoSuccess
      }
    }

    "the data is invalid" should {

      "return MongoError case object" in {
        await(service.update(testVatNumber, testStoreDataKey, testStoreDataJson)) shouldBe
          MongoError("Update was unacknowledged")
      }
    }
  }

  "The .getData method" when {

    "the query data is valid" should {

      "return the correct data model" in {
        await(service.update(testVatNumber, testStoreDataKey, testStoreDataJson))
        await(service.getData(testVatNumber, testStoreDataKey)) shouldBe Some(testStoreDataModel)
      }
    }

    "the query data is invalid" should {

      "return None" in {
        await(service.getData(testVatNumber, testStoreDataKey)) shouldBe None
      }
    }
  }

  "The .removeData method" when {

    "a document is successfully deleted" should {

      "return MongoSuccess case object" in {
        await(service.removeData(testVatNumber, testStoreDataKey)) shouldBe MongoSuccess
      }
    }

    "the document cannot be deleted" should {

      "return MongoError case object" in {
        await(service.removeData(testVatNumber, testStoreDataKey)) shouldBe MongoError("Failed to delete")
      }
    }
  }

  "The .removeAll method" when {

    "documents are successfully deleted" should {

      "return a MongoSuccess" in {
        val result = {
          await(service.update(testVatNumber, testStoreDataKey, testStoreDataJson))
          await(service.removeAll(testVatNumber))
        }
        result shouldBe MongoSuccess
      }
    }

    "documents cannot be deleted" should {

      "return MongoError case object" in {
        await(service.removeAll(testVatNumber)) shouldBe deleteError
      }
    }
  }
}
