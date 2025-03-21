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

Review the application properties of the Producer and Consumer. Below are the properties for the Producer and Consumer reviewed for RabbitMQ configured with TLS.

Application properties for the Producer (see the [application.properties](amqp-quickstart-producer/src/main/resources/application.properties) file):

```properties
#
# Configure the Quarkus application properties to connect to the RabbitMQ broker.
# The Quarkus application properties are used to configure the AMQP connection properties.
# The AMQP connection properties are used to connect to the RabbitMQ broker.
# The RabbitMQ broker is used to send and receive messages.
# The messages are sent and received by the `smallrye-amqp` connector.
# The `smallrye-amqp` connector used by the `mp.messaging` API.
# The `mp.messaging` API used by the `@Incoming` and `@Outgoing` annotations.
# The 'Producer' class uses the '@Incoming' and '@Outgoing' annotations.
# The 'Producer' class used to send the outgoing messages via Rest API and expose the incoming messages via Rest API.

# Configure the AMQP connection properties.
# Hostname, IP address, and port of the server RabbitMQ is running on
amqp-host=localhost
amqp-port=5671
amqp-use-ssl=true

# Username and password for the server RabbitMQ is running on
amqp-username=quarkus
amqp-password=quarkus

quarkus.tls.rabbitmq-tls-config.trust-store.pem.certs=certs/ca.crt
quarkus.tls.rabbitmq-tls-config.alpn=false

# Setting the key-store path. The key-store file is located in the `certs` directory
# inside the resources directory.
quarkus.tls.rabbitmq-tls-config.key-store.p12.path=certs/client_amusarra-macbook-pro.local.p12

# Setting the key-store password.
# The password is stored in the `application.properties` file.
# In a development environment, the password may be changed when regenerating the key-store (e.g., using the `mvn clean` command).
# In a production environment, it is recommended to use a secure password storage mechanism.
quarkus.tls.rabbitmq-tls-config.key-store.p12.password=changeit

# Set the AMQP address for the outgoing `quotes` channel.
# The address is in format addressing v2, that is specific to RabbitMQ.
# In this case, the address is an exchange.
# For more information, see https://www.rabbitmq.com/docs/amqp#addresses
mp.messaging.outgoing.quote-requests.tls-configuration-name=rabbitmq-tls-config
mp.messaging.outgoing.quote-requests.use-ssl=true
mp.messaging.outgoing.quote-requests.connector=smallrye-amqp
#mp.messaging.outgoing.quote-requests.virtual-host=vhost:amusarra-quarkus
mp.messaging.outgoing.quote-requests.address=/exchanges/request-quotes
mp.messaging.outgoing.quote-requests.use-anonymous-sender=false

# Set the AMQP address for the incoming `requests` channel.
# In this case, the address is a queue.
mp.messaging.incoming.quotes.tls-configuration-name=rabbitmq-tls-config
mp.messaging.incoming.quotes.use-ssl=true
mp.messaging.incoming.quotes.connector=smallrye-amqp
#mp.messaging.incoming.quotes.virtual-host=amusarra-quarkus
mp.messaging.incoming.quotes.address=/queues/quotes
mp.messaging.incoming.quotes.durable=false

quarkus.arc.exclude-types=io.quarkus.smallrye.reactivemessaging.amqp.runtime.AmqpClientConfigCustomizer
```

Application properties for the Consumer (see the [application.properties](amqp-quickstart-processor/src/main/resources/application.properties) file):

