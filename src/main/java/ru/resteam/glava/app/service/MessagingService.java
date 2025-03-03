package ru.resteam.glava.app.service;

public interface MessagingService {
	void forwardToTelegram(String message);
	void forwardToDiscord(String message);
	void forwardToOpenAi();
}
