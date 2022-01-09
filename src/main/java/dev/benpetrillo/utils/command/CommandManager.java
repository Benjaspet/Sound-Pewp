/*
 * Copyright © 2022 Ben Petrillo, KingRainbow44. All rights reserved.
 *
 * Project licensed under the MIT License: https://www.mit.edu/~amini/LICENSE.md
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * All portions of this software are available for public use, provided that
 * credit is given to the original author(s).
 */

package dev.benpetrillo.utils.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class CommandManager {

    private final static HashMap<String, Command> commands = new HashMap<>();
    private final static HashMap<String, SubCommand> aliases = new HashMap<>();

    public static void registerCommands() {}

    private static void registerCommand(Command command) {
        commands.put(command.getLabel(), command);
        for(SubCommand subCmd : command.getSubCommands().values()) {
            if(subCmd instanceof Alias)
                registerAlias(((Alias) subCmd).getSlashAlias(), subCmd);
        }
    }

    private static void registerAlias(String alias, SubCommand command) {
        aliases.put(alias, command);
    }

    public static void runCommand(String label, Member sender, Message message, MessageChannel channel, String prefix) {
        if(!commands.containsKey(label)) return;

        String[] splitMessage = message.getContentRaw().split(" ");
        List<String> args = new ArrayList<>();
        for(String argument : splitMessage)
            if(!argument.startsWith(prefix))
                args.add(argument);

        commands.get(label)
                .prepareForExecution(args, sender, message, channel, false);
    }

    /**
     * Slash-command parser for our Bukkit-based command system.
     */
    public static void runCommand(SlashCommandEvent event) {
        if (!commands.containsKey(event.getName())) {
            if (!aliases.containsKey(event.getName())) {
                event.reply("Unable to find registered command.").setEphemeral(true).queue();
            } else aliases.get(event.getName()).prepareForExecution(event);
            return;
        }
        commands.get(event.getName()).prepareForExecution(event);
    }

    public static HashMap<String, Command> getCommands() {
        return commands;
    }

    public static Object getCommand(String label) {
        return commands.getOrDefault(label, null);
    }

    public static Object getCommand(String label, boolean getAsAlias) {
        if (getAsAlias) {
            Object command = commands.getOrDefault(label, null);
            if (command != null) return command;
            command = aliases.getOrDefault(label, null);
            return command;
        } else {
            return commands.getOrDefault(label, null);
        }
    }
}
