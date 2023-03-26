package ru.scriptrid.reviewservice.service;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ru.scriptrid.common.dto.OrderDto;
import ru.scriptrid.common.security.JwtAuthenticationToken;

@Service
@Slf4j
public class WebOrderService {

    private final WebClient webClient;

    public WebOrderService(@Value("${services.order.uri}") String organizationServiceUri, WebClient.Builder builder) {
        this.webClient = builder.baseUrl(organizationServiceUri).build();
    }

    @Nullable
    public OrderDto getDto(long id) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return webClient.get()
                .uri("/api/order/" + id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getJwt())
                .retrieve()
                .bodyToMono(OrderDto.class)
                .onErrorResume(WebClientResponseException.class,
                        e -> e.getStatusCode().equals(HttpStatus.NOT_FOUND) ? Mono.empty() : Mono.error(e))
                .block();

    }
}
