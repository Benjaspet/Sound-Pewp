package dev.benpetrillo.commands.utils;

import dev.benpetrillo.Config;
import dev.benpetrillo.SoundPewp;
import dev.benpetrillo.utils.command.*;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class DeployCommand extends Command implements Arguments {

    private static int deployed, maxDeployed;

    public DeployCommand() {
        super(
                "deploy",
                "Deploy slash-commands to this guild."
        );
    }

    @Override
    public void execute(CommandMessage commandMessage) {
        Member sender = commandMessage.getMember();
        TextChannel channel = commandMessage.getChannel();

        if (!sender.getId().matches("252090676068614145")) {
            commandMessage.reply("You have to be `Magix#1000` to deploy slash-commands!");
            return;
        }

        if(commandMessage.isSlash())
            commandMessage.deferReply();
        channel.sendTyping()
                .queue();

        deployed = 0;
        maxDeployed = CommandManager.getCommands().size();

        boolean upsertToGuild = Boolean.parseBoolean(Config.get("DEPLOY-GUILD"));
        CommandManager.getCommands().forEach((label, cmd) -> {
            CommandCreateAction action;
            if(upsertToGuild)
                action = commandMessage.getGuild().upsertCommand(label, cmd.getDescription());
            else action = SoundPewp.getJda().upsertCommand(label, cmd.getDescription());

            List<String> optionList = new ArrayList<>();

            if(cmd.getOptions() != null)
                action = action.addOptions(cmd.getOptions());
            else {
                if(cmd.getSubCommands().size() > 0) {
                    OptionData options = new OptionData(OptionType.STRING, "action", "Execute another sub-command/action of this command.", false);

                    for(SubCommand subCmd : cmd.getSubCommands().values()) {
                        options = options.addChoice(subCmd.getLabel(), subCmd.getLabel());
                        // Slash arguments.
                        if(subCmd instanceof Arguments) {
                            for(SlashArgument argument : ((Arguments) subCmd).getArguments()) {
                                if(!optionList.contains(argument.label)) {
                                    action = action.addOption(argument.argumentType, argument.label, argument.description, false);
                                    optionList.add(argument.label);
                                }
                            }
                        }

                        // Slash-aliases.
                        if(subCmd instanceof Alias) {
                            CommandCreateAction subAction = commandMessage.getGuild().upsertCommand(((Alias) subCmd).getSlashAlias(), subCmd.getDescription());
                            if(subCmd instanceof Arguments)
                                for(SlashArgument argument : ((Arguments) subCmd).getArguments())
                                    subAction = subAction.addOption(argument.argumentType, argument.label, argument.description, false);
                            subAction.queue((command) -> System.out.println("Sub-Command alias " + ((Alias) subCmd).getSlashAlias() + " was deployed."));
                        }
                    }
                    action = action.addOptions(options);
                }

                if(cmd instanceof Arguments) {
                    for(SlashArgument argument : ((Arguments) cmd).getArguments()) {
                        if(!optionList.contains(argument.label)) {
                            action = action.addOption(argument.argumentType, argument.label, argument.description, false);
                            optionList.add(argument.label);
                        }
                    }
                }
            }

            action.queue((command) -> {
                System.out.println("Command " + command.getName() + " was deployed.");
                deployed++;

                if(deployed == maxDeployed)
                    commandMessage.reply("Successfully deployed slash-commands!");
            });
        });
    }

    @Override
    public Collection<SlashArgument> getArguments() {
        return null;
    }

    @Override
    public Map<String, OptionType> getArgumentOptions() {
        return null;
    }
}