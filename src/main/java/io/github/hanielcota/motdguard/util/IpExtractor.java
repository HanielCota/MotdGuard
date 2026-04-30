package io.github.hanielcota.motdguard.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import lombok.experimental.UtilityClass;

/**
 * Utility class for extracting IP addresses from {@link InetSocketAddress} objects.
 *
 * <p>Provides a safe way to get the host address string from a socket address, handling null cases
 * gracefully.
 */
@UtilityClass
public class IpExtractor {

  public static String extract(final InetSocketAddress remoteAddress) {
    if (remoteAddress == null) return null;

    final InetAddress address = remoteAddress.getAddress();
    if (address == null) return null;

    return address.getHostAddress();
  }
}