```properties
#
# Configure the Quarkus application properties to connect to the RabbitMQ broker.
# The Quarkus application properties are used to configure the AMQP connection properties.
# The AMQP connection properties are used to connect to the RabbitMQ broker.
# The RabbitMQ broker is used to send and receive messages.
# The messages are sent and received by the `smallrye-amqp` connector.
# The `smallrye-amqp` connector used by the `mp.messaging` API.
# The `mp.messaging` API used by the `@Incoming` and `@Outgoing` annotations.
# The `@Incoming` and `@Outgoing` annotations are used by the `Processor` class.
# The `Processor` class used to process the incoming messages and send the outgoing messages.
#

# Configure the AMQP connection properties.
# Hostname, IP address, and port of the server RabbitMQ is running on
amqp-host=localhost
amqp-port=5671
amqp-use-ssl=true

# Username and password for the server RabbitMQ is running on
amqp-username=quarkus
amqp-password=quarkus

quarkus.tls.rabbitmq-tls-config.trust-store.pem.certs=certs/ca.crt
quarkus.tls.rabbitmq-tls-config.alpn=false

# Setting the key-store path. The key-store file is located in the `certs` directory
# inside the resources directory.
quarkus.tls.rabbitmq-tls-config.key-store.p12.path=certs/client_amusarra-macbook-pro.local.p12

# Setting the key-store password.
# The password is stored in the `application.properties` file.
# In a development environment, the password may be changed when regenerating the key-store (e.g., using the `mvn clean` command).
# In a production environment, it is recommended to use a secure password storage mechanism.
quarkus.tls.rabbitmq-tls-config.key-store.p12.password=changeit

# Set the AMQP address for the incoming `requests` channel.
# The address is in format addressing v2, that is specific to RabbitMQ.
# In this case, the address is a queue.
# For more information, see https://www.rabbitmq.com/docs/amqp#addresses
mp.messaging.incoming.requests.tls-configuration-name=rabbitmq-tls-config
mp.messaging.incoming.requests.use-ssl=true
mp.messaging.incoming.requests.connector=smallrye-amqp
#mp.messaging.incoming.requests.virtual-host=vhost:amusarra-quarkus
#mp.messaging.incoming.requests.sni-server-name=vhost:amusarra-quarkus
mp.messaging.incoming.requests.address=/queues/quotes
mp.messaging.incoming.requests.durable=false

# Set the AMQP address for the outgoing `quotes` channel.
mp.messaging.outgoing.quotes.tls-configuration-name=rabbitmq-tls-config
mp.messaging.outgoing.quotes.use-ssl=true
mp.messaging.outgoing.quotes.connector=smallrye-amqp
#mp.messaging.outgoing.quotes.virtual-host=vhost:amusarra-quarkus
mp.messaging.outgoing.quotes.address=/exchanges/request-quotes
mp.messaging.outgoing.quotes.use-anonymous-sender=false

quarkus.arc.exclude-types=io.quarkus.smallrye.reactivemessaging.amqp.runtime.AmqpClientConfigCustomizer
```

### Build the application in JVM mode

To build the applications, run:

```bash
mvn clean package -DskipTests=true
```

### Build docker images and start the applications

To build the Docker images and start the applications, run:

```bash
# Build the Docker images
podman compose -f docker-compose-rabbitmq.yml build

# Start the RabbitMQ broker and the applications
podman compose -f docker-compose-rabbitmq.yml up
```

### Test issue Cannot read the array length because "tms" is null

If you want to test the issue "Cannot read the array length because "tms" is null", you can use the following steps:

1. Uncomment the following line in the [application.properties](amqp-quickstart-producer/src/main/resources/application.properties) file:

```properties
mp.messaging.outgoing.quote-requests.virtual-host=vhost:amusarra-quarkus
```

2. Uncomment the following line in the [application.properties](amqp-quickstart-processor/src/main/resources/application.properties) file:

```properties
mp.messaging.incoming.requests.virtual-host=vhost:amusarra-quarkus
```

3. Build the applications and start the RabbitMQ broker and the applications.
4. Observe the error message in the Producer and Consumer logs.

You can see the error message in the Processor logs:

```log
processor-1      | 2025-03-21 13:18:14,331 WARN  [io.net.cha.ChannelInitializer] (vert.x-eventloop-thread-0) Failed to initialize a channel. Closing: [id: 0x34c31341]: io.vertx.core.VertxException: java.lang.NullPointerException: Cannot read the array length because "tms" is null
processor-1      | 	at io.vertx.core.net.impl.SslChannelProvider.sslClientContext(SslChannelProvider.java:85)
processor-1      | 	at io.vertx.core.net.impl.SslChannelProvider.sslClientContext(SslChannelProvider.java:78)
processor-1      | 	at io.vertx.core.net.impl.SslChannelProvider.createClientSslHandler(SslChannelProvider.java:135)
```