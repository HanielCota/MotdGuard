package io.github.hanielcota.motdguard.ratelimit;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
class IpExtractor {

  public static Optional<String> extract(final InetSocketAddress remoteAddress) {
    if (remoteAddress == null) {
      return Optional.empty();
    }

    final InetAddress address = remoteAddress.getAddress();

    if (address == null) {
      log.warn(
          "Unresolved address; using hostname as rate-limit key: {}",
          remoteAddress.getHostString());

      return Optional.ofNullable(normalize(remoteAddress.getHostString()));
    }

    return Optional.ofNullable(normalize(address.getHostAddress()));
  }

  private static String normalize(final String host) {
    if (host == null || host.isBlank()) {
      return null;
    }

    final int scopeIndex = host.indexOf('%');

    if (scopeIndex > 0) {
      return host.substring(0, scopeIndex);
    }

    return host;
  }
}
