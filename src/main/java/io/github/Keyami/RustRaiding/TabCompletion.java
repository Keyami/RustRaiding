package io.github.Keyami.RustRaiding;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TabCompletion implements TabCompleter {
    public static final String[] rr = {"satchel", "c4", "rpg", "rocket", "c4bl", "satchelbl", "rocketbl", "rpgbl"};

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final List<String> completions = new ArrayList<String>();
        StringUtil.copyPartialMatches(args[0], Arrays.asList(rr), completions);
        Collections.sort(completions);
        return completions;

    }
}