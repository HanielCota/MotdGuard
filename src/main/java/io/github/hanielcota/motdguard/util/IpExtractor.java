package io.github.hanielcota.motdguard.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public final class IpExtractor {

  private IpExtractor() {}

  public static String extract(final InetSocketAddress remoteAddress) {
    if (remoteAddress == null) return null;
    final InetAddress address = remoteAddress.getAddress();
    if (address == null) return null;
    return address.getHostAddress();
  }
}
