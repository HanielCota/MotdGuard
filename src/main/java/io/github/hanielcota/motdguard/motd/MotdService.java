package io.github.hanielcota.motdguard.motd;

import com.velocitypowered.api.proxy.server.ServerPing;

public final class MotdService implements MotdProvider {

    private final MotdBuilder builder;
    private final MotdFetcher fetcher;

    public MotdService(final MotdBuilder builder, final MotdFetcher fetcher) {
        this.builder = builder;
        this.fetcher = fetcher;
    }

    @Override
    public ServerPing buildMotd(final ServerPing original) {
        return builder.build(original);
    }

    @Override
    public void refresh() {
        if (fetcher instanceof MotdFetcherImpl impl) {
            impl.refresh();
        }
    }
}