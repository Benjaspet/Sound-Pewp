package dev.benpetrillo.utils.command;

import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.*;

public interface Arguments {

    Collection<SlashArgument> getArguments();
    Map<String, OptionType> getArgumentOptions();

}