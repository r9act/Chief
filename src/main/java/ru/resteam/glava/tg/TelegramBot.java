package ru.resteam.glava.tg;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.resteam.glava.tg.handlers.UpdateHandler;

import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {

	private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);

	@Autowired
	private UpdateHandler updateHandler;

	@Value("${telegram.bot.chat_id}")
	private Long CHAT_ID;

	@Autowired
	public TelegramBot(@Value("${telegram.bot.token}") String botToken) {
		super(botToken);
	}

	@PostConstruct
	public void init() {
		try {
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			botsApi.registerBot(this);

			List<BotCommand> botCommands = List.of(
					new BotCommand("/discord", "Отправить сообщение в Discord")
			);
			execute(new SetMyCommands(botCommands, new BotCommandScopeDefault(), null));
			logger.info("Команды: {}", botCommands);
			logger.info("Telegram Bot инициализирован!");
		} catch (TelegramApiException e) {
			logger.error("Ошибка при регистрации Telegram API", e);
			throw new RuntimeException("Ошибка при регистрации Telegram API", e);
		}
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage() && update.getMessage().hasText()) {
			String messageText = update.getMessage().getText();
			Long chatId = update.getMessage().getChatId();
			logger.info("Получено сообщение из Telegram ({}): {}", chatId, messageText);
			try {
				execute(updateHandler.handleTGUpdate(update));
			} catch (TelegramApiException e) {
				logger.error("Ошибка обработки сообщения: {}", messageText, e);
			}
		}
	}

	public void sendMessageToTelegram(String text) {
		SendMessage message = new SendMessage();
		message.enableMarkdown(true);
		message.setChatId(CHAT_ID);
		message.setText(text);
		try {
			execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	@Override public String getBotUsername() {
		return "Chief";
	}
}
