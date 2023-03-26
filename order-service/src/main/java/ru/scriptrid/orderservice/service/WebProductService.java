package ru.scriptrid.orderservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ru.scriptrid.common.dto.ProductDto;
import ru.scriptrid.common.security.JwtAuthenticationToken;
import ru.scriptrid.common.security.JwtService;
import ru.scriptrid.orderservice.exceptions.ReservationException;

@Service
@Slf4j
public class WebProductService {

    private final WebClient webClient;
    private final JwtService jwtService;

    public WebProductService(@Value("${services.product.uri}") String productServiceUri, WebClient.Builder builder, JwtService jwtService) {
        this.webClient = builder.baseUrl(productServiceUri).build();
        this.jwtService = jwtService;
    }

    public ProductDto getDto(long id) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return webClient.get()
                .uri("/api/product/" + id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getJwt())
                .retrieve()
                .bodyToMono(ProductDto.class)
                .onErrorResume(WebClientResponseException.class,
                        e -> e.getStatusCode().equals(HttpStatus.NOT_FOUND) ? Mono.empty() : Mono.error(e))
                .block();
    }

    public void reserveProduct(long productId, int quantity) {
        String jwt = jwtService.generateServiceToken();
        try {
            webClient.put()
                    .uri("/api/product/" + productId + "/reserve")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(quantity))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException.BadRequest e) {
            log.warn("Error during reservation", e);
            throw new ReservationException(e, productId, quantity);
        }
    }

    public void returnProduct(long productId, int quantity) {
        String jwt = jwtService.generateServiceToken();
        webClient.put()
                .uri("/api/product/" + productId + "/return")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(quantity))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
