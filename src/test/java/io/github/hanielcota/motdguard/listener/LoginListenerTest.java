package io.github.hanielcota.motdguard.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import io.github.hanielcota.motdguard.maintenance.MaintenanceManager;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

class LoginListenerTest {

    private static final Component KICK = Component.text("Kick");

    private LoginEvent eventWithPermission(final boolean bypass) {
        final var player = mock(Player.class);
        when(player.hasPermission("motdguard.bypass")).thenReturn(bypass);
        when(player.getUsername()).thenReturn("Steve");

        final var event = mock(LoginEvent.class);
        when(event.getPlayer()).thenReturn(player);
        return event;
    }

    @Test
    void shouldAllowWhenMaintenanceDisabled() {
        final var maintenanceManager = mock(MaintenanceManager.class);
        when(maintenanceManager.getState()).thenReturn(new MaintenanceManager.State(false, KICK));
        final var event = eventWithPermission(false);

        new LoginListener(maintenanceManager).onLogin(event);

        verify(event, never()).setResult(any(ResultedEvent.ComponentResult.class));
    }

    @Test
    void shouldAllowPlayerWithBypassPermission() {
        final var maintenanceManager = mock(MaintenanceManager.class);
        when(maintenanceManager.getState()).thenReturn(new MaintenanceManager.State(true, KICK));
        final var event = eventWithPermission(true);

        new LoginListener(maintenanceManager).onLogin(event);

        verify(event, never()).setResult(any(ResultedEvent.ComponentResult.class));
    }

    @Test
    void shouldDenyPlayerWithoutBypassDuringMaintenance() {
        final var maintenanceManager = mock(MaintenanceManager.class);
        when(maintenanceManager.getState()).thenReturn(new MaintenanceManager.State(true, KICK));
        final var event = eventWithPermission(false);

        new LoginListener(maintenanceManager).onLogin(event);

        verify(event).setResult(any(ResultedEvent.ComponentResult.class));
    }
}
