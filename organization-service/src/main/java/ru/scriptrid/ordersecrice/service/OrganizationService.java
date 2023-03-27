package ru.scriptrid.ordersecrice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.scriptrid.common.dto.OrganizationDto;
import ru.scriptrid.common.exception.*;
import ru.scriptrid.common.security.JwtAuthenticationToken;
import ru.scriptrid.ordersecrice.model.dto.EditOrganizationDto;
import ru.scriptrid.ordersecrice.model.dto.RequestOrganizationDto;
import ru.scriptrid.ordersecrice.model.entity.OrganizationEntity;
import ru.scriptrid.ordersecrice.repository.OrganizationRepository;
import ru.scriptrid.ordersecrice.repository.RequestOrganizationRepository;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class OrganizationService {

    private final RequestOrganizationRepository requestOrganizationRepository;
    private final OrganizationRepository organizationRepository;
    private final RequestOrganizationService requestOrganizationService;

    public OrganizationService(OrganizationRepository organizationRepository, @Lazy RequestOrganizationService requestOrganizationService,
                               RequestOrganizationRepository requestOrganizationRepository) { //TODO polish
        this.organizationRepository = organizationRepository;
        this.requestOrganizationService = requestOrganizationService;
        this.requestOrganizationRepository = requestOrganizationRepository;
    }

    @Transactional
    public OrganizationDto addOrganization(long requestId) {
        RequestOrganizationDto request = requestOrganizationService.getRequest(requestId);
        requestOrganizationRepository.deleteById(requestId);
        if (organizationRepository.existsByName(request.name())) {
            log.warn("The organization \"{}\" already exists", request.name());
            throw new OrganizationAlreadyExistsException(request.name());
        }

        organizationRepository.save(toOrganizationEntity(request));
        log.info("The organization \"{}\" was successfully created", request.name());
        return toOrganizationDto(organizationRepository.findByName(request.name()));
    }

    @Transactional
    public void deleteOrganization(JwtAuthenticationToken token, long id) {
        if (!organizationRepository.existsById(id)) {
            log.warn("The organization with id \"{}\" was not found", id);
            throw new OrganizationNotFoundByIdException(id);
        }
        OrganizationEntity organization = getOrganization(id);
        if (organization.getIsDeleted()) {
            log.warn("The organization with id \"{}\" was not found", id);
            throw new OrganizationNotFoundByIdException(id);
        }
        if (token.isAdmin()) {
            log.warn("The organization with id \"{}\" was deleted by admin {}", id, token.getUsername());
            organization.setIsDeleted(true);
            return;
        }
        if (!isValidOwner(token.getId(), id)) {
            log.warn("User \"{}\" is not an owner of organization with id \"{}\"", token.getUsername(), id);
            throw new InvalidOwnerException(id, getOrganization(id).getOwnerId(), token.getId());
        }
        log.info("The organization with id \"{}\" was deleted by owner {}", id, token.getUsername());
        organization.setIsDeleted(true);
    }

    @Transactional
    public OrganizationDto editOrganization(JwtAuthenticationToken token, long id, EditOrganizationDto dto) {
        if (!organizationRepository.existsById(id)) {
            log.warn("The organization to be edited with id \"{}\" was not found", id);
            throw new OrganizationNotFoundByIdException(id);
        }
        OrganizationEntity entity = getOrganization(id);

        if (token.isAdmin()) {
            log.warn("The organization with id \"{}\" was edited by admin \"{}\"", id, token.getUsername());
            return toOrganizationDto(modifyEntity(entity, entity.getOwnerId(), dto));
        }
        if (entity.getIsDeleted()) {
            log.warn("Organization with id \"{}\" is deleted", id);
            throw new DeletedOrganizationException(id);
        }
        if (entity.getIsFrozen()) {
            log.warn("Organization with id \"{}\" is frozen", id);
            throw new FrozenOrganizationException(id);
        }
        if (!isValidOwner(token.getId(), id)) {
            log.warn("User \"{}\" is not an owner of \"{}\" organization", token.getUsername(), entity.getName());
            throw new InvalidOwnerException(id, entity.getOwnerId(), token.getId());
        }
        if (organizationRepository.existsByName(dto.name()) && !dto.name().equals(entity.getName())) {
            log.warn("The organization with new name \"{}\" already exists", dto.name());
            throw new OrganizationAlreadyExistsException(dto.name());
        }
        log.info("The organization with id \"{}\" was edited by owner \"{}\"", id, token.getUsername());
        return toOrganizationDto(modifyEntity(entity, token.getId(), dto));
    }


    @Transactional
    public OrganizationDto setFreezeForOrganization(long id, boolean isFrozen) {
        if (!organizationRepository.existsById(id)) {
            log.info("The organization with id \"{}\" was not found", id);
            throw new OrganizationNotFoundByIdException(id);
        }
        OrganizationEntity entity = getOrganization(id);
        if (entity.getIsDeleted()) {
            log.warn("Organization with id \"{}\" is deleted", id);
            throw new DeletedOrganizationException(id);
        }
        entity.setIsFrozen(isFrozen);
        if (isFrozen) {
            log.info("Organization with id \"{}\" has been frozen", id);
        } else {
            log.info("Organization with id \"{}\" has been unfrozen", id);
        }
        return toOrganizationDto(entity);
    }

    @Transactional
    public OrganizationDto getOrganizationDto(long id) {
        if (!organizationRepository.existsById(id)) {
            log.warn("The organization with id \"{}\" was not found", id);
            throw new OrganizationNotFoundByIdException(id);
        }
        OrganizationEntity entity = getOrganization(id);
        return toOrganizationDto(entity);
    }

    private OrganizationEntity modifyEntity(OrganizationEntity entity, long ownerId, EditOrganizationDto dto) {
        entity.setName(dto.name());
        entity.setOwnerId(ownerId);
        entity.setLogoUrl(dto.logoUrl());
        entity.setDescription(dto.description());
        return entity;
    }

    private OrganizationDto toOrganizationDto(OrganizationEntity entity) {
        return new OrganizationDto(
                entity.getId(),
                entity.getName(),
                entity.getIsFrozen(),
                entity.getIsDeleted(),
                entity.getLogoUrl(),
                entity.getOwnerId(),
                entity.getDescription()
        );
    }

    private OrganizationEntity toOrganizationEntity(RequestOrganizationDto request) {
        OrganizationEntity entity = new OrganizationEntity();
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setLogoUrl(request.logoUrl());
        entity.setOwnerId(request.owner());
        return entity;
    }

    @Transactional
    public List<OrganizationDto> getOrganizationsDto() {
        List<OrganizationEntity> organizations = organizationRepository.findByIsDeletedFalse();

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

    public boolean organizationExistsByName(String name) {
        return organizationRepository.existsByName(name);
    }
}
