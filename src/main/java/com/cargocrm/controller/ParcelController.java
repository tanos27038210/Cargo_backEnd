package com.cargocrm.controller;

import com.cargocrm.dto.ParcelCreateRequest;
import com.cargocrm.dto.ParcelResponse;
import com.cargocrm.dto.ParcelStatusChangeRequest;
import com.cargocrm.dto.ParcelUpdateRequest;
import com.cargocrm.service.ParcelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ParcelController {
    private final ParcelService parcelService;

    public ParcelController(ParcelService parcelService) {
        this.parcelService = parcelService;
    }

    @PostMapping("/orders/{orderId}/parcels")
    public ResponseEntity<ParcelResponse> createParcel(@PathVariable Long orderId,
                                                       @RequestBody @Valid ParcelCreateRequest request) {
        ParcelResponse created = parcelService.createParcel(orderId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/orders/{orderId}/parcels")
    public List<ParcelResponse> getParcels(@PathVariable Long orderId,
                                           @RequestParam(required = false) String search) {
        return parcelService.getParcels(orderId, search);
    }

    @GetMapping("/parcels/{id}")
    public ParcelResponse getParcelById(@PathVariable Long id) {
        return parcelService.getParcelById(id);
    }

    @PutMapping("/parcels/{id}")
    public ResponseEntity<ParcelResponse> updateParcel(@PathVariable Long id,
                                                       @RequestBody @Valid ParcelUpdateRequest request) {
        ParcelResponse updated = parcelService.updateParcel(id, request);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/parcels/{id}")
    public ResponseEntity<Void> deleteParcel(@PathVariable Long id) {
        parcelService.deleteParcel(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("parcels/{id}/status")
    public ResponseEntity<ParcelResponse> changeStatus(@PathVariable Long id,
                                                       @RequestBody @Valid ParcelStatusChangeRequest request) {
        ParcelResponse changedStatus = parcelService.changeStatus(id, request);

        return ResponseEntity.ok(changedStatus);
    }
}
