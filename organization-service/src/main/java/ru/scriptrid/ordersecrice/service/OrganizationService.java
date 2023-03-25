package ru.scriptrid.ordersecrice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.scriptrid.common.dto.OrganizationDto;
import ru.scriptrid.common.exception.FrozenOrganizationException;
import ru.scriptrid.common.exception.InvalidOwnerException;
import ru.scriptrid.common.exception.OrganizationAlreadyExistsException;
import ru.scriptrid.common.exception.OrganizationNotFoundByIdException;
import ru.scriptrid.common.security.JwtAuthenticationToken;
import ru.scriptrid.ordersecrice.model.dto.EditOrganizationDto;
import ru.scriptrid.ordersecrice.model.dto.RequestOrganizationDto;
import ru.scriptrid.ordersecrice.model.entity.OrganizationEntity;
import ru.scriptrid.ordersecrice.repository.OrganizationRepository;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class OrganizationService {
    private final OrganizationRepository organizationRepository;

    private final RequestService requestService;

    public OrganizationService(OrganizationRepository organizationRepository, @Lazy RequestService requestService) { //TODO
        this.organizationRepository = organizationRepository;
        this.requestService = requestService;
    }

    @Transactional
    public OrganizationDto addOrganization(long requestId) {
        RequestOrganizationDto request = requestService.getRequest(requestId);

        if (organizationRepository.existsByName(request.organizationName())) {
            log.info("The organization \"{}\" already exists", request.organizationName());
            throw new OrganizationAlreadyExistsException(request.organizationName());
        }

        organizationRepository.save(toOrganizationEntity(request));
        log.info("The organization \"{}\" was successfully created", request.organizationName());
        return toOrganizationDto(organizationRepository.findByName(request.organizationName()));
    }

    @Transactional
    public void deleteOrganization(JwtAuthenticationToken token, long id) {
        if (!organizationRepository.existsById(id)) {
            log.info("The organization with id \"{}\" was not found", id);
            throw new OrganizationNotFoundByIdException(id);
        }
        OrganizationEntity organization = getOrganization(id);
        if (token.isAdmin()) {
            log.info("The organization with id \"{}\" was deleted by admin {}", id, token.getUsername());
            organization.setIsDeleted(true);
            return;
        }
        if (!isValidOwner(token.getId(), id)) {
            log.info("User \"{}\" is not an owner of organization with id \"{}\"", token.getUsername(), id);
            throw new InvalidOwnerException(id, getOrganization(id).getOwnerId(), token.getId() );
        }
        log.info("The organization with id \"{}\" was deleted by owner {}", id, token.getUsername());
        organization.setIsDeleted(true);
    }

    @Transactional
    public OrganizationDto editOrganization(JwtAuthenticationToken token, long id, EditOrganizationDto dto) {
        if (!organizationRepository.existsById(id)) {
            log.info("The organization to be edited with id \"{}\" was not found", id);
            throw new OrganizationNotFoundByIdException(id);
        }
        OrganizationEntity entity = getOrganization(id);

        if (token.isAdmin()) {
            log.info("The organization with id \"{}\" was edited by admin \"{}\"", id, token.getUsername());
            return toOrganizationDto(modifyEntity(entity, token.getId(), dto));
        }
        if (organizationIsFrozen(id)) {
            log.info("Organization with id \"{}\" is frozen", id);
            throw new FrozenOrganizationException(id);
        }
        if (!isValidOwner(token.getId(), id)) {
            log.info("User \"{}\" is not an owner of \"{}\" organization", token.getUsername(), entity.getName());
            throw new InvalidOwnerException(id, entity.getOwnerId(), token.getId());
        }
        if (organizationRepository.existsByName(dto.name())) {
            log.info("The organization with new name \"{}\" already exists", dto.name());
            throw new OrganizationAlreadyExistsException(dto.name());
        }
        return toOrganizationDto(modifyEntity(entity, token.getId(), dto));
    }


    @Transactional
    public OrganizationDto setFreezeForOrganization(long id, boolean isFrozen) {
        if (!organizationRepository.existsById(id)) {
            log.info("The organization with id \"{}\" was not found", id);
            throw new OrganizationNotFoundByIdException(id);
        }
        OrganizationEntity entity = getOrganization(id);
        entity.setIsFrozen(isFrozen);
        return toOrganizationDto(entity);
    }

    @Transactional
    public OrganizationDto getOrganizationDto(long id) {
        if (!organizationRepository.existsById(id)) {
            log.info("The organization with id \"{}\" was not found", id);
            throw new OrganizationNotFoundByIdException(id);
        }
        return toOrganizationDto(getOrganization(id));
    }

    private OrganizationEntity modifyEntity(OrganizationEntity entity, long ownerId, EditOrganizationDto dto) {
        entity.setName(dto.name());
        entity.setOwnerId(ownerId);
        if (dto.description() != null) {
            entity.setDescription(dto.description());
        } else {
            entity.setDescription("Empty description");
        }
        return entity;
    }

    private OrganizationDto toOrganizationDto(OrganizationEntity entity) {
        return new OrganizationDto(
                entity.getId(),
                entity.getName(),
                entity.getIsFrozen(),
                entity.getIsDeleted(),
                entity.getOwnerId(),
                entity.getDescription()
        );
    }

    private OrganizationEntity toOrganizationEntity(RequestOrganizationDto request) {
        OrganizationEntity entity = new OrganizationEntity();
        entity.setName(request.organizationName());
        if (request.organizationDescription() != null) {
            entity.setDescription(request.organizationDescription());
        } else {
            entity.setDescription("Empty description");
        }
        entity.setOwnerId(request.organizationOwner());
        return entity;
    }

    public List<OrganizationDto> getOrganizationsDto() {
        List<OrganizationEntity> organizations = organizationRepository.findAll();

        return organizations.stream()
                .map(this::toOrganizationDto)
                .sorted(Comparator.comparing(OrganizationDto::name))
                .toList();
    }

    public OrganizationEntity getOrganization(long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new OrganizationNotFoundByIdException(id));
    }

    public boolean isValidOwner(long userId, long organizationId) {
        return userId == getOrganization(organizationId).getOwnerId();
    }


    public boolean organizationIsFrozen(long id) {
        return getOrganization(id).getIsFrozen();
    }

    public boolean organizationExistsByName(String name) {
        return organizationRepository.existsByName(name);
    }
}
