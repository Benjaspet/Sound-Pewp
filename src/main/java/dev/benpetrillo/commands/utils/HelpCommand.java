package dev.benpetrillo.commands.utils;

import dev.benpetrillo.Config;
import dev.benpetrillo.utils.command.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class HelpCommand extends Command implements Arguments {

    public HelpCommand() {
        super(
                "help",
                "Gives you a long message with command descriptions."
        );
    }

    @Override
    public void execute(CommandMessage commandMessage) {
        List<Object> arguments = commandMessage.getArguments();

        String prefix = Config.get("PREFIX");
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(new Color(80, 188, 236));
        if(arguments.size() > 0) {
            if(CommandManager.getCommand(arguments.get(0).toString()) == null) {
                commandMessage
                        .setEphemeral()
                        .reply("That's not a valid command!");
                return;
            }

            Command command = (Command) CommandManager.getCommand(arguments.get(0).toString());
            builder.setTitle("Sub Commands for: " + command.getLabel());

            if(command.getSubCommands().size() > 0) {
                for(SubCommand subCommand : command.getSubCommands().values()) {
                    builder.addField(prefix + command.getLabel() + " " + subCommand.getLabel(), subCommand.getDescription(), false);
                }
            } else {
                builder.addField(prefix + command.getLabel(), "There are no sub-commands for this command!", false);
            }
        } else {
            builder.setTitle("Sound Pewp | All Commands");

            for(Command command : CommandManager.getCommands().values()) {
                builder.addField(prefix + command.getLabel(), command.getDescription(), false);
            }
        }

        commandMessage
                .setEphemeral()
                .reply(builder.build());
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[]{
                new OptionData(OptionType.STRING, "command", "Lists the sub-commands for that command.")
        };
    }

    @Override
    public Collection<SlashArgument> getArguments() {
        SlashArgument argument = new SlashArgument(
                "command", "The command to list sub-commands for.",
                "command", OptionType.STRING
        );

        return List.of(argument);
    }

    @Override
    public Map<String, OptionType> getArgumentOptions() {
        return Map.of(
                "command", OptionType.STRING
        );
    }
}