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
  url:          # URL to the organizatiuon or running party
  contactInfo:
    name:       # the name for the main contact
    email:      # the email of the main contact
    telephone:  # the telephone number of the main contact
```

These mainly show up in the webpage, but can also be accessed at `/api/server/info`

## Runtime Administration

### Health checks

Health checks can be found at `/health` ([http://localhost:8080/health]())

### Metrics

Metrics can be found at:
 - `/metrics` ([http://localhost:8080/metrics]())
 - `/metrics/application` ([http://localhost:8080/metrics/application]())
 

