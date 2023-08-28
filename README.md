# Gateway Management System

This project is a Gateway Management System that allows you to manage gateways and their associated
peripheral devices through a REST API. It's built using Spring Boot. It provides endpoints for
creating a gateway, adding a device to a gateway, removing associated devices from a gateway and fetching gateway details

## Requirements

- Java 8 or higher
- Maven

## Getting Started

1. Build the project:
    ```bash
    mvn clean install

2. Run the application:
    ```bash
   mvn spring-boot:run

The application will start running on `http://localhost:8082`

## API Endpoints

| HTTP Method | Endpoint                           | Description                                       |
|-------------|------------------------------------|---------------------------------------------------|
| POST        | `/gateways`                        | Creates a new gateway.                            |
| POST        | `/gateways/{serialNumber}/devices` | Adds a peripheral device to a gateway.            |
| GET         | `/gateways/{serialNumber}`         | Get details of a single gateway by serial number. |
| GET         | `/gateways`                        | Get a list of all gateways .                      |
| DELETE      | `/gateways/{serialNumber}`         | Remove a peripheral device from a gateway.        |

## Usage

Use a tool like Postman or curl to send HTTP requests to the API endpoints. Here are some examples:

* Register a drone: `POST /gateways` <br>
  Request Body: <br>
    ```json
        {
           "serialNumber": "Gateway-123",
           "name": "Main Gateway",
           "ipv4Address": "192.168.1.1",
        }

* Add a peripheral device to a gateway:`POST /gateways/{serialNumber}/devices` <br>
  Request Body: <br>
    ```json
    [
      {
         "uid": "Device1",
         "vendor": "NewDeviceVendor",
         "status": "OFFLINE"
      }
    ]

* Get list of all gateways: `GET /gateways`

* Get details of a single gateway: `GET /gateways/{serialNumber}`

* Remove a device from a gateway: `DELETE /gateways/{deviceId}`