package ru.scriptrid.ordersecrice.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.scriptrid.common.security.JwtAuthenticationToken;
import ru.scriptrid.ordersecrice.model.dto.RequestOrganizationCreateDto;
import ru.scriptrid.ordersecrice.model.dto.RequestOrganizationDto;
import ru.scriptrid.ordersecrice.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/api/organization/request")
public class OrganizationRequestController {

    private final RequestService requestService;

    public OrganizationRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ResponseEntity<RequestOrganizationDto> requestOrganizationCreate(@AuthenticationPrincipal JwtAuthenticationToken token,
                                                                            @RequestBody @Valid RequestOrganizationCreateDto dto) {
        RequestOrganizationDto requestOrganizationDto = requestService.addRequest(token, dto);
        return ResponseEntity.ok(requestOrganizationDto);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<RequestOrganizationDto>> getRequests() {
        return ResponseEntity.ok(requestService.getRequests());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<RequestOrganizationDto> getRequests(@PathVariable long id) {
        return ResponseEntity.ok(requestService.getRequest(id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> declineRequest(@PathVariable long id) {
        requestService.declineRequest(id);
        return ResponseEntity.noContent().build();
    }
}