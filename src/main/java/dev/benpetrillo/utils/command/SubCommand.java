package dev.benpetrillo.utils.command;

public abstract class SubCommand extends Command {
    public SubCommand(String label, String description) {
        super(label, description);
    }

    public abstract void execute(CommandMessage message);
}