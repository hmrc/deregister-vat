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

package controllers

import assets.BaseTestConstants._
import controllers.actions.mocks.MockVatAuthorised
import models.responses.ErrorModel
import play.api.http.Status
import play.api.libs.json.Json
import repositories.models.{MongoError, MongoSuccess}
import services.mocks.MockDataService

class DataControllerSpec extends MockVatAuthorised with MockDataService {

  object TestDataController extends DataController(mockVatAuthorised, mockDataService, cc)
  val err = "Mongo Error"

  "The .storeData method" when {

    "called by an Authorised Entity" when {

      "a successful JSON body is received" when {

        "a successful response is returned from the Mongo insert" should {

          "return status NO_CONTENT (204)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockAddEntry(testVatNumber, testStoreDataKey, testStoreDataJson)(MongoSuccess)

            val result = TestDataController.storeData(testVatNumber, testStoreDataKey)(fakeRequest.withJsonBody(testStoreDataJson))

            status(result) shouldBe Status.NO_CONTENT
          }
        }

        "an error response is returned from the Mongo insert" should {

          lazy val result = TestDataController.storeData(testVatNumber, testStoreDataKey)(fakeRequest.withJsonBody(testStoreDataJson))

          "return status ISE (500)" in {
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
            mockAddEntry(testVatNumber, testStoreDataKey, testStoreDataJson)(MongoError(err))

            status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          }

          "return the correct ErrorModel with message" in {
            await(jsonBodyOf(result)) shouldBe Json.toJson(ErrorModel("Error when adding data to Mongo Repository"))
          }
        }
      }

      "an invalid JSON body is received" should {

        lazy val result = TestDataController.storeData(testVatNumber, testStoreDataKey)(fakeRequest)

        "return status BAD_REQUEST (400)" in {
          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockAddEntry(testVatNumber, testStoreDataKey, testStoreDataJson)(MongoSuccess)

          status(result) shouldBe Status.BAD_REQUEST
        }

        "return the correct error message in ErrorModel format" in {
          await(jsonBodyOf(result)) shouldBe Json.toJson(ErrorModel("Body of request did not contain valid JSON"))
        }
      }
    }
  }

  "The .getData method" when {

    "called by an Authorised Entity" when {

      "a document is returned from the Mongo findById method" should {

        lazy val result = TestDataController.getData(testVatNumber, testStoreDataKey)(fakeRequest)

        "return status OK (200)" in {
          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockGetData(testVatNumber, testStoreDataKey)(Some(testStoreDataModel))

          status(result) shouldBe Status.OK
        }

        "return the JSON body of the DataModels data field" in {
          await(jsonBodyOf(result)) shouldBe testStoreDataModel.data
        }
      }

      "no document is returned from the Mongo findById method" should {

        lazy val result = TestDataController.getData(testVatNumber, testStoreDataKey)(fakeRequest)

        "return status NOT_FOUND (404)" in {
          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockGetData(testVatNumber, testStoreDataKey)(None)

          status(result) shouldBe Status.NOT_FOUND
        }

        "return the correct ErrorModel with message" in {
          await(jsonBodyOf(result)) shouldBe Json.toJson(ErrorModel(s"No data found for vrn: $testVatNumber and key: $testStoreDataKey"))
        }
      }
    }
  }

  "The .removeData method" when {

    "called by an Authorised Entity" when {

      "a document is successfully deleted" should {

        lazy val result = TestDataController.removeData(testVatNumber, testStoreDataKey)(fakeRequest)

        "return status NO_CONTENT (204)" in {
          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockRemoveData(testVatNumber, testStoreDataKey)(MongoSuccess)

          status(result) shouldBe Status.NO_CONTENT
        }
      }

      "an error is returned from mongo" should {

        lazy val result = TestDataController.removeData(testVatNumber, testStoreDataKey)(fakeRequest)

        "return status ISE (500)" in {
          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockRemoveData(testVatNumber, testStoreDataKey)(MongoError(err))

          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }

        "return the correct ErrorModel with message" in {
          await(jsonBodyOf(result)) shouldBe
            Json.toJson(ErrorModel(s"Error when removing data for vrn: $testVatNumber and key: $testStoreDataKey"))
        }
      }
    }
  }

  "The .removeAll method" when {

    "called by an Authorised Entity" when {

      "documents are successfully deleted" should {

        lazy val result = TestDataController.removeAll(testVatNumber)(fakeRequest)

        "return status NO_CONTENT (204)" in {
          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockRemove(testVatNumber)(MongoSuccess)

          status(result) shouldBe Status.NO_CONTENT
        }
      }

      "an error is returned from mongo" should {

        lazy val result = TestDataController.removeAll(testVatNumber)(fakeRequest)

        "return status ISE (500)" in {
          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
          mockRemove(testVatNumber)(MongoError(err))

          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }

        "return the correct ErrorModel with message" in {
          await(jsonBodyOf(result)) shouldBe
            Json.toJson(ErrorModel(s"Error when removing all data for vrn: $testVatNumber"))
        }
      }
    }
  }

}
