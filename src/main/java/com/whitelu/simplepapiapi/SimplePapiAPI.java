package com.whitelu.simplepapiapi;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public final class SimplePapiAPI extends JavaPlugin {

    private int port;
    private WebServer server;

    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        reload();
        server = new WebServer(this, port);
    }

    private void reload() {
        reloadConfig();
        port = getConfig().getInt("port");
    }

    @Override
    public void onDisable() {
        if (server != null) {
            server.stop();
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("simplepapiapi.admin") || args.length < 1 || !args[0].equalsIgnoreCase("reload")) {
            return true;
        }
        int prevPort = getConfig().getInt("port");
        reload();
        if (port != prevPort) {
            server.restart(port);
        }
        sender.sendMessage("SimplePapiAPI reloaded");
        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return Collections.singletonList("reload");
        }
        return Collections.emptyList();
    }
}