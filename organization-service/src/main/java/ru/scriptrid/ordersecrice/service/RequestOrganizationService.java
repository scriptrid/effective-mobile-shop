package ru.scriptrid.ordersecrice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.scriptrid.common.exception.OrganizationAlreadyExistsException;
import ru.scriptrid.common.security.JwtAuthenticationToken;
import ru.scriptrid.ordersecrice.exceptions.RequestOrganizationNotFoundException;
import ru.scriptrid.ordersecrice.model.dto.RequestOrganizationCreateDto;
import ru.scriptrid.ordersecrice.model.dto.RequestOrganizationDto;
import ru.scriptrid.ordersecrice.model.entity.RequestOrganizationEntity;
import ru.scriptrid.ordersecrice.repository.RequestOrganizationRepository;

import java.util.List;

@Service
@Slf4j
public class RequestOrganizationService {
    private final RequestOrganizationRepository requestOrganizationRepository;
    private final OrganizationService organizationService;

    public RequestOrganizationService(OrganizationService organizationService,
                                      RequestOrganizationRepository requestOrganizationRepository) {
        this.organizationService = organizationService;
        this.requestOrganizationRepository = requestOrganizationRepository;
    }

    @Transactional
    public RequestOrganizationDto addRequest(JwtAuthenticationToken token, RequestOrganizationCreateDto dto) {
        if (organizationService.organizationExistsByName(dto.name())) {
            log.warn("The organization \"{}\" already exists", dto.name());
            throw new OrganizationAlreadyExistsException(dto.name());
        }
        RequestOrganizationEntity entity = requestOrganizationRepository.save(toEntity(token.getId(), dto));
        log.info("The organization creation request with id \"{}\" was successfully added", entity.getId());
        return toDto(entity);
    }

    public List<RequestOrganizationDto> getRequests() {
        List<RequestOrganizationEntity> requests = requestOrganizationRepository.findAll();

        return requests.stream()
                .map(this::toDto)
                .toList();
    }

    public RequestOrganizationDto getRequest(long id) {
        RequestOrganizationEntity request = requestOrganizationRepository.findById(id)
                .orElseThrow(() -> new RequestOrganizationNotFoundException(id));

        return toDto(request);
    }

    public void rejectRequest(long id) {
        if (!requestOrganizationRepository.existsById(id)) {
            throw new RequestOrganizationNotFoundException(id);
        }
        log.info("The organization creation request with id \"{}\" has been rejected", id);
        requestOrganizationRepository.deleteById(id);
    }

    private RequestOrganizationEntity toEntity(long usernameId, RequestOrganizationCreateDto dto) {
        RequestOrganizationEntity entity = new RequestOrganizationEntity();
        entity.setName(dto.name());
        entity.setDescription(dto.description());
        entity.setLogoUrl(dto.logoUrl());
        entity.setOwnerId(usernameId);
        return entity;
    }

    private RequestOrganizationDto toDto(RequestOrganizationEntity entity) {
        return new RequestOrganizationDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getLogoUrl(),
                entity.getOwnerId()
        );
    }
}
