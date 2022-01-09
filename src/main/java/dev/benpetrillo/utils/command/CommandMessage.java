package dev.benpetrillo.utils.command;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class CommandMessage implements Cloneable {

    private Command command = null;
    private boolean isSlash = false;
    private SlashCommandEvent slashEvent = null;
    private Message message = null;

    private boolean ephemeral = false;
    private boolean dmEphemeral = false;
    private boolean isDeferred = false;

    // private final Map<String, Object> arguments = new HashMap<>();
    private final List<Object> arguments = new ArrayList<>();
    private final Map<String, Object> mappedArguments = new HashMap<>();
    private final Member sender;
    private final TextChannel channel;
    private final Guild guild;

    public CommandMessage(SlashCommandEvent event) {
        sender = event.getMember();
        channel = event.getTextChannel();
        isSlash = true; slashEvent = event;
        guild = event.getGuild();
        if (CommandManager.getCommand(event.getName(), true) != null) {
            command = (Command) CommandManager.getCommand(event.getName(), true);
        }
        if (command == null) return;
        if (event.getOption("action") != null) {
            SubCommand subCommand = command.getSubCommand(event.getOption("action").getAsString());
            if (subCommand instanceof Arguments) {
                Map<String, OptionType> arguments = ((Arguments) subCommand).getArgumentOptions();
                for (Map.Entry<String, OptionType> entry : arguments.entrySet()) {
                    if (event.getOption(entry.getKey()) != null) {
                        switch(entry.getValue()) {
                            case STRING -> {
                                this.arguments.add(event.getOption(entry.getKey()).getAsString());
                                this.mappedArguments.put(entry.getKey(), event.getOption(entry.getKey()).getAsString());
                            }
                            case INTEGER -> {
                                this.arguments.add(event.getOption(entry.getKey()).getAsLong());
                                this.mappedArguments.put(entry.getKey(), event.getOption(entry.getKey()).getAsLong());
                            }
                            case MENTIONABLE -> {
                                this.arguments.add(event.getOption(entry.getKey()).getAsMentionable().getAsMention());
                                this.mappedArguments.put(entry.getKey(), event.getOption(entry.getKey()).getAsMentionable().getAsMention());
                            }
                            case BOOLEAN -> {
                                this.arguments.add(event.getOption(entry.getKey()).getAsBoolean());
                                this.mappedArguments.put(entry.getKey(), event.getOption(entry.getKey()).getAsBoolean());
                            }
                        }
                    }
                }
            }
        } else if (command instanceof Arguments) {
            Map<String, OptionType> arguments = ((Arguments) command).getArgumentOptions();
            for (Map.Entry<String, OptionType> entry : arguments.entrySet()) {
                if (event.getOption(entry.getKey()) != null) {
                    switch (entry.getValue()) {
                        case STRING -> {
                            this.arguments.add(event.getOption(entry.getKey()).getAsString());
                            this.mappedArguments.put(entry.getKey(), event.getOption(entry.getKey()).getAsString());
                        }
                        case INTEGER -> {
                            this.arguments.add(event.getOption(entry.getKey()).getAsLong());
                            this.mappedArguments.put(entry.getKey(), event.getOption(entry.getKey()).getAsLong());
                        }
                        case MENTIONABLE -> {
                            this.arguments.add(event.getOption(entry.getKey()).getAsMentionable().getAsMention());
                            this.mappedArguments.put(entry.getKey(), event.getOption(entry.getKey()).getAsMentionable().getAsMention());
                        }
                        case BOOLEAN -> {
                            this.arguments.add(event.getOption(entry.getKey()).getAsBoolean());
                            this.mappedArguments.put(entry.getKey(), event.getOption(entry.getKey()).getAsBoolean());
                        }
                    }
                }
            }
        }
    }

    public CommandMessage(Message message, TextChannel channel, List<String> args, Command command) {
        sender = message.getMember(); this.message = message;
        this.channel = channel; guild = message.getGuild();
        this.command = command;
        if (command instanceof Arguments) {
            for(int i = 0; i < args.size(); i++) {
                SlashArgument argument = (SlashArgument) ((Arguments) command).getArguments().toArray()[i];
                mappedArguments.put(argument.reference, args.get(i));
            }
        }
    }

    /**
     * Get all command arguments.
     * @return List<Object>
     */

    public List<Object> getArguments() {
        return new ArrayList<>(mappedArguments.values());
    }

    /**
     * Get the guild member that ran this command.
     * @return Member
     */

    public Member getMember() {
        return sender;
    }

    /**
     * Get the text channel in which this command was run.
     * @return TextChannel
     */

    public TextChannel getChannel() {
        return channel;
    }

    /**
     * Get the guild in which this command was run.
     * @return Guild
     */

    public Guild getGuild() {
        return guild;
    }

    /**
     * Get the SlashCommandEvent for this interaction.
     * @return SlashCommandEvent
     */

    public SlashCommandEvent getSlashEvent() {
        return slashEvent;
    }

    /**
     * Whether the command is a slash command or not.
     * @return boolean
     */

    public boolean isSlash() {
        return isSlash;
    }

    public CommandMessage setEphemeral(boolean sendToDMs) {
        ephemeral = true; dmEphemeral = sendToDMs;
        return this;
    }

    public CommandMessage setEphemeral() {
        ephemeral = true;
        return this;
    }

    public CommandMessage deferReply() {
        if (isSlash) {
            slashEvent.deferReply(ephemeral).queue();
            isDeferred = true;
        }
        return this;
    }

    public void sendReply(String reply) {
        if (isSlash) {
            channel.sendMessage(reply).queue();
        } else {
            message.reply(reply).queue();
        }
    }

    public void sendReply(String reply, boolean mention) {
        if (isSlash) channel.sendMessage(reply).queue();
        else message.reply(reply).mentionRepliedUser(mention).queue();
    }

    public void reply(String replyWith) {
        reply(replyWith, false);
    }

    public void reply(MessageEmbed replyWith) {
        reply(replyWith, false);
    }

    public void reply(String replyWith, boolean mention) {
        if (isSlash) {
            if (isDeferred) slashEvent.getHook().sendMessage(replyWith).queue();
            else slashEvent.reply(replyWith).setEphemeral(ephemeral).queue();
        } else {
            if (dmEphemeral) sender.getUser().openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage(replyWith).queue();
                });
            else message.reply(replyWith).mentionRepliedUser(mention).queue();
        }
    }

    public void reply(MessageEmbed replyWith, boolean mention) {
        if (isSlash) {
            if (isDeferred) slashEvent.getHook().sendMessageEmbeds(replyWith).queue();
            else slashEvent.replyEmbeds(replyWith).setEphemeral(ephemeral).queue();
        } else {
            if (dmEphemeral) sender.getUser().openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessageEmbeds(replyWith).queue();
                });
            else message.replyEmbeds(replyWith).mentionRepliedUser(mention).queue();
        }
    }

    public void sendMessage(String message) {
        channel.sendMessage(message).queue();
    }

    public void sendMessage(MessageEmbed message) {
        channel.sendMessageEmbeds(message, new MessageEmbed[]{}).queue();
    }

    public void execute(Consumer<CommandMessage> consumer, long after, TimeUnit timeUnit) {
        try {
            CommandMessage messageClone = (CommandMessage) this.clone();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    consumer.accept(messageClone);
                }
            }, timeUnit.toMillis(after));
        } catch (CloneNotSupportedException ignored) { }
    }
}