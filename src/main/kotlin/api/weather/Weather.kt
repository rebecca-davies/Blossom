package api.weather

import api.Api
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import log
import java.net.URL
import kotlin.math.roundToInt

/**
 * Represents a weather API handler.
 */
class Weather : Api {

    // Base URL components for the weather API
    private var urlPrefix = "https://api.open-meteo.com/v1/forecast?"
    private var urlCoordinates = ""
    private var urlSuffix = "&current=temperature_2m,rain,showers&forecast_days=1"

    /**
     * Initializes the Weather API.
     */
    override fun init() {
        log.info("Loaded ${this::class.java.simpleName}")
    }

    /**
     * Processes a message related to weather.
     * @param msg The incoming message.
     */
    override fun handle(msg: String) {
        val objectMapper = ObjectMapper()

        // Fetch user location
        val (latitude, longitude) = fetchUserLocation(objectMapper)
        urlCoordinates = "latitude=$latitude&longitude=$longitude"

        // Fetch weather information
        val weatherJSON = fetchWeatherData()
        val temperature = parseTemperature(weatherJSON)

        // Display temperature message
        ApiContainer.elevenlabs.handle("It's currently ${temperature.roundToInt()} degrees outside.")
    }

    /**
     * Fetches the user's location coordinates (latitude and longitude) from the IPInfo API.
     * @param objectMapper The ObjectMapper for JSON parsing.
     * @return Pair of latitude and longitude as strings.
     */
    private fun fetchUserLocation(objectMapper: ObjectMapper): Pair<String, String> {
        val locationURL = URL("https://ipinfo.io/json")
        val locationJSON = locationURL.readText()
        val jsonNode: JsonNode = objectMapper.readTree(locationJSON)
        val location = jsonNode.get("loc").asText()
        val (latitude, longitude) = location.split(",")
        return latitude to longitude
    }

    /**
     * Fetches weather data from the Open Meteo API based on the user's location.
     * @return JSON string containing weather information.
     */
    private fun fetchWeatherData(): String {
        val weatherURL = URL("$urlPrefix$urlCoordinates$urlSuffix")
        return weatherURL.readText()
    }

    /**
     * Parses the temperature from the weather JSON response.
     * @param weatherJSON JSON string containing weather information.
     * @return Current temperature at 2 meters in Celsius.
     */
    private fun parseTemperature(weatherJSON: String): Double {
        val objectMapper = ObjectMapper()
        val jsonNode = objectMapper.readTree(weatherJSON)
        return jsonNode.path("current").path("temperature_2m").asDouble()
    }
}
