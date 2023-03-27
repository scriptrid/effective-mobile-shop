package ru.scriptrid.ordersecrice.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.scriptrid.common.security.JwtAuthenticationToken;
import ru.scriptrid.ordersecrice.model.dto.RequestOrganizationCreateDto;
import ru.scriptrid.ordersecrice.model.dto.RequestOrganizationDto;
import ru.scriptrid.ordersecrice.service.RequestOrganizationService;

import java.util.List;

@RestController
@RequestMapping("/api/organization/request/")
public class OrganizationRequestController {

    private final RequestOrganizationService requestOrganizationService;

    public OrganizationRequestController(RequestOrganizationService requestOrganizationService) {
        this.requestOrganizationService = requestOrganizationService;
    }

    @PostMapping
    public ResponseEntity<RequestOrganizationDto> requestOrganizationCreate(@AuthenticationPrincipal JwtAuthenticationToken token,
                                                                            @RequestBody @Valid RequestOrganizationCreateDto dto) {
        RequestOrganizationDto requestOrganizationDto = requestOrganizationService.addRequest(token, dto);
        return ResponseEntity.ok(requestOrganizationDto);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public List<RequestOrganizationDto> getRequests() {
        return requestOrganizationService.getRequests();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public RequestOrganizationDto getRequest(@PathVariable long id) {
        return requestOrganizationService.getRequest(id);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> rejectRequest(@PathVariable long id) {
        requestOrganizationService.rejectRequest(id);
        return ResponseEntity.noContent().build();
    }
}
