package com.example.daycandle;

import com.example.daycandle.model.DayCandle;
import com.example.daycandle.model.DayCandleDto;
import com.example.daycandle.repository.DayCandleRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.util.UriEncoder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Component
@AllArgsConstructor
public class DayCandleCollector {

    private final RestTemplate restTemplate;
    private final DayCandleRepository dayCandleRepository;

    public void start(){
        List<String> list = Arrays.asList("KRW-BTC","KRW-SOL","KRW-ETH","KRW-XRP", "KRW-ADA", "KRW-DOGE", "KRW-AVAX", "KRW-DOT", "KRW-MATIC");
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        for(String tiker : list) {
            for (int i = 0; i <= 2; i++) {
                String date = now.minusDays((long) i * 200).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String url = String.format("market=%s&to=%s&count=200&convertingPriceUnit=KRW",
                        tiker, date);
                String path = String.format("%s?%s","https://api.upbit.com/v1/candles/days", UriEncoder.encode(url));
                ResponseEntity<String> response = restTemplate.exchange(URI.create(path), HttpMethod.GET, HttpEntity.EMPTY, String.class);
                List<DayCandleDto> candles = JsonUtil.listFromJson(response.getBody(), DayCandleDto.class);

                if (candles.size() != 0) {
                    for (DayCandleDto candleDto : candles) {
                        DayCandle dayCandle = new DayCandle(candleDto);
                        dayCandleRepository.save(dayCandle);
                    }
                }
            }
        }
    }
}
