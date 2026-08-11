package com.randomidentity;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.bukkit.profile.PlayerTextures;
import org.bukkit.profile.PlayerTextures.SkinModel;

import com.destroystokyo.paper.profile.PlayerProfile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class SkinManager {

private final RandomIdentityPlugin plugin;

private final Map<UUID, PlayerProfile> originalProfiles = new HashMap<>();

private final List<DefaultSkin> defaultSkins = List.of(

    new DefaultSkin(
        "Steve",
        "https://textures.minecraft.net/texture/31f477eb1a7beee631c2ca64d06f8f68fa93a3386d04452ab27f43acdf1b60cb",
        SkinModel.CLASSIC
    ),

    new DefaultSkin(
        "Alex",
        "https://textures.minecraft.net/texture/46acd06e8483b176e8ea39fc12fe105eb3a2a4970f5100057e9d84d4b60bdfa7",
        SkinModel.SLIM
    ),

    new DefaultSkin(
        "Noor",
        "https://textures.minecraft.net/texture/6c160fbd16adbc4bff2409e70180d911002aebcfa811eb6ec3d1040761aea6dd",
        SkinModel.SLIM
    ),

    new DefaultSkin(
        "Sunny",
        "https://textures.minecraft.net/texture/a3bd16079f764cd541e072e888fe43885e711f98658323db0f9a6045da91ee7a",
        SkinModel.CLASSIC
    ),

    new DefaultSkin(
        "Ari",
        "https://textures.minecraft.net/texture/4c05ab9e07b3505dc3ec11370c3bdce5570ad2fb2b562e9b9dd9cf271f81aa44",
        SkinModel.CLASSIC
    ),

    new DefaultSkin(
        "Zuri",
        "https://textures.minecraft.net/texture/f5dddb41dcafef616e959c2817808e0be741c89ffbfed39134a13e75b811863d",
        SkinModel.CLASSIC
    ),

    new DefaultSkin(
        "Makena",
        "https://textures.minecraft.net/texture/7cb3ba52ddd5cc82c0b050c3f920f87da36add80165846f479079663805433db",
        SkinModel.SLIM
    ),

    new DefaultSkin(
        "Kai",
        "https://textures.minecraft.net/texture/e5cdc3243b2153ab28a159861be643a4fc1e3c17d291cdd3e57a7f370ad676f3",
        SkinModel.CLASSIC
    ),

    new DefaultSkin(
        "Efe",
        "https://textures.minecraft.net/texture/fece7017b1bb13926d1158864b283b8b930271f80a90482f174cca6a17e88236",
        SkinModel.SLIM
    )
);

public SkinManager(RandomIdentityPlugin plugin) {
    this.plugin = plugin;
}

public void applyRandomSkin(Player player) {

    if (player == null || !player.isOnline()) {
        return;
    }

    if (!plugin.getConfig().getBoolean("skin.enabled", true)) {
        return;
    }

    PlayerProfile original =
        (PlayerProfile) player.getPlayerProfile();

    originalProfiles.putIfAbsent(
        player.getUniqueId(),
        original
    );

    DefaultSkin selected =
        defaultSkins.get(
            ThreadLocalRandom.current().nextInt(
                defaultSkins.size()
            )
        );

    applySkin(player, selected);
}

private void applySkin(
    Player player,
    DefaultSkin skin
) {

    try {

        URL textureUrl =
            new URL(skin.textureUrl);

        PlayerProfile profile =
            (PlayerProfile) player.getPlayerProfile();

        PlayerTextures textures =
            profile.getTextures();

        textures.clear();

        textures.setSkin(
            textureUrl,
            skin.model
        );

        profile.setTextures(
            textures
        );

        refreshPlayerProfile(
            player,
            profile
        );

        plugin.getLogger().info(
            "Applied skin "
            + skin.name
            + " to "
            + player.getName()
        );

    } catch (MalformedURLException exception) {

        plugin.getLogger().warning(
            "Invalid texture URL for "
            + skin.name
        );
    }
}

private void refreshPlayerProfile(
    Player player,
    PlayerProfile profile
) {

    Bukkit.getScheduler().runTask(
        plugin,
        () -> {

            if (!player.isOnline()) {
                return;
            }

            player.setPlayerProfile(profile);

            Bukkit.getScheduler().runTaskLater(
                plugin,
                () -> refreshVisiblePlayers(player),
                2L
            );
        }
    );
}

private void refreshVisiblePlayers(
    Player player
) {

    if (!player.isOnline()) {
        return;
    }

    for (Player viewer : Bukkit.getOnlinePlayers()) {

        if (viewer.equals(player)) {
            continue;
        }

        if (!viewer.canSee(player)) {
            continue;
        }

        viewer.hidePlayer(
            plugin,
            player
        );

        Bukkit.getScheduler().runTaskLater(
            plugin,
            () -> {

                if (viewer.isOnline() &&
                    player.isOnline()) {

                    viewer.showPlayer(
                        plugin,
                        player
                    );
                }

            },
            1L
        );
    }
}

public void rerollSkin(Player player) {

    if (player == null || !player.isOnline()) {
        return;
    }

    applyRandomSkin(player);
}

public void resetSkin(Player player) {

    if (player == null || !player.isOnline()) {
        return;
    }

    PlayerProfile original =
        originalProfiles.get(
            player.getUniqueId()
        );

    if (original == null) {

        plugin.getLogger().warning(
            "No original skin was saved for "
            + player.getName()
        );

        return;
    }

    Bukkit.getScheduler().runTask(
        plugin,
        () -> {

            if (!player.isOnline()) {
                return;
            }

            player.setPlayerProfile(original);

            Bukkit.getScheduler().runTaskLater(
                plugin,
                () -> refreshVisiblePlayers(player),
                2L
            );

            plugin.getLogger().info(
                "Reset skin for "
                + player.getName()
            );
        }
    );
}

public void playerQuit(Player player) {
}

private static final class DefaultSkin {

    private final String name;
    private final String textureUrl;
    private final SkinModel model;

    private DefaultSkin(
        String name,
        String textureUrl,
        SkinModel model
    ) {
        this.name = name;
        this.textureUrl = textureUrl;
        this.model = model;
    }
}

}