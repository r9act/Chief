package ru.resteam.glava.openAi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ru.resteam.glava.app.config.MessageEvent;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author Artem
 */
@Component
public class OpenAiClient {

	@Value("${open_ai.token}")
	private String OPEN_AI_TOKEN;

	private final ApplicationEventPublisher eventPublisher;

	public OpenAiClient(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	public void runOpenAiRequest(String message) {

		final String apiUrl = "https://api.openai.com/v1/engines/babbage-002/completions";

		String requestBody = "{ \"prompt\": \"" + message + "\", \"max_tokens\": 150 }";

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(apiUrl))
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + OPEN_AI_TOKEN)
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.build();

		HttpResponse<String> response;
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}

		String responseText = "ChatGPT Response: " + response.body() + response.statusCode();

		eventPublisher.publishEvent(new MessageEvent(this, responseText, MessageEvent.MessageTarget.TELEGRAM));
	}
}
