package io.github.hanielcota.motdguard.motd;

import com.velocitypowered.api.proxy.server.ServerPing;

public final class MotdBuilderImpl implements MotdBuilder {

  private final MotdFetcher fetcher;

  public MotdBuilderImpl(final MotdFetcher fetcher) {
    this.fetcher = fetcher;
  }

  @Override
  public ServerPing build(final ServerPing original) {
    return original.asBuilder().description(fetcher.fetch()).build();
  }
}
