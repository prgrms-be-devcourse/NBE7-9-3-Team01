package org.example.povi.domain.weather

import org.example.povi.domain.weather.dto.OpenWeatherDto
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class OpenWeatherClient(
    private val openWeatherWebClient: WebClient,
    private val props: OpenWeatherProperties
) {

    fun fetchSnapshot(lat: Double, lon: Double): Snapshot {
        check(!(props.apiKey == null || props.apiKey.isBlank())) { "OpenWeather API key가 설정되지 않았습니다." }

        val dto = openWeatherWebClient.get()
            .uri { b ->
                b.path(props.path) // /data/2.5/weather
                    .queryParam("lat", lat)
                    .queryParam("lon", lon)
                    .queryParam("appid", props.apiKey)
                    .queryParam("units", props.units) // metric
                    .build()
            }
            .retrieve()
            .onStatus(HttpStatusCode::isError) { resp ->
                resp.bodyToMono(String::class.java)
                    .flatMap { body ->
                        Mono.error(RuntimeException("OpenWeather error: ${resp.statusCode()} / $body"))
                    }
            }
            .bodyToMono(OpenWeatherDto::class.java)
            .block(Duration.ofSeconds(5))

        checkNotNull(dto) { "OpenWeather 응답이 비었습니다." }

        val main = extractWeatherMain(dto)
        val tempC = extractTemperature(dto)
        val windMs = extractWindSpeed(dto)

        return Snapshot(main, tempC, windMs)
    }

    /** 서비스에서 쓰는 최소 요약  */
    data class Snapshot(val weatherMain: String?, val temperatureC: Double, val windMs: Double)

    internal fun extractWeatherMain(dto: OpenWeatherDto): String? {
        return if (dto.weather != null && dto.weather.isNotEmpty() && dto.weather[0]?.main != null)
            dto.weather[0]?.main
        else
            "Clear"
    }

    internal fun extractTemperature(dto: OpenWeatherDto): Double {
        return dto.main?.temp ?: Double.NaN
    }

    internal fun extractWindSpeed(dto: OpenWeatherDto): Double {
        return dto.wind?.speed ?: 0.0
    }
}
