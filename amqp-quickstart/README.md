Quarkus AMQP 1.0 Quickstart
============================

This project illustrates how you can interact with AMQP 1.0 (Apache Artemis in this quickstart) using MicroProfile Reactive Messaging.
The complete instructions are available on https://quarkus.io/guides/amqp.

## Start the application in dev mode

In a first terminal, run:

```bash
> mvn -f amqp-quickstart-producer quarkus:dev
```

In a second terminal, run:

```bash
> mvn -f amqp-quickstart-processor quarkus:dev
```  

Then, open your browser to `http://localhost:8080/quotes.html`, and click on the "Request Quote" button.

## Build the application in JVM mode

To build the applications, run:

```bash
> mvn -f amqp-quickstart-producer package
> mvn -f amqp-quickstart-processor package
```

Because we are running in _prod_ mode, we need to provide an AMQP 1.0 broker.
The [docker-compose.yml](docker-compose.yml) file starts the broker and your application.

Start the broker and the applications using:

```bash
> docker compose up --build
```

Then, open your browser to `http://localhost:8080/quotes.html`, and click on the "Request Quote" button.
 

## Build the application in native mode

To build the applications into native executables, run:

```bash
> mvn -f amqp-quickstart-producer package -Pnative  -Dquarkus.native.container-build=true
> mvn -f amqp-quickstart-processor package -Pnative -Dquarkus.native.container-build=true
```

The `-Dquarkus.native.container-build=true` instructs Quarkus to build Linux 64bits native executables, who can run inside containers.  

Then, start the system using:

```bash
> export QUARKUS_MODE=native
> docker compose up
```
Then, open your browser to `http://localhost:8080/quotes.html`, and click on the "Request Quote" button.

## Test with RabbitMQ

Review the application properties of the Producer and Consumer. Below are the properties for the Producer and Consumer reviewed for RabbitMQ.

Application properties for the Producer (see the [application.properties](amqp-quickstart-producer/src/main/resources/application.properties) file):

```properties
# Configure the AMQP connection properties.
# Hostname, IP address, and port of the server RabbitMQ is running on
amqp-host=localhost
amqp-port=5672

# Username and password for the server RabbitMQ is running on
amqp-username=quarkus
amqp-password=quarkus

# Set the AMQP address for the outgoing `quotes` channel.
# The address is in format addressing v2, that is specific to RabbitMQ.
# In this case, the address is an exchange.
# For more information, see https://www.rabbitmq.com/docs/amqp#addresses
mp.messaging.outgoing.quote-requests.connector=smallrye-amqp
mp.messaging.outgoing.quote-requests.address=/exchanges/request-quotes
mp.messaging.outgoing.quote-requests.use-anonymous-sender=false

# Set the AMQP address for the incoming `requests` channel.
# In this case, the address is a queue.
mp.messaging.incoming.quotes.connector=smallrye-amqp
mp.messaging.incoming.quotes.address=/queues/quotes
mp.messaging.incoming.quotes.durable=false
```

Application properties for the Consumer (see the [application.properties](amqp-quickstart-processor/src/main/resources/application.properties) file):

```properties
# Configure the AMQP connection properties.
# Hostname, IP address, and port of the server RabbitMQ is running on
amqp-host=localhost
amqp-port=5672

# Username and password for the server RabbitMQ is running on
amqp-username=quarkus
amqp-password=quarkus

# Set the AMQP address for the incoming `requests` channel.
# The address is in format addressing v2, that is specific to RabbitMQ.
# In this case, the address is a queue.
# For more information, see https://www.rabbitmq.com/docs/amqp#addresses
mp.messaging.incoming.requests.connector=smallrye-amqp
mp.messaging.incoming.requests.address=/queues/quotes
mp.messaging.incoming.requests.durable=false

# Set the AMQP address for the outgoing `quotes` channel.
mp.messaging.outgoing.quotes.connector=smallrye-amqp
mp.messaging.outgoing.quotes.address=/exchanges/request-quotes
mp.messaging.outgoing.quotes.use-anonymous-sender=false
```

Then, start the RabbitMQ broker and the applications using:

```bash
podman run --rm -p5672:5672 -p9000:15672 \
  -e RABBITMQ_DEFAULT_USER=quarkus \
  -e RABBITMQ_DEFAULT_PASS=quarkus \
  -e RABBITMQ_LOG=debug \
  rabbitmq:4.0.7-management
```

Console 1 - Start the RabbitMQ broker: