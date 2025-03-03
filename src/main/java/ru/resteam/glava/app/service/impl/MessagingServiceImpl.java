package ru.resteam.glava.app.service.impl;

import org.springframework.stereotype.Service;
import ru.resteam.glava.app.service.MessagingService;
import ru.resteam.glava.discord.service.DiscordBotService;
import ru.resteam.glava.openAi.OpenAiClient;
import ru.resteam.glava.tg.service.TelegramBotService;

/**
 * @author Artem
 */
@Service
public class MessagingServiceImpl implements MessagingService {

	private final TelegramBotService telegramBotService;
	private final DiscordBotService discordBotService;
	private final OpenAiClient openAiClient;

	public MessagingServiceImpl(TelegramBotService telegramBotService, DiscordBotService discordBotService,
			OpenAiClient openAiClient) {
		this.telegramBotService = telegramBotService;
		this.discordBotService = discordBotService;
		this.openAiClient = openAiClient;
	}

	public void forwardToTelegram(String message) {
		telegramBotService.sendMessage(message);
	}

	public void forwardToDiscord(String message) {
		discordBotService.sendMessage(message);
	}

	public void forwardToOpenAi() {
		openAiClient.runOpenAiRequest();
	}
}
