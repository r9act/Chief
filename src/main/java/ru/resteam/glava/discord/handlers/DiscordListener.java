package ru.resteam.glava.discord.handlers;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.resteam.glava.app.service.MessagingService;
import ru.resteam.glava.app.service.impl.MessagingServiceImpl;

import java.util.concurrent.TimeUnit;

/**
 * @author Artem
 */
@Component
public class DiscordListener extends ListenerAdapter {

	@Autowired
	@Lazy
	private MessagingService messagingService;

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
	{
		// Only accept commands from guilds
		if (event.getGuild() == null)
			return;
		switch (event.getName())
		{
		case "ban":
			Member member = event.getOption("user").getAsMember(); // the "user" option is required, so it doesn't need a null-check here
			User user = event.getOption("user").getAsUser();
			ban(event, user, member);
			break;
		case "say":
			say(event, event.getOption("content").getAsString()); // content is required so no null-check here
			break;
		case "leave":
			leave(event);
			break;
		case "prune": // 2 stage command with a button prompt
			prune(event);
			break;
		case "meet":
			meet(event);
		default:
			event.reply("I can't handle that command right now :(").setEphemeral(true).queue();
		}
	}

	@Override
	public void onButtonInteraction(ButtonInteractionEvent event)
	{
		String[] id = event.getComponentId().split(":"); // this is the custom id we specified in our button
		String authorId = id[0];
		String type = id[1];
		// Check that the button is for the user that clicked it, otherwise just ignore the event (let interaction fail)
		if (!authorId.equals(event.getUser().getId()))
			return;
		event.deferEdit().queue(); // acknowledge the button was clicked, otherwise the interaction will fail

		MessageChannel channel = event.getChannel();
		switch (type)
		{
		case "prune":
			int amount = Integer.parseInt(id[2]);
			event.getChannel().getIterableHistory()
					.skipTo(event.getMessageIdLong())
					.takeAsync(amount)
					.thenAccept(channel::purgeMessages);
			// fallthrough delete the prompt message with our buttons
		case "delete":
			event.getHook().deleteOriginal().queue();
		}
	}

	public void ban(SlashCommandInteractionEvent event, User user, Member member)
	{
		event.deferReply(true).queue(); // Let the user know we received the command before doing anything else
		InteractionHook hook = event.getHook(); // This is a special webhook that allows you to send messages without having permissions in the channel and also allows ephemeral messages
		hook.setEphemeral(true); // All messages here will now be ephemeral implicitly
		if (!event.getMember().hasPermission(Permission.BAN_MEMBERS))
		{
			hook.sendMessage("You do not have the required permissions to ban users from this server.").queue();
			return;
		}

		Member selfMember = event.getGuild().getSelfMember();
		if (!selfMember.hasPermission(Permission.BAN_MEMBERS))
		{
			hook.sendMessage("I don't have the required permissions to ban users from this server.").queue();
			return;
		}

		if (member != null && !selfMember.canInteract(member))
		{
			hook.sendMessage("This user is too powerful for me to ban.").queue();
			return;
		}

		// optional command argument, fall back to 0 if not provided
		int delDays = event.getOption("del_days", 0, OptionMapping::getAsInt); // this last part is a method reference used to "resolve" the option value

		// optional ban reason with a lazy evaluated fallback (supplier)
		String reason = event.getOption("reason",
				() -> "Banned by " + event.getUser().getName(), // used if getOption("reason") is null (not provided)
				OptionMapping::getAsString); // used if getOption("reason") is not null (provided)

		// Ban the user and send a success response
		event.getGuild().ban(user, delDays, TimeUnit.DAYS)
				.reason(reason) // audit-log ban reason (sets X-AuditLog-Reason header)
				.flatMap(v -> hook.sendMessage("Banned user " + user.getName())) // chain a followup message after the ban is executed
				.queue(); // execute the entire call chain
	}

	public void say(SlashCommandInteractionEvent event, String content)
	{
		event.reply(content).queue(); // This requires no permissions!
	}

	public void leave(SlashCommandInteractionEvent event)
	{
		if (!event.getMember().hasPermission(Permission.KICK_MEMBERS))
			event.reply("You do not have permissions to kick me.").setEphemeral(true).queue();
		else
			event.reply("Leaving the server... :wave:") // Yep we received it
					.flatMap(v -> event.getGuild().leave()) // Leave server after acknowledging the command
					.queue();
	}

	public void prune(SlashCommandInteractionEvent event)
	{
		OptionMapping amountOption = event.getOption("amount"); // This is configured to be optional so check for null
		int amount = amountOption == null
				? 100 // default 100
				: (int) Math.min(200, Math.max(2, amountOption.getAsLong())); // enforcement: must be between 2-200
		String userId = event.getUser().getId();
		event.reply("This will delete " + amount + " messages.\nAre you sure?") // prompt the user with a button menu
				.addActionRow(// this means "<style>(<id>, <label>)", you can encode anything you want in the id (up to 100 characters)
						Button.secondary(userId + ":delete", "Nevermind!"),
						Button.danger(userId + ":prune:" + amount, "Yes!")) // the first parameter is the component id we use in onButtonInteraction above
				.queue();
	}

	private void meet(SlashCommandInteractionEvent event) {
		OptionMapping amountOption = event.getOption("minutes");
		int amount = amountOption == null
				? 5 : (int) amountOption.getAsLong();
		String msg = "Встреча начнется через " + amount + " минут!";
		//		String msg = "!!!";
		event.getChannel().sendMessage(msg).queue();
		messagingService.forwardToTelegram(msg);

	}
}

