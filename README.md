# scala-prime-number-service

## How to execute

### For the following commands to work, `sbt` is required
* spin up the proxy server using command: `sbt "runMain PrimeNumber.PrimeNumberServer"`
* spin up the prime number gRPC server using command: `sbt "runMain PrimeNumber.PrimeNumberServer"`
* Now the proxy service is available at the endpoint `localhost:8080` and gRPC server is bound to `localhost:8081`
* HTTP Get requests can be made at `localhost:8080\prime\{your_number}`

## Design Choices
### proxy-service
* `akka-http` is used to establish the proxy-server that will make HTTP endpoint available over REST
* To handle the wrong inputs like `prime/abcd`, a regex based check is placed to keep these faulty requests on a check
* In order to not overpress the server with huge requests like `prime/158198`, the same regex check is to be used
* A forwarder will make an rpc call to the external service to and recieve a response as a string

### prime-number-service
* `akka-grpc` is used to establish the gRPC prime number server
* Protobuf contract tells on how to communicate with the service
* The checks for handling edge cases like when number is passed as a negative or 0 are made available
* The response containing the list of prime numbers or some other message is sent as a string

### tests
* The suitable end-to-end test cases for both services covering wide range of wrong inputs can be found

## Scope for the future releases
* Adding an authorization layer over proxy-service would be a good next step.Currently server is open to everyone once spun-out.
* Code optimization: Currently few things are hard coded and being written in a simple and ineffecient way.
A minor releases based on the little optimizations would be a good next step.
For ex: The protobuf contract is very simple in a way that it only returns String as a reply be it the list of prime numbers or some other message.
The reply could be expanded into two fields: one being a list of integers and other being a string containing some other message.
* There are not many checks for handling the cases when service fails or couldn't connect to the other services.
* Exploring other libraries like lagom or zio and see how well they can be integrated into the current structure.

## version changes

### 0.5 - Bug fix to address failing to start a server issue.
### 0.4 - Added Proxy Service to expose HTTP endpoint over REST.
### 0.3 - Added Prime Number service to calculate all prime numbers upto N.
### 0.2 - Added structure of the project
### 0.1 - Added basic sbt template

