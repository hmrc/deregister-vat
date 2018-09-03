# Deregister VAT Microservice

[![Build Status](https://travis-ci.org/hmrc/deregister-vat.svg)](https://travis-ci.org/hmrc/deregister-vat) [![Download](https://api.bintray.com/packages/hmrc/releases/deregister-vat/images/download.svg)](https://bintray.com/hmrc/releases/deregister-vat/_latestVersion)

This service provides backend functionality to support the Deregister VAT Frontend Microservice.

It's primary purpose is to support the Storage and Retrieval of Session Data that is captured by the frontend from the User

## Endpoints

### Store Key-Value Data for VRN and Key
#### PUT /deregister-vat/data/:vrn/:key
##

Allows the `deregister-vat-frontent` microservice to store key value data captured during the user journey.

The endpoint is idempotent, if a records already exists for the supplied vrn and key then the document is updated with the payload supplied. If the document does not exist, it is inserted. Both scenarios will return a NO_CONTENT response if successful.
  
*  **URL Params**

   **Required:**
 
   `:vrn` VRN of the logged in user (or in case of an Agent, the VRN of the Client on which they're acting on behalf of.
   
   `:key` Unique name for the piece of data to be stored. For example, if storing a forename the key may be `forename`

* **Json Body**

    The json body of the request will be stored in the `value` which is stored within the MongoDB repository. Therefore, any valid json can be supplied and stored so as to keep the backend service generic and scalable.

* **Success Response:**

  * **Code:** 204 (NO_CONTENT)
 
* **Error Responses:**

  * **Code:** 500 (INTERNAL_SERVER_ERROR)
  
    **Content:** `{ "message" : "Error Message..." }`

  * **Code:** 401 (UNAUTHORIZED)
  
    **Content:** No body returned

  * **Code:** 403 (FORBIDDEN)
  
    **Content:** No body returned

* **Sample Call:**

  ```
    PUT /deregister-vat/data/999999999/capitalAssets
    {
      "hasCapitalAssets" : "yes",
      "assetValue" : 12000.00
    }
    
    Response: 204 (NO_CONTENT)
  ```
  

### Get Key-Value Data for VRN and Key
#### GET /deregister-vat/data/:vrn/:key
##

Allows the `deregister-vat-frontent` microservice to retrieve stored key value data.
  
*  **URL Params**

   **Required:**
 
   `:vrn` VRN of the logged in user (or in case of an Agent, the VRN of the Client on which they're acting on behalf of.
   
   `:key` Unique name for the piece of data to be retrieved. For example, if retrieving a forename the key may be `forename`

* **Success Response:**

  * **Code:** 200 (OK)
  
    **Content:** Json structure matching that which was originally stored
 
* **Error Responses:**

  * **Code:** 401 (UNAUTHORIZED)
  
    **Content:** No body returned

  * **Code:** 403 (FORBIDDEN)
  
    **Content:** No body returned
    
  * **Code:** 404 (NOT_FOUND)
      
    **Content:** `{ message: "No data found for vrn: $vrn and key: $key" }`
    
  * **Code:** 500 (INTERNAL_SERVER_ERROR)
      
    **Content:** `{ message: "Error Message..." }`

* **Sample Call:**

  ```
    GET /deregister-vat/data/999999999/capitalAssets
    
    Response: 200 (OK)
    {
      "hasCapitalAssets" : "yes",
      "assetValue" : 12000.00
    }
  ```
  
  
### Delete Key-Value Data for VRN and Key
#### DELETE /deregister-vat/data/:vrn/:key
##

Allows the `deregister-vat-frontent` microservice to delete stored key value data.

If the key is successfully deleted, a success response is returned of 204 (NO_CONTENT) - If the key did not exist in the DB then the API will return the same success response as the desired end result/state has been reached already.

*  **URL Params**

   **Required:**

   `:vrn` VRN of the logged in user (or in case of an Agent, the VRN of the Client on which they're acting on behalf of.
   
   `:key` Unique name for the piece of data to be deleted. For example, if deleting a forename the key may be `forename`

* **Success Response:**

    * **Code:** 204 (NO_CONTENT)

* **Error Responses:**

    * **Code:** 401 (UNAUTHORIZED)

      **Content:** No body returned

    * **Code:** 403 (FORBIDDEN)

      **Content:** No body returned
  
    * **Code:** 500 (INTERNAL_SERVER_ERROR)
    
      **Content:** `{ message: "Error Message..." }`

* **Sample Call:**

```
  DELETE /deregister-vat/data/999999999/capitalAssets
  
  Response: 204 (NO_CONTENT)
```

### Delete Key-Value Data for VRN
#### DELETE /deregister-vat/data/:vrn
##

Allows the `deregister-vat-frontent` microservice to delete all stored key value data for a supplied VRN.

*  **URL Params**

   **Required:**

   `:vrn` VRN of the logged in user (or in case of an Agent, the VRN of the Client on which they're acting on behalf of.
   
* **Success Response:**

    * **Code:** 204 (NO_CONTENT)

* **Error Responses:**

    * **Code:** 401 (UNAUTHORIZED)

      **Content:** No body returned

    * **Code:** 403 (FORBIDDEN)

      **Content:** No body returned
  
    * **Code:** 500 (INTERNAL_SERVER_ERROR)
    
      **Content:** `{ message: "Error Message..." }`

* **Sample Call:**

```
  DELETE /deregister-vat/data/999999999
  
  Response: 204 (NO_CONTENT)
```
    

## License
  
 This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")