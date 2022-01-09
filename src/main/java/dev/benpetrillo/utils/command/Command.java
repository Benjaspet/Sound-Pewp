package dev.benpetrillo.utils.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.util.*;

public abstract class Command {

    private final String label, description;
    private final List<String> interactiveArguments;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public Command(String label, String description) {
        this(label, description, Collections.emptyList());
    }

    public Command(String label, String description, List<String> interactiveArguments) {
        this.label = label; this.description = description;
        this.interactiveArguments = interactiveArguments;
    }

    public final void registerSubCommand(SubCommand command) {
        subCommands.put(command.getLabel(), command);
    }

    public final SubCommand getSubCommand(String label) {
        return subCommands.getOrDefault(label, new SubCommand("default", "Default sub-command.") {
            @Override
            public void execute(CommandMessage message) {
                message.reply("Default sub-command.");
            }
        });
    }

    public final Map<String, SubCommand> getSubCommands() {
        return subCommands;
    }

    public final String getLabel() {
        return this.label;
    }

    public final String getDescription() {
        return this.description;
    }

    public final void prepareForExecution(List<String> arguments, Member sender, Message message, MessageChannel channel, boolean skipArgs) {
        List<String> args = new ArrayList<>(arguments);
        boolean executeBase = true;
        for (String argument : arguments) {
            if (subCommands.containsKey(argument)) {
                executeBase = false;
                args.remove(argument);
                getSubCommand(argument).prepareForExecution(args, sender, message, channel, false);
            }
        }
        if (executeBase) {
            if (interactiveArguments.size() == 0 || skipArgs) {
                execute(new CommandMessage(message, message.getTextChannel(), arguments, this));
            } else {
                new InteractiveArguments(message, sender, label, interactiveArguments);
            }
        }
    }

    public final void prepareForExecution(SlashCommandEvent event) {
        OptionMapping choice = event.getOption("action");
        if (choice != null) {
            if (subCommands.containsKey(choice.getAsString())) {
                getSubCommand(choice.getAsString()).executeSlash(event);
            } else executeSlash(event);
        } else executeSlash(event);
    }

    @Deprecated
    public void execute(Member sender, Message message, MessageChannel channel, List<String> arguments) {
        CommandMessage commandMessage = new CommandMessage(message, message.getTextChannel(), arguments, this);
        execute(commandMessage);
    }

    public abstract void execute(CommandMessage commandMessage);

    /**
     * Not necessary abstract method.
     */
    @Deprecated
    public void executeSlash(SlashCommandEvent event) {CommandMessage commandMessage = new CommandMessage(event);
        execute(commandMessage);
    }

    /**
     * Slash-command component.
     */
    public OptionData[] getOptions() {
        return null;
    }
}
