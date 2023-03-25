package ru.scriptrid.orderservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ru.scriptrid.common.dto.ProductDto;
import ru.scriptrid.common.security.JwtAuthenticationToken;

@Service
@Slf4j
public class WebProductService {

    private final WebClient webClient;

    public WebProductService(@Value("${services.product.uri}") String productServiceUri, WebClient.Builder builder) {
        this.webClient = builder.baseUrl(productServiceUri).build();
    }

    public ProductDto getDto(long id) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return webClient.get()
                .uri("/api/product/" + id)
                .header("Authentication", "Bearer " + token.getJwt())
                .retrieve()
                .bodyToMono(ProductDto.class)
                .onErrorResume(WebClientResponseException.class,
                        e -> e.getStatusCode().equals(HttpStatus.NOT_FOUND) ? Mono.empty() : Mono.error(e))
                .block();
    }

    public void reserveProduct(long id) {
    }
}
