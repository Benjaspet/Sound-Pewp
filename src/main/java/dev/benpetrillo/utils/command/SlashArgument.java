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

import net.dv8tion.jda.api.interactions.commands.OptionType;

public final class SlashArgument {

    public SlashArgument() {
        /*
         * This means the command must manually specify the variables.
         */
    }

    public SlashArgument(String label, String description, String reference, OptionType type) {
        this.label = label;
        this.description = description;
        this.reference = reference;
        this.argumentType = type;
    }

    public String label = null;
    public String description = null;
    public String reference = null;
    public OptionType argumentType = null;

}