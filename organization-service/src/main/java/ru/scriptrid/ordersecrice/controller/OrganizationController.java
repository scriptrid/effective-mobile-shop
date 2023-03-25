package ru.scriptrid.ordersecrice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.scriptrid.common.dto.OrganizationDto;
import ru.scriptrid.common.security.JwtAuthenticationToken;
import ru.scriptrid.ordersecrice.model.dto.EditOrganizationDto;
import ru.scriptrid.ordersecrice.service.OrganizationService;

import java.util.List;

@RestController
@RequestMapping("/api/organization")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    //TODO Add logo file
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/submit")
    public ResponseEntity<OrganizationDto> submitOrganization(@RequestParam long requestId) {
        OrganizationDto organizationDto = organizationService.addOrganization(requestId);
        return ResponseEntity.ok(organizationDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(@AuthenticationPrincipal JwtAuthenticationToken token,
                                                   @PathVariable long id) {
        organizationService.deleteOrganization(token, id);
        return ResponseEntity.noContent().build(); //TODO Повесить isDeleted вместо удаления из базы данных
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizationDto> editOrganization(@AuthenticationPrincipal JwtAuthenticationToken token,
                                                            @PathVariable long id,
                                                            @RequestBody EditOrganizationDto dto) {
        OrganizationDto organizationDto = organizationService.editOrganization(token, id, dto);
        return ResponseEntity.ok(organizationDto);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}/freeze")
    public ResponseEntity<OrganizationDto> setFreezeForOrganization(@PathVariable long id, @RequestParam boolean isFrozen) {
        OrganizationDto dto = organizationService.setFreezeForOrganization(id, isFrozen);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationDto> getOrganization(@PathVariable long id) {
        OrganizationDto dto = organizationService.getOrganizationDto(id);
        return ResponseEntity.ok(dto);
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<OrganizationDto>> getOrganizations() {
        return ResponseEntity.ok(organizationService.getOrganizationsDto());
    }


}
