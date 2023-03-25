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
public class RequestService {
    private final RequestOrganizationRepository requestOrganizationRepository;
    private final OrganizationService organizationService;

    public RequestService(OrganizationService organizationService,
                          RequestOrganizationRepository requestOrganizationRepository) {
        this.organizationService = organizationService;
        this.requestOrganizationRepository = requestOrganizationRepository;
    }

    @Transactional
    public RequestOrganizationDto addRequest(JwtAuthenticationToken token, RequestOrganizationCreateDto dto) {
        if (organizationService.organizationExistsByName(dto.name())) {
            log.info("The organization \"{}\" already exists", dto.name());
            throw new OrganizationAlreadyExistsException(dto.name());
        }
        RequestOrganizationEntity entity = toEntity(token.getUsername(), dto);
        requestOrganizationRepository.save(entity);
        return toDto(entity);
    }

    private RequestOrganizationDto toDto(RequestOrganizationEntity entity) {
        return new RequestOrganizationDto(
                entity.getId(),
                entity.getOrganizationName(),
                entity.getOrganizationDescription(),
                entity.getOrganizationOwner()
        );
    }

    private RequestOrganizationEntity toEntity(String username, RequestOrganizationCreateDto dto) {
        RequestOrganizationEntity entity = new RequestOrganizationEntity();
        entity.setOrganizationName(dto.name());
        entity.setOrganizationDescription(dto.description());
        entity.setOrganizationOwner(username);
        return entity;
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

    public void declineRequest(long id) {
        if (!requestOrganizationRepository.existsById(id)) {
            throw new RequestOrganizationNotFoundException(id);
        }

        requestOrganizationRepository.deleteById(id);
    }
}
