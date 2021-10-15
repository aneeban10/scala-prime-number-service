# scala-prime-number-service

## How to execute

### For the following commands to work, `sbt` is required
* spin up the proxy server using command: `sbt "runMain PrimeNumber.PrimeNumberServer"`
* spin up the prime number gRPC server using command: `sbt "runMain PrimeNumber.PrimeNumberServer"`
* Now the proxy service is available at the endpoint `localhost:8080 and gRPC server is bound to `localhost:8081``
* HTTP Get requests can be made at `localhost:8080\prime\{your_number}`

## Design Choices

## Scope for the future releases

## version changes

###0.1 - Added basic sbt template

###0.2 - Added structure of the project

###0.3 - Added Prime Number service to calculate all prime numbers upto N.

###0.4 - Added Proxy Service to expose HTTP endpoint over REST.

###0.5 - Bug fix to address failing to start a server issue.