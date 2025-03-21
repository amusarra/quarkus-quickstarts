package org.acme.amqp.tls.runtime.config;

import io.quarkus.tls.TlsConfiguration;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.SSLOptions;
import io.vertx.core.net.TCPSSLOptions;
import org.jboss.logging.Logger;

public class TlsConfigUtils {

  private static final Logger log = Logger.getLogger(TlsConfigUtils.class);

  public static void configure(TCPSSLOptions options, TlsConfiguration configuration) {
    options.setSsl(true);
    if (configuration.getTrustStoreOptions() != null) {
      options.setTrustOptions(configuration.getTrustStoreOptions());
    }

    if (configuration.getKeyStoreOptions() != null) {
      options.setKeyCertOptions(configuration.getKeyStoreOptions());
    }

    SSLOptions sslOptions = configuration.getSSLOptions();
    if (sslOptions != null) {
      options.setSslHandshakeTimeout(sslOptions.getSslHandshakeTimeout());
      options.setSslHandshakeTimeoutUnit(sslOptions.getSslHandshakeTimeoutUnit());

      for (String suite : sslOptions.getEnabledCipherSuites()) {
        options.addEnabledCipherSuite(suite);
      }

      for (Buffer buffer : sslOptions.getCrlValues()) {
        options.addCrlValue(buffer);
      }

      options.setEnabledSecureTransportProtocols(sslOptions.getEnabledSecureTransportProtocols());
      // Try to set ALPN configuration, but handle UnsupportedOperationException
      try {
        options.setUseAlpn(sslOptions.isUseAlpn());
      } catch (UnsupportedOperationException e) {
        log.warn("ALPN configuration not supported by implementation: " +
                 options.getClass().getName() +
                 ". ALPN setting will be ignored.");
      }
    }
  }
}