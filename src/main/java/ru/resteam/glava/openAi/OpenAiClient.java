package ru.resteam.glava.openAi;

import org.springframework.stereotype.Component;

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

	private static final String OPEN_AI_TOKEN = "-";

	public void runOpenAiRequest() {

		final String apiUrl = "https://api.openai.com/v1/engines/babbage-002/completions";

		String prompt = "How old is Russian Federation";

		String requestBody = "{ \"prompt\": \"" + prompt + "\", \"max_tokens\": 150 }";

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(apiUrl))
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + OPEN_AI_TOKEN)
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.build();

		HttpResponse<String> response = null;
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}

		System.out.println("ChatGPT Response: " + response.body() + response.statusCode());
	}
}
