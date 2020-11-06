## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Usage](#usage)
* [Limitations](#limitations)
* [How it works](#how-it-works)

## General info
This project helping create route for delivery things from storages to all consumers.
	
## Technologies
Project was created with:
* SpringBoot version: 2.3.5
* gson version: 2.8.6
* Google distance matrix API
	
## Usage
### How to build
Standalone jar
`./gradlew.bat bootJar`

Ready jar file you can find in 
`\build\libs`

### Start
For start this app, you need to start jar file.

You can edit application.properties files:

server.port

googleApi.key
### Endpoints
Use post http request to host:port/createRoute.
The request body must be in following format:
```json
{
    "storages":{
        "storage id":"storage name"
    },
    "customers":
    [
        {
            "address":"address of customer",
            "storage_id":"id of storage where is customer goods"
        }
    ],
    "start_address":"address where starting route"
}
```
#### Example
Input:
```json
{
    "storages":{
        "1":"Zhytomyr",
        "2":"Dnipro"
    },
    "customers":
    [
        {
            "address":"Kyiv",
            "storage_id":"1"
        },
        {
            "address":"Poltava",
            "storage_id":"2"
        }
    ],
    "start_address":"Khmenitsky"
}
```
Output:
```json
{
    "route": [
        {
            "address": "Khmenitsky",
            "type": "startAddress"
        },
        {
            "address": "Zhytomyr",
            "type": "storage"
        },
        {
            "address": "Kyiv",
            "type": "customer"
        },
        {
            "address": "Dnipro",
            "type": "storage"
        },
        {
            "address": "Poltava",
            "type": "customer"
        }
    ],
    "length": 1007022.0
}
```
## Limitations
Input addresses can be in next format:
* Google map address (Kyiv, Ukraine, 02000)
* Geographic coordinates ( 50.448336,30.52232 )

Output addresses are in same format.

All distances in meters.
The total number of points must be not more than 100.

## How it works
When total number if points not more than 20, using dynamic programming and route it the best of the existing ones.

When total number of points more than 20, using genetic algorithm and route can be not the best.