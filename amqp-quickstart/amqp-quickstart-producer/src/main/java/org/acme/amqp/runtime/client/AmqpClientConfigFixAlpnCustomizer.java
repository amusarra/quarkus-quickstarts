package org.acme.amqp.runtime.client;

import io.quarkus.tls.TlsConfiguration;
import io.quarkus.tls.TlsConfigurationRegistry;
import io.smallrye.reactive.messaging.ClientCustomizer;
import io.vertx.amqp.AmqpClientOptions;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;
import org.eclipse.microprofile.config.Config;
import org.jboss.logging.Logger;
import org.acme.amqp.tls.runtime.config.TlsConfigUtils; // Import your custom class

@ApplicationScoped
@Priority(1)
public class AmqpClientConfigFixAlpnCustomizer implements ClientCustomizer<AmqpClientOptions> {

  private static final Logger log = Logger.getLogger(AmqpClientConfigFixAlpnCustomizer.class);

  @Inject
  TlsConfigurationRegistry tlsRegistry;

  @Override
  public AmqpClientOptions customize(String channel, Config channelConfig, AmqpClientOptions options) {
    Optional<String> tlsConfigName = channelConfig.getOptionalValue("tls-configuration-name", String.class);
    if (tlsConfigName.isPresent()) {
      String tlsConfig = tlsConfigName.get();
      Optional<TlsConfiguration> maybeTlsConfig = tlsRegistry.get(tlsConfig);
      if (maybeTlsConfig.isPresent()) {
        TlsConfigUtils.configure(options, maybeTlsConfig.get()); // Use your custom class
        log.debugf("Configured RabbitMQOptions for channel %s with TLS configuration %s", channel, tlsConfig);
      }
    }
    return options;
  }
}