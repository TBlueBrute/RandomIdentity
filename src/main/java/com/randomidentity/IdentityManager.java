package com.randomidentity;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class IdentityManager implements Listener {

private final RandomIdentityPlugin plugin;
private final NameGenerator nameGenerator;
private final SkinManager skinManager;

private final Map<UUID, String> identities = new HashMap<>();

public IdentityManager(
        RandomIdentityPlugin plugin,
        NameGenerator nameGenerator,
        SkinManager skinManager
) {
    this.plugin = plugin;
    this.nameGenerator = nameGenerator;
    this.skinManager = skinManager;
}

@EventHandler(priority = EventPriority.HIGHEST)
public void onPlayerJoin(PlayerJoinEvent event) {

    Player player = event.getPlayer();

    if (!isAutoIdentityEnabled()) {

        event.joinMessage(
                Component.text(
                        player.getName() +
                                " joined the game"
                )
        );

        return;
    }

    String newName = generateIdentity();

    identities.put(
            player.getUniqueId(),
            newName
    );

    applyDisplayName(
            player,
            newName
    );

    if (plugin.getConfig().getBoolean(
            "skin.enabled",
            true
    )) {

        skinManager.applyRandomSkin(player);
    }

    if (plugin.getConfig().getBoolean(
            "features.change-join-message",
            true
    )) {

        Component joinMessage =
                Component.text(
                        newName + " joined the game",
                        NamedTextColor.YELLOW
                );

        event.joinMessage(joinMessage);

    } else {

        event.joinMessage(
                Component.text(
                        player.getName() +
                                " joined the game"
                )
        );
    }
}

@EventHandler(priority = EventPriority.HIGHEST)
public void onPlayerQuit(PlayerQuitEvent event) {

    Player player = event.getPlayer();

    String identity =
            identities.get(
                    player.getUniqueId()
            );

    if (identity != null &&
            plugin.getConfig().getBoolean(
                    "features.change-quit-message",
                    true
            )) {

        Component quitMessage =
                Component.text(
                        identity + " left the game",
                        NamedTextColor.YELLOW
                );

        event.quitMessage(
                quitMessage
        );

    } else {

        event.quitMessage(
                Component.text(
                        player.getName() +
                                " left the game"
                )
        );
    }

    identities.remove(
            player.getUniqueId()
    );

    skinManager.playerQuit(player);
}

private String generateIdentity() {

    Set<String> usedNames =
            new HashSet<>();

    for (String identity :
            identities.values()) {

        usedNames.add(
                identity.toLowerCase()
        );
    }

    return nameGenerator.generate(
            usedNames
    );
}

private void applyDisplayName(
        Player player,
        String name
) {

    if (plugin.getConfig().getBoolean(
            "features.change-chat-name",
            true
    )) {

        player.displayName(
                Component.text(name)
        );
    }

    if (plugin.getConfig().getBoolean(
            "features.change-tab",
            true
    )) {

        player.playerListName(
                Component.text(name)
        );
    }

    player.customName(
            Component.text(name)
    );
}

public String getIdentity(
        Player player
) {

    if (player == null) {
        return null;
    }

    return identities.get(
            player.getUniqueId()
    );
}

public void reroll(
        Player player
) {

    if (player == null ||
            !player.isOnline()) {

        return;
    }

    String newName =
            generateIdentity();

    identities.put(
            player.getUniqueId(),
            newName
    );

    applyDisplayName(
            player,
            newName
    );

    if (plugin.getConfig().getBoolean(
            "skin.enabled",
            true
    )) {

        skinManager.rerollSkin(player);
    }
}

public void resetIdentity(
        Player player
) {

    if (player == null ||
            !player.isOnline()) {

        return;
    }

    identities.remove(
            player.getUniqueId()
    );

    player.displayName(
            Component.text(
                    player.getName()
            )
    );

    player.playerListName(
            Component.text(
                    player.getName()
            )
    );

    player.customName(null);

    if (plugin.getConfig().getBoolean(
            "skin.enabled",
            true
    )) {

        skinManager.resetSkin(player);
    }
}

public boolean isAutoIdentityEnabled() {

    return plugin.getConfig().getBoolean(
            "features.auto-identity",
            true
    );
}

public boolean toggleAutoIdentity() {

    boolean newValue =
            !isAutoIdentityEnabled();

    plugin.getConfig().set(
            "features.auto-identity",
            newValue
    );

    plugin.saveConfig();

    return newValue;
}

public Map<UUID, String> getIdentities() {

    return identities;
}

}