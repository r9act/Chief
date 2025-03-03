package ru.resteam.glava.app.config;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;

import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.resteam.glava.discord.handlers.DiscordListener;

import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;
import static ru.resteam.glava.discord.handlers.SlashCommandConstants.*;

@Configuration
public class BeanConfiguration {

    @Value("${discord.bot.token}")
    private String discordToken;

    @Bean
    public JDA jda(DiscordListener discordListener) {
        final JDABuilder builder = JDABuilder.createDefault(discordToken);
        builder.addEventListeners(discordListener);
        builder.setActivity(Activity.watching("Pron"));
        builder.enableIntents(
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.MESSAGE_CONTENT);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS,
                CacheFlag.MEMBER_OVERRIDES);
        builder.setChunkingFilter(ChunkingFilter.NONE);
        builder.disableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_TYPING);
        builder.setLargeThreshold(50);
        try {
            final JDA jda = builder.build();
            jda.awaitReady();

            CommandListUpdateAction commands = jda.updateCommands();
            // Moderation commands with required options
            //            commands.addCommands(
            //                    Commands.slash(BAN, "Ban a user from this server. Requires permission to ban users.")
            //                            .addOptions(new OptionData(USER, "user",
            //                                    "The user to ban") // USER type allows to include members of the server or other users by id
            //                                    .setRequired(true)) // This command requires a parameter
            //                            .addOptions(new OptionData(INTEGER, "del_days",
            //                                    "Delete messages from the past days.") // This is optional
            //                                    .setRequiredRange(0, 7)) // Only allow values between 0 and 7 (inclusive)
            //                            .addOptions(new OptionData(STRING, "reason",
            //                                    "The ban reason to use (default: Banned by <user>)")) // optional reason
            //                            .setGuildOnly(true) // This way the command can only be executed from a guild, and not the DMs
            //                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS))
            //                    // Only members with the BAN_MEMBERS permission are going to see this command
            //            );
            // Simple reply commands
            commands.addCommands(
                    Commands.slash(SAY, "Makes the bot say what you tell it to")
                            .addOption(STRING, "content", "What the bot should say", true)
                    // you can add required options like this too
            );
            // Commands without any inputs
            //            commands.addCommands(
            //                    Commands.slash(LEAVE, "Make the bot leave the server")
            //                            .setGuildOnly(true) // this doesn't make sense in DMs
            //                            .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
            //                    // only admins should be able to use this command.
            //            );
            commands.addCommands(
                    Commands.slash(PRUNE, "Prune messages from this channel")
                            .addOption(INTEGER, "amount",
                                    "How many messages to prune (Default 100)") // simple optional argument
                            .setGuildOnly(true)
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE))
            );
            commands.addCommands(
                    Commands.slash(MEET, "Meeting notification")
                            .addOption(INTEGER, "minutes",
                                    "How many minutes left") // simple optional argument
                            .setGuildOnly(true)
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE))
            );

            // Send the new set of commands to discord, this will override any existing global commands with the new set provided here
            commands.queue();
            return jda;
        } catch (final InterruptedException e) {
            System.out.println("TG error");
            ;
        }
        return null;
    }
}
