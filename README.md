This is a project about currency exchanges.  
***How to run project?***  
1-It uses docker so it should be installed in your pc.  
2-It also uses java17 so it should also be installed in your pc.  
3-If above steps are completed , you can run -> "docker-compose up --build" in terminal to start up the project and redis all together.  
4-After docker is done with it's downloads , it will run the spring project.  
***How does it work?***  
Since I am using free version of "https://currencylayer.com/", I have left around 15 api calls. As free version user I will get updates daily so there is a scheduled job that runs daily to update currency rates.  
Received currency rates are stored at redis client. Example for USD : {"USDEUR":"x.xxx","USDGBP":"x.xxx"}  
Since the rates are saved on redis the project is ready to accept any user calls.  
For testing purposes please use "http://localhost:8080/swagger-ui/index.html", which grants you easy ui api call access.  
***What are the api structures?***  
There are 4 different apis defined in this project and all of them has swagger definitions are added. Here they are:  
**1-/exchange/{currency1}/{currency2} -> GET**  
This api gets you the currency rate of currency1 over currency2.  
*curl string =* "curl -X 'GET' \  'http://localhost:8080/exchange/USD/EUR' \  -H 'accept: */*'"  
*response =* "{  
  "code": 200,  
  "message": "Success",  
  "data": 0.879504  
}"  
**2-/exchange/{amount}/{currency1}/{currency2} -> GET**  
This api gets the entered amount and returns the converted amount by using the rate of given currencies and unique conversionId.  
*curl string =* "curl -X 'GET' \  'http://localhost:8080/exchange/100/USD/EUR' \  -H 'accept: application/json'"  
*response=* "{  
  "code": 200,  
  "message": "Success",  
  "data": {  
    "convertedValue": 87.9504,  
    "conversionId": 1  
  }  
}"  
**3-/exchange -> GET**  
This api lets you filter by using a specific conversionId or a date. Api also shows the data list page by page according to the page list and size.  
--If user enters a conversionId, a single record will be returned.  
*curl string=* "curl -X 'GET' \  'http://localhost:8080/exchange?id=1&page=0&size=20' \  -H 'accept: */*'"  
*response=" "{  
  "code": 200,  
  "message": "Success",  
  "data": {  
    "content": [  
      {  
        "amount": 100,  
        "convertedAmount": 87.9504,  
        "fromCurrency": "USD",  
        "toCurrency": "EUR",  
        "date": "2025-05-25T00:00:00"  
      }  
    ],  
    "pageable": {
      "pageNumber": 0,
      "pageSize": 1,
      "sort": {
        "sorted": false,
        "unsorted": true,
        "empty": true
      },
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalPages": 1,
    "totalElements": 1,
    "last": true,
    "size": 1,
    "number": 0,
    "sort": {
      "sorted": false,
      "unsorted": true,
      "empty": true
    },
    "first": true,
    "numberOfElements": 1,
    "empty": false
  }
}"  

--If user enters a date, it will return the list of conversion made at that specific day page by page.  
*curl string=* "curl -X 'GET' \  'http://localhost:8080/exchange?date=2025-05-25&page=0&size=3' \  -H 'accept: */*'"  
*response=* "{  
  "code": 200,  
  "message": "Success",  
  "data": {  
    "content": [  
      {  
        "amount": 100,  
        "convertedAmount": 87.9504,  
        "fromCurrency": "USD",  
        "toCurrency": "EUR",  
        "date": "2025-05-25T00:00:00"  
      },  
      {  
        "amount": 99,  
        "convertedAmount": 87.07089599999999,  
        "fromCurrency": "USD",  
        "toCurrency": "EUR",  
        "date": "2025-05-25T00:00:00"  
      },  
      {  
        "amount": 119,  
        "convertedAmount": 104.66097599999999,  
        "fromCurrency": "USD",  
        "toCurrency": "EUR",  
        "date": "2025-05-25T00:00:00"  
      }  
    ],  
    "pageable": {
      "pageNumber": 0,
      "pageSize": 3,
      "sort": {
        "sorted": false,
        "unsorted": true,
        "empty": true
      },
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalPages": 5,
    "totalElements": 13,
    "last": false,
    "size": 3,
    "number": 0,
    "sort": {
      "sorted": false,
      "unsorted": true,
      "empty": true
    },
    "first": true,
    "numberOfElements": 3,
    "empty": false
  }
}"  
**4-/exchange/upload -> POST**  
This api lets you convert from single or multiple **csv** files added by the user. Automaticaly saves them and returns their conversionId for future calls. Please find the test csv files in project structure.  
*curl string=* "curl -X 'POST' \  'http://localhost:8080/exchange/upload' \  -H 'accept: application/json' \  -H 'Content-Type: multipart/form-data' \  -F 'files=@test1.csv;type=text/csv' \  -F 'files=@test2.csv;type=text/csv'"  
*response=* "{  
  "code": 200,  
  "message": "Success",  
  "data": [  
    {  
      "convertedValue": 87.07089599999999,  
      "conversionId": 20  
    },  
    {  
      "convertedValue": 104.66097599999999,  
      "conversionId": 21  
    },  
    {  
      "convertedValue": 122.25105599999999,  
      "conversionId": 22  
    },  
    {  
      "convertedValue": 139.841136,  
      "conversionId": 23  
    },  
    {  
      "convertedValue": 1089.705456,  
      "conversionId": 24  
    },  
    {  
      "convertedValue": 7.9155359999999995,  
      "conversionId": 25  
    },  
    {  
      "convertedValue": 87.9504,  
      "conversionId": 26  
    },  
    {  
      "convertedValue": 105.54047999999999,  
      "conversionId": 27  
    },  
    {  
      "convertedValue": 123.13055999999999,  
      "conversionId": 28  
    },  
    {  
      "convertedValue": 140.72064,  
      "conversionId": 29  
    },  
    {  
      "convertedValue": 1090.58496,  
      "conversionId": 30  
    },  
    {  
      "convertedValue": 8.79504,  
      "conversionId": 31  
    }  
  ]  
}"  

***Dev Notes:***  
-H2 inmemory db is being used to save conversion operations.  
-Project specific error definitions are added.  
-General response payload structure is created.  
-Generic http request sender is defined.  
-Strategy pattern is added for each http type.  
-Mapstruct is added for mapping purposes.  
-For easy API calls OpenAPI(Swagger) is added.
