package ru.scriptrid.productservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ru.scriptrid.common.dto.OrganizationDto;
import ru.scriptrid.common.security.JwtAuthenticationToken;

@Service
@Slf4j
public class WebOrganizationService {

    private final WebClient webClient;

    public WebOrganizationService(@Value("${services.organization.uri}") String organizationServiceUri, WebClient.Builder builder) {
        this.webClient = builder.baseUrl(organizationServiceUri).build();
    }

    public OrganizationDto getDto(long id) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return webClient.get()
                .uri("/api/organization/" + id)
                .header("Authentication", "Bearer " + token.getJwt())
                .retrieve()
                .bodyToMono(OrganizationDto.class)
                .onErrorResume(WebClientResponseException.class,
                        e -> e.getStatusCode().equals(HttpStatus.NOT_FOUND) ? Mono.empty() : Mono.error(e))
                .block();

    }

}
