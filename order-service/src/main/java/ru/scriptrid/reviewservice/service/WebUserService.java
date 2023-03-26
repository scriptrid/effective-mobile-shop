package ru.scriptrid.reviewservice.service;

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
import ru.scriptrid.common.dto.TransactionCreateDto;
import ru.scriptrid.common.dto.TransactionDto;
import ru.scriptrid.common.dto.UserDto;
import ru.scriptrid.common.security.JwtAuthenticationToken;
import ru.scriptrid.common.security.JwtService;


@Service
@Slf4j
public class WebUserService {

    private final WebClient webClient;
    private final JwtService jwtService;

    public WebUserService(@Value("${services.user.uri}") String productServiceUri, WebClient.Builder builder, JwtService jwtService) {
        this.webClient = builder.baseUrl(productServiceUri).build();
        this.jwtService = jwtService;
    }

    public UserDto getDto(long id) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return webClient.get()
                .uri("/api/user/" + id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getJwt())
                .retrieve()
                .bodyToMono(UserDto.class)
                .onErrorResume(WebClientResponseException.class,
                        e -> e.getStatusCode().equals(HttpStatus.NOT_FOUND) ? Mono.empty() : Mono.error(e))
                .block();

    }

    public TransactionDto transferMoney(TransactionCreateDto dto) {
        String jwt = jwtService.generateServiceToken();
        return webClient.put()
                .uri("/api/transaction/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(dto))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .retrieve()
                .bodyToMono(TransactionDto.class)
                .block();
    }

    public TransactionDto returnMoney(long transactionId) {
        String jwt = jwtService.generateServiceToken();
        return webClient.put()
                .uri("/api/transaction/transfer/return")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(transactionId))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .retrieve()
                .bodyToMono(TransactionDto.class)
                .block();
    }
}