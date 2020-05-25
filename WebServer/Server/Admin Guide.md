# Task Timekeeper Admin Guide

This is a guide for setting up the Task Timekeeper server to be used by users.

[TOC]

## Initial setup and running

### Setting up a Mongo instance

The server needs a MongoDB instance to connect to. It can be running really anywhere, as long as you have connection to it.

#### Getting a quick and easy MongoDB instance running (Podman (Docker))

##### 0) Install Podman (or docker)

https://podman.io/getting-started/installation.html

https://podman.io/getting-started/

##### 1) Setup data directory

```bash
sudo mkdir /data
sudo mkdir /data/db

sudo chown -R $USER /data 
sudo chgrp -R $USER /data 
``` 

##### 2) Create mongo container, tie it to the `/data/db` folder.  

`podman run --name timekeeper_mongo -p=27017:27017 --mount type=bind,destination=/data/db -d mongo`

You can make sure it is running using: `podman ps`

You can check the logs of the mongo instance using: `podman logs timekeeper-mongo`

To start it after the fact: `podman start timekeeper-mongo`

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

https://quarkus.io/guides/config#overriding-properties-at-runtime

Using a configuration file placed in `$PWD/config/application.properties`; By placing an application.properties file inside a directory named config which resides in the directory where the application runs, any runtime properties defined in that file will override the default configuration. Furthermore any runtime properties added to this file that were not part of the original application.properties file will also be taken into account.

You can probably also use a yaml file.

### Connecting to a Mongodb instance

The configuration will by default try to connect to mongodb at `127.0.0.1:27017`. It will use the `task-timekeeper` database name and `task-timekeeper-server` application name.

More information on how to configure the Mongo client can be found on the [all config options guide](https://quarkus.io/guides/all-config#quarkus-mongodb-client_quarkus-mongodb-client).

### Setting Up Your Information

You can define specific info about your server, like the server's name and contact information:

```yaml
runningInfo:
  organization: # The organization running the server
  serverName:   # The name of the server. defaults to organization
  orgUrl:       # URL to the organization or running party
  hostname:     # Hostname to use to hit this service. Defaults to "localhost"
  contactInfo:
    name:       # the name for the main contact
    email:      # the email of the main contact
    phone:  # the telephone number of the main contact
```

These mainly show up in the webpage, but can also be accessed at `/api/server/info`.

This configuration is optional (in whole or part), and is mostly used in the front end webpages.

Note that `runningInfo.serverName` is used in the context of: "<serverName> Task Timekeeper Server"

### Security/ Keys

The service requires a `.pem` public and private key to make JWT tokens for users to login with. The private key must be in `pkcs8` format.

Test keys are in the test resources directory, under 'security'. You will have to make your own and specify the location of the `.pem`s with the `mp.jwt.verify.publickey.location` and `mp.jwt.verify.privatekey.location` properties.

You can also specify the issuer of the key using: `mp.jwt.verify.issuer`, but one will be automatically gleaned from the `runningInfo` configuration (see [Setting Up Your Information](#Setting_Up_Your_Information)).

The test keys were created using the following command:

```
openssl genrsa -out private_key.pem 4096
openssl rsa -pubout -in private_key.pem -out public_key.pem
# convert private key to pkcs8 format in order to import it from Java
openssl pkcs8 -topk8 -in private_key.pem -inform pem -out private_key_pkcs8.pem -outform pem -nocrypt
```

Given the previous commands, you would set `mp.jwt.verify.publickey.location` to the location of `public_key.pem`, and `mp.jwt.verify.privatekey.location` to the location of `private_key_pkcs8.pem`.  

If you are a 'real organization' you might want to use keys that were issued by a real cert provider.

#### The Packaged Key

There is a packaged keys to enable running right off the bat, but you _should not_ use them in production under any circumstances. The service will only run for ten minutes if these keys are used. 

### User

There are a couple of configurations related to the users themselves:

`user.new.autoApprove` - If set to `true`, will automatically approve new users. Otherwise, an admin will have to approve their account before they are allowed to login.

## Runtime Administration

### User/ User Management

TODO

### Health checks

Health checks can be found at `/health` ([http://localhost:8080/health]())

### Metrics

Metrics can be found at:
 - `/metrics` ([http://localhost:8080/metrics]())
 - `/metrics/application` ([http://localhost:8080/metrics/application]())
 



[]: https://www.graalvm.org/docs/getting-started/