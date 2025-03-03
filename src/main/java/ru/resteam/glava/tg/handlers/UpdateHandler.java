package ru.resteam.glava.tg.handlers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.resteam.glava.app.config.MessageEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Artem
 */
@Component
public class UpdateHandler {
	@Value("${telegram.bot.bot_name}")
	private String botName;
	private final ConcurrentMap<Long, Boolean> awaitingDiscordMessage = new ConcurrentHashMap<>();
	private final ApplicationEventPublisher eventPublisher;

	public UpdateHandler(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}
	public SendMessage handleTGUpdate(Update update) {
		Message message = update.getMessage();
		var chatId = message.getChatId();
		var messageText = message.getText();

		// В мапу помечаем, что ждет сообщение от юзера
		if (messageText.startsWith("/discord") || messageText.startsWith("/discord@" + botName)) {
			awaitingDiscordMessage.put(chatId, true);
			return new SendMessage(chatId.toString(), "Введите свое сообщение для отправки в Discord:");
		}

		// Если юзер в режиме ожидания ввода сообщения
		if (awaitingDiscordMessage.getOrDefault(chatId, false)) {
			awaitingDiscordMessage.remove(chatId); // Выключаем режим ожидания
			String payload = messageText;
			eventPublisher.publishEvent(new MessageEvent(this, payload, MessageEvent.MessageTarget.DISCORD));
			return new SendMessage(chatId.toString(), "✅ Ваше сообщение отправлено в Discord!");
		}

		// Запускаем событие в отдельном потоке
		CompletableFuture.runAsync(() -> {
			eventPublisher.publishEvent(new MessageEvent(this, messageText, MessageEvent.MessageTarget.OPENAI));
		});
		return new SendMessage(chatId.toString(), "Не знаю такую команду. Спросил у GPT.");
	}
}
