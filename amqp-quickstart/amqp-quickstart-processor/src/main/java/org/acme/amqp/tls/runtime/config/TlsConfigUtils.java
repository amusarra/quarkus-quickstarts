package org.acme.amqp.tls.runtime.config;

import io.quarkus.tls.TlsConfiguration;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.SSLOptions;
import io.vertx.core.net.TCPSSLOptions;

public class TlsConfigUtils {

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
      if (sslOptions.isUseAlpn()) {
        options.setUseAlpn(true);
      }
    }
  }
}