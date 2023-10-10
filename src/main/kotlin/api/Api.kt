package api

import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * Data class representing a map of API configurations.
 *
 * @property api Map of API names to their configurations.
 */
data class ApiMap(val api: Map<String, ApiConfig>)

/**
 * Data class representing the configuration of an API.
 *
 * @property key API key.
 * @property clazz Fully qualified class name of the API implementation.
 * @property enabled Flag indicating whether the API is enabled.
 * @property api Instantiated API object (used internally, ignored during serialization).
 */
data class ApiConfig(
    val key: String? = null,
    val clazz: String,
    val enabled: Boolean? = true,
    @get:JsonIgnore val api: Api? = null
) {
    /**
     * Instantiates the corresponding API implementation based on the class name.
     *
     * @return Instantiated API object.
     */
    fun instantiateApi(): Api =
        Class.forName(clazz).getDeclaredConstructor().newInstance() as Api
}

/**
 * Interface representing an API.
 */
interface Api {
    /**
     * Initialization function for the API.
     */
    fun init()

    /**
     * Message handler for the API.
     *
     * @param msg The input message to be processed by the API.
     */
    fun message(msg: String)

    /**
     * Response handler for the API.
     *
     * @param msg The response message from the API.
     */
    fun response(msg: String)
}
