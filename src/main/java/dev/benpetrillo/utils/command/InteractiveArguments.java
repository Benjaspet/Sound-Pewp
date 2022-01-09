package dev.benpetrillo.utils.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class InteractiveArguments {

    private static final HashMap<String, Integer> index = new HashMap<>();

    private final List<String> questions;
    private final Map<Integer, String> answers = new HashMap<>();
    private final Member member;
    private final String commandLabel;
    private final MessageChannel channel;
    private final Message message;

    public InteractiveArguments(Message replyTo, Member member, String commandLabel, List<String> questions) {
        this.questions = questions; this.member = member; this.commandLabel = commandLabel;
        this.channel = replyTo.getChannel(); this.message = replyTo;
        start(replyTo); InteractiveArgumentsManager.addInteraction(this);
    }

    public void start(Message replyTo) {
        if(index.containsKey(member.getId())) return;
        index.put(member.getId(), 0);

        replyTo.reply(
                questions.get(index.get(member.getId()))
        ).queue();
    }

    public void continueInteraction(Message response) {
        answers.put(
                index.get(member.getId()),
                response.getContentRaw()
        ); index.put(member.getId(), index.get(member.getId()) + 1);

        if((index.get(member.getId()) + 1) > questions.size()) {
            InteractiveArgumentsManager.removeInteraction(this);
            index.remove(member.getId());
            ((Command) CommandManager.getCommand(commandLabel))
                    .prepareForExecution(new ArrayList<>(answers.values()), member, message, response.getChannel(), true);
        } else {
            response.reply(
                    questions.get(index.get(member.getId()))
            ).mentionRepliedUser(false).queue();
        }
    }

    public Member getMember() {
        return member;
    }

    public MessageChannel getChannel() {
        return channel;
    }
}