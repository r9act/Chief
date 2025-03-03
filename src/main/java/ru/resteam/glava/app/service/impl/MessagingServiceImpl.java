package ru.resteam.glava.app.service.impl;

import org.springframework.context.event.EventListener;

import org.springframework.stereotype.Service;
import ru.resteam.glava.app.config.MessageEvent;
import ru.resteam.glava.app.service.MessagingService;
import ru.resteam.glava.discord.service.DiscordBotService;
import ru.resteam.glava.openAi.OpenAiClient;
import ru.resteam.glava.tg.service.TelegramBotService;

/**
 * Общий сервис
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

	@EventListener
	public void handleMessageEvent(MessageEvent event) {
		switch (event.getTarget()) {
		case TELEGRAM -> telegramBotService.sendMessage(event.getMessage());
		case DISCORD -> discordBotService.sendMessage(event.getMessage());
		case OPENAI -> openAiClient.runOpenAiRequest(event.getMessage());
		}
	}
}
