import api.*
import api.time.CurrentTime
import api.time.Timer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.util.logging.Logger

// Logger instance for the application
val log: Logger = Logger.getLogger("AppLogger")

/**
 * Object holding instances of various APIs used in the application.
 */
object ApiContainer {
    lateinit var openai: ChatGPT
    lateinit var elevenlabs: ElevenLabs
    lateinit var timer: Timer
    lateinit var currenttime: CurrentTime

    /**
     * Sets the property of ApiContainer with the provided name to the given API instance.
     *
     * @param name The name of the API property.
     * @param api The API instance to be set.
     */
    fun setApiProperty(name: String, api: Api) {
        this::class.members
            .firstOrNull { it.name == name }
            ?.let { (it as? kotlin.reflect.KMutableProperty1<ApiContainer, Api?>)?.set(this, api) }
    }
}

/**
 * Object holding configuration information for available APIs.
 */
object AppConfig {
    var availableApis: ApiMap = ApiMap(emptyMap())
}

/**
 * Main function starting the Blossom application.
 */
fun main() {
    log.info("Starting Blossom")

    // Loading configuration and initiating Vosk speech recognition
    loadConfig()
    log.info("Loading VOSK speech recognition model")
    Vosk().listen()
}

/**
 * Loads the configuration from the YAML file and initializes the available APIs.
 */
private fun loadConfig() {
    // Path to the YAML configuration file
    val path = AppConfig::class.java.getResource("config/api.yaml")

    // Reading the configuration into ApiMap using Jackson YAML parsing
    val apiMap: ApiMap = ObjectMapper(YAMLFactory())
        .registerModule(KotlinModule())
        .readValue(path, ApiMap::class.java)

    // Initializing APIs based on the configuration
    val initializedApis = apiMap.api.mapValues { (key, config) ->
        val apiInstance = config.instantiateApi()
        ApiContainer.setApiProperty(key, apiInstance)
        config.copy(api = apiInstance)
    }

    // Updating AppConfig with the initialized APIs
    AppConfig.availableApis = ApiMap(initializedApis)

    // Initializing each API
    initializedApis.values.forEach { it.api?.init() }
}
