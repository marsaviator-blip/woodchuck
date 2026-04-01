package org.hatchery.services;

import org.springframework.web.client.RestClient;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class WeatherService {

	public record WeatherResponse(Current current) {
		public record Current(LocalDateTime time, int interval, double temperature_2m) {}
	}

	@McpTool(description = "Get the temperature (in celsius) for a specific location")
	public WeatherResponse getTemperature(
      @McpToolParam(description = "The location latitude") double latitude,
      @McpToolParam(description = "The location longitude") double longitude) {

		return RestClient.create()
				.get()
				.uri("https://api.open-meteo.com/v1/forecast?latitude={latitude}&longitude={longitude}&current=temperature_2m",
						latitude, longitude)
				.retrieve()
				.body(WeatherResponse.class);
	}

}

