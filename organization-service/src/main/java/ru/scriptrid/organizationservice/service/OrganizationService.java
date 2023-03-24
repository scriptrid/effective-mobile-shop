package ru.scriptrid.organizationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.scriptrid.common.dto.OrganizationDto;
import ru.scriptrid.common.exception.FrozenOrganizationException;
import ru.scriptrid.common.exception.InvalidOwnerException;
import ru.scriptrid.common.exception.OrganizationAlreadyExistsException;
import ru.scriptrid.common.exception.OrganizationNotFoundException;
import ru.scriptrid.common.security.JwtAuthenticationToken;
import ru.scriptrid.organizationservice.exceptions.NoSuchOrganizationsException;
import ru.scriptrid.organizationservice.model.dto.EditOrganizationDto;
import ru.scriptrid.organizationservice.model.dto.RequestOrganizationDto;
import ru.scriptrid.organizationservice.model.entity.OrganizationEntity;
import ru.scriptrid.organizationservice.repository.OrganizationRepository;

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
            log.info("The organization \"{}\" already exists",request.organizationName());
            throw new OrganizationAlreadyExistsException();
        }

        organizationRepository.save(toOrganizationEntity(request));
        log.info("The organization \"{}\" was successfully created", request.organizationName());
        return toOrganizationDto(organizationRepository.findByName(request.organizationName()));
    }

    @Transactional
    public void deleteOrganization(JwtAuthenticationToken token, long id) {
        if (!organizationRepository.existsById(id)) {
            log.info("The organization with id \"{}\" was not found", id);
            throw new OrganizationNotFoundException();
        }
        if (token.isAdmin()) {
            log.info("The organization with id \"{}\" was deleted by admin {}", id, token.getUsername());
            organizationRepository.deleteById(id);
            return;
        }
        if (!isValidOwner(token.getUsername(), id)) {
            log.info("User \"{}\" is not an owner of organization with id \"{}\"", token.getUsername(), id);
            throw new InvalidOwnerException();
        }
        log.info("The organization with id \"{}\" was deleted by owner {}", id, token.getUsername());
        organizationRepository.deleteById(id);
    }

    @Transactional
    public OrganizationDto editOrganization(JwtAuthenticationToken token, long id, EditOrganizationDto dto, MultipartFile logo) {
        if (!organizationRepository.existsById(id)) {
            log.info("The organization to be edited with id \"{}\" was not found", id);
            throw new OrganizationNotFoundException();
        }
        if (token.isAdmin()) {
            OrganizationEntity entity = getOrganization(id);
            modifyEntity(entity, entity.getOwner(), dto, logo);
            log.info("The organization with id \"{}\" was edited by admin \"{}\"",id, token.getUsername());
            return toOrganizationDto(entity);
        }
        if (organizationIsFrozen(id)) {
            log.info("Organization with id \"{}\" is frozen", id);
            throw new FrozenOrganizationException();
        }
        if (!isValidOwner(token.getUsername(), id)) {
            log.info("User \"{}\" is not an owner of \"{}\" organization", token.getUsername(), getOrganization(id).getName());
            throw new InvalidOwnerException();
        }
        if (organizationRepository.existsByName(dto.name())) {
            log.info("The organization with new name \"{}\" already exists", id);
            throw new OrganizationAlreadyExistsException();
        }
        OrganizationEntity entity = getOrganization(id);
        modifyEntity(entity, token.getUsername(), dto, logo);
        return toOrganizationDto(entity);
    }

    @Transactional
    public OrganizationDto setFreezeForOrganization(long id, boolean isFrozen) {
        if (!organizationRepository.existsById(id)) {
            log.info("The organization with id \"{}\" was not found", id);
            throw new OrganizationNotFoundException();
        }
        OrganizationEntity entity = getOrganization(id);
        entity.setIsFrozen(isFrozen);
        return toOrganizationDto(entity);
    }

    @Transactional
    public OrganizationDto getOrganizationDto(long id) {
        if (!organizationRepository.existsById(id)) {
            log.info("The organization with id \"{}\" was not found", id);
            throw new OrganizationNotFoundException();
        }
        return toOrganizationDto(getOrganization(id));
    }

    private void modifyEntity(OrganizationEntity entity, String owner, EditOrganizationDto dto, MultipartFile logo) {
        entity.setName(dto.name());
        entity.setOwner(owner);
        if (dto.description() != null) {
            entity.setDescription(dto.description());
        } else {
            entity.setDescription("Empty description");
        }
        //TODO Add logo to entity.

    }

    private OrganizationDto toOrganizationDto(OrganizationEntity entity) {
        return new OrganizationDto(
                entity.getId(),
                entity.getName(),
                entity.getIsFrozen(),
                entity.getOwner(),
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
        entity.setOwner(request.organizationOwner());
        return entity;
    }

    public List<OrganizationDto> getOrganizationsDto() {
        List<OrganizationEntity> organizations = organizationRepository.findAll();
        if (organizations.isEmpty()) {
            throw new NoSuchOrganizationsException();
        }

        return organizations.stream()
                .map(this::toOrganizationDto)
                .sorted(Comparator.comparing(OrganizationDto::name))
                .toList();
    }

    public OrganizationEntity getOrganization(long id) {
        return organizationRepository.findById(id)
                .orElseThrow(OrganizationNotFoundException::new);
    }

    public boolean isValidOwner(String username, long organizationId) {
        return username.equals(getOrganization(organizationId).getOwner());
    }


    public boolean organizationIsFrozen(long id) {
        return getOrganization(id).getIsFrozen();
    }

    public boolean organizationExistsByName(String name) {
        return organizationRepository.existsByName(name);
    }
}
