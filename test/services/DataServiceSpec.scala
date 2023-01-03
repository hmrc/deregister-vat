/*
 * Copyright 2023 HM Revenue & Customs
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
import com.mongodb.client.result.{DeleteResult, UpdateResult}
import play.api.libs.json.Json
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import repositories.DataRepository
import repositories.models.{DataModel, IdModel}
import testUtils.TestSupport
import uk.gov.hmrc.mongo.play.json.Codecs
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

class DataServiceSpec extends TestSupport with DefaultPlayMongoRepositorySupport[DataModel] {

  override lazy val repository: DataRepository = new DataRepository(mongoComponent, mockConfig)
  lazy val service = new DataService(repository)

  "The .update method" when {

    "there is no existing matching record" should {

      "add the data to the database and return an AcknowledgedUpdateResult with 0 matched and 0 modified" in {
        val expected = UpdateResult.acknowledged(0, 0, Codecs.toBson(IdModel(testVatNumber, testStoreDataKey)))

        await(service.update(testVatNumber, testStoreDataKey, testStoreDataJson)) shouldBe expected
        await(repository.collection.countDocuments().toFuture()) shouldBe 1
      }
    }

    "there is an existing matching record with the same data" should {

      "return an AcknowledgedUpdateResult with 1 matched and 1 modified, and the same data as before" in {
        val expected = UpdateResult.acknowledged(1, 1, null)
        await(service.update(testVatNumber, testStoreDataKey, testStoreDataJson))

        await(service.update(testVatNumber, testStoreDataKey, testStoreDataJson)) shouldBe expected
        await(service.getData(testVatNumber, testStoreDataKey)).get.data shouldBe testStoreDataJson
        await(repository.collection.countDocuments().toFuture()) shouldBe 1
      }
    }

    "there is an existing matching record with different data" should {

      "return an AcknowledgedUpdateResult with 1 matched and 1 modified, with new data" in {
        val expected = UpdateResult.acknowledged(1, 1, null)
        val newJson = Json.obj("newField" -> "newValue")
        await(service.update(testVatNumber, testStoreDataKey, testStoreDataJson))

        await(service.update(testVatNumber, testStoreDataKey, newJson)) shouldBe expected
        await(service.getData(testVatNumber, testStoreDataKey)).get.data shouldBe newJson
        await(repository.collection.countDocuments().toFuture()) shouldBe 1
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

    "the query data is not found" should {

      "return None" in {
        await(service.getData(testVatNumber, testStoreDataKey)) shouldBe None
      }
    }
  }

  "The .removeData method" when {

    "a document is successfully deleted" should {

      "return an AcknowledgedDeleteResult with 1 deleted" in {
        val expected = DeleteResult.acknowledged(1)
        await(service.update(testVatNumber, testStoreDataKey, testStoreDataJson))

        await(service.removeData(testVatNumber, testStoreDataKey)) shouldBe expected
        await(repository.collection.countDocuments().toFuture()) shouldBe 0
      }
    }

    "the requested document cannot be found" should {

      "return an AcknowledgedDeleteResult with 0 deleted" in {
        val expected = DeleteResult.acknowledged(0)
        await(service.update(testVatNumber, testStoreDataKey, testStoreDataJson))

        await(service.removeData(testVatNumber, "unrecognisedKey")) shouldBe expected
        await(repository.collection.countDocuments().toFuture()) shouldBe 1
      }
    }
  }

  "The .removeAll method" when {

    "matching documents are found" should {

      "return an AcknowledgedDeleteResult with the number of matching documents deleted" in {
        val expected = DeleteResult.acknowledged(2)
        val differentKey = "differentKey"
        await(service.update(testVatNumber, testStoreDataKey, testStoreDataJson))
        await(service.update(testVatNumber, differentKey, testStoreDataJson))

        await(service.removeAll(testVatNumber)) shouldBe expected
        await(repository.collection.countDocuments().toFuture()) shouldBe 0
      }
    }

    "no matching documents are found" should {

      "return an AcknowledgedDeleteResult with 0 deleted" in {
        val expected = DeleteResult.acknowledged(0)
        val differentKey = "differentKey"
        val unrecognisedVrn = "111111111"
        await(service.update(testVatNumber, testStoreDataKey, testStoreDataJson))
        await(service.update(testVatNumber, differentKey, testStoreDataJson))

        await(service.removeAll(unrecognisedVrn)) shouldBe expected
        await(repository.collection.countDocuments().toFuture()) shouldBe 2
      }
    }
  }
}
