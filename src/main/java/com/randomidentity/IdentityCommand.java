package com.randomidentity;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class IdentityCommand
implements CommandExecutor, TabCompleter {

private final RandomIdentityPlugin plugin;
private final IdentityManager identityManager;

public IdentityCommand(
        RandomIdentityPlugin plugin,
        IdentityManager identityManager
) {
    this.plugin = plugin;
    this.identityManager = identityManager;
}

@Override
public boolean onCommand(
        CommandSender sender,
        Command command,
        String label,
        String[] args
) {

    if (!sender.hasPermission(
            "randomidentity.admin"
    )) {

        sender.sendMessage(
                Component.text(
                        "You don't have permission to use this command."
                )
        );

        return true;
    }

    if (args.length == 0) {

        sendUsage(sender);

        return true;
    }

    switch (args[0].toLowerCase()) {

        case "reroll" -> {

            if (args.length < 2) {

                sendUsage(sender);

                return true;
            }

            List<Player> players =
                    getTargetPlayers(
                            sender,
                            args[1]
                    );

            if (players.isEmpty()) {

                sender.sendMessage(
                        Component.text(
                                "No players found."
                        )
                );

                return true;
            }

            for (Player player : players) {

                identityManager.reroll(
                        player
                );
            }

            sender.sendMessage(
                    Component.text(
                            "Rerolled " +
                                    players.size() +
                                    " player(s)."
                    )
            );

            return true;
        }

        case "reset" -> {

            if (args.length < 2) {

                sendUsage(sender);

                return true;
            }

            List<Player> players =
                    getTargetPlayers(
                            sender,
                            args[1]
                    );

            if (players.isEmpty()) {

                sender.sendMessage(
                        Component.text(
                                "No players found."
                        )
                );

                return true;
            }

            for (Player player : players) {

                identityManager.resetIdentity(
                        player
                );
            }

            sender.sendMessage(
                    Component.text(
                            "Reset " +
                                    players.size() +
                                    " player(s)."
                    )
            );

            return true;
        }

        case "toggle" -> {

            boolean enabled =
                    identityManager.toggleAutoIdentity();

            if (enabled) {

                sender.sendMessage(
                        Component.text(
                                "Automatic identity is now ENABLED."
                        )
                );

            } else {

                sender.sendMessage(
                        Component.text(
                                "Automatic identity is now DISABLED."
                        )
                );
            }

            return true;
        }

        case "info" -> {

            if (args.length < 2) {

                sendUsage(sender);

                return true;
            }

            Player player =
                    Bukkit.getPlayer(args[1]);

            if (player == null) {

                sender.sendMessage(
                        Component.text(
                                "Player not found."
                        )
                );

                return true;
            }

            String identity =
                    identityManager.getIdentity(
                            player
                    );

            sender.sendMessage(
                    Component.text(
                            "Real username: " +
                                    player.getName()
                    )
            );

            sender.sendMessage(
                    Component.text(
                            "Random identity: " +
                                    (
                                            identity == null
                                                    ? "None"
                                                    : identity
                                    )
                    )
            );

            return true;
        }

        case "reload" -> {

            plugin.reloadConfig();

            sender.sendMessage(
                    Component.text(
                            "RandomIdentity configuration reloaded."
                    )
            );

            return true;
        }

        default -> {

            sendUsage(sender);

            return true;
        }
    }
}

private List<Player> getTargetPlayers(
        CommandSender sender,
        String target
) {

    List<Player> players =
            new ArrayList<>();

    if (target.startsWith("@")) {

        try {

            List<Entity> entities =
                    Bukkit.selectEntities(
                            sender,
                            target
                    );

            for (Entity entity : entities) {

                if (entity instanceof Player player) {

                    players.add(player);
                }
            }

        } catch (IllegalArgumentException exception) {

            return players;
        }

        return players;
    }

    Player player =
            Bukkit.getPlayerExact(target);

    if (player != null) {

        players.add(player);
    }

    return players;
}

private void sendUsage(
        CommandSender sender
) {

    sender.sendMessage(
            Component.text(
                    "/identity reroll <player|@a>"
            )
    );

    sender.sendMessage(
            Component.text(
                    "/identity reset <player|@a>"
            )
    );

    sender.sendMessage(
            Component.text(
                    "/identity toggle"
            )
    );

    sender.sendMessage(
            Component.text(
                    "/identity info <player>"
            )
    );

    sender.sendMessage(
            Component.text(
                    "/identity reload"
            )
    );
}

@Override
public List<String> onTabComplete(
        CommandSender sender,
        Command command,
        String alias,
        String[] args
) {

    if (args.length == 1) {

        List<String> options =
                List.of(
                        "reroll",
                        "reset",
                        "toggle",
                        "info",
                        "reload"
                );

        return options.stream()
                .filter(option ->
                        option.toLowerCase()
                                .startsWith(
                                        args[0].toLowerCase()
                                )
                )
                .toList();
    }

    if (args.length == 2 &&
            (
                    args[0].equalsIgnoreCase("reroll") ||
                    args[0].equalsIgnoreCase("reset") ||
                    args[0].equalsIgnoreCase("info")
            )
    ) {

        List<String> players =
                new ArrayList<>();

        players.add("@a");

        for (Player player :
                Bukkit.getOnlinePlayers()) {

            players.add(
                    player.getName()
            );
        }

        return players;
    }

    return List.of();
}

}