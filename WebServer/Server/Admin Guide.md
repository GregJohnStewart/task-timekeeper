# Task Timekeeper Admin Guide

This is a guide for setting up the Task Timekeeper server to be used by users.

## Initial setup and running

### Setting up a Mongo instance

The server needs a MongoDB instance to connect to. It can be running really anywhere, as long as you have connection to it.

#### Getting a quick and easy MongoDB instance running (Docker)

TODO:: move to use PodMan instead of docker

`sudo docker run -ti --rm -p 27017:27017 mongo:4.0`

TODO:: how to get it to save the database file and reuse it later

### Running the server

#### Running with container

##### Prerequisite: GraalVm

As a prerequisite, GraalVM must be used instead of an 'official' JDK.

[Official Reference from Graal](https://www.graalvm.org/docs/getting-started/)

[A good reference for installing on Linux using alternatives (Preferred for Linux users)](https://gist.github.com/ricardozanini/fa65e485251913e1467837b1c5a8ed28)

##### Building the container

##### Running the container

#### Running "just the code"

## Configuring the server

### Connecting to a Mongodb instance

### Setting up your information

You can define specific info about your server, like the server's name and contact information:

```yaml
runningInfo:
  organization: # The organization running the server
  serverName:   # The name of the server
  url:          # URL to the organization or running party
  contactInfo:
    name:       # the name for the main contact
    email:      # the email of the main contact
    phone:  # the telephone number of the main contact
```

These mainly show up in the webpage, but can also be accessed at `/api/server/info`.

This configuration is optional (in whole or part), and is mostly used in the front end webpages.

### Security/ Keys

The service requires a `.pem` public key to make JWT tokens for users to login with.

Test keys are in the test resources directory, under 'security'. You will have to make your own and specify the location of the `.pem` with the `mp.jwt.verify.publickey.location` property.

You can also specify the issuer of the key using: `mp.jwt.verify.issuer`

The test keys were created using the following commands:

```
openssl req -new -newkey rsa:4096 -nodes -keyout snakeoil.key -out snakeoil.csr
openssl x509 -req -sha256 -days 365 -in snakeoil.csr -signkey snakeoil.key -out snakeoil.pem
```

If you are a 'real organization' you might want to use keys that were issued by a real cert provider.

## Runtime Administration

### Health checks

Health checks can be found at `/health` ([http://localhost:8080/health]())

### Metrics

Metrics can be found at:
 - `/metrics` ([http://localhost:8080/metrics]())
 - `/metrics/application` ([http://localhost:8080/metrics/application]())
 



[]: https://www.graalvm.org/docs/getting-started/