package io.github.hanielcota.motdguard.ratelimit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class IpExtractorTest {

  @Test
  void shouldReturnEmptyForNullAddress() {
    assertTrue(IpExtractor.extract(null).isEmpty());
  }

  @Test
  void shouldExtractIpv4Address() {
    final var address = new InetSocketAddress("192.168.1.1", 25565);

    final Optional<String> result = IpExtractor.extract(address);

    assertTrue(result.isPresent());
    assertEquals("192.168.1.1", result.get());
  }

  @Test
  void shouldExtractIpv6AddressWithoutScope() throws Exception {
    final InetAddress inet6 = InetAddress.getByName("2001:db8::1");
    final var address = new InetSocketAddress(inet6, 25565);

    final Optional<String> result = IpExtractor.extract(address);

    assertTrue(result.isPresent());
    assertEquals("2001:db8:0:0:0:0:0:1", result.get());
  }

  @Test
  void shouldNormalizeScopedIpv6Address() throws Exception {
    final InetAddress scoped = InetAddress.getByName("fe80:0:0:0:0:0:0:1");
    final var address = new InetSocketAddress(scoped, 25565);

    final Optional<String> result = IpExtractor.extract(address);

    assertTrue(result.isPresent());
    assertEquals("fe80:0:0:0:0:0:0:1", result.get());
  }

  @Test
  void shouldFallbackToHostStringForUnresolvedAddress() {
    final var address = InetSocketAddress.createUnresolved("unresolved-host", 25565);

    final Optional<String> result = IpExtractor.extract(address);

    assertTrue(result.isPresent());
    assertEquals("unresolved-host", result.get());
  }
}
