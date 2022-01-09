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

package dev.benpetrillo.listeners;

import dev.benpetrillo.Config;
import dev.benpetrillo.utils.command.CommandManager;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public final class CommandListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getChannelType().isGuild()) return;
        if (event.getAuthor().isBot()) return;

        String message = event.getMessage().getContentRaw();
        String prefix = Config.get("PREFIX");
        if (!message.startsWith(prefix)) return;
        if (message.split(prefix).length < 2) return;

        CommandManager.runCommand(
                message.split(prefix)[1].split(" ")[0],
                event.getMember(), event.getMessage(),
                event.getChannel(), prefix
        );
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        CommandManager.runCommand(event);
    }
}
