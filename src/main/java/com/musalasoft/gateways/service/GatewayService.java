package com.musalasoft.gateways.service;

import com.musalasoft.gateways.dtos.GatewayDTO;
import com.musalasoft.gateways.entities.GatewayEntity;
import com.musalasoft.gateways.repository.GatewayRepository;
import com.musalasoft.gateways.util.GatewayResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GatewayService {

    @Autowired
    private GatewayRepository gatewayRepository;

    public ResponseEntity<GatewayResponse<List<GatewayDTO>>> getAllGateways() {
        List<GatewayEntity> gatewayEntities = gatewayRepository.findAll();

        // Convert the entities to DTOs
        List<GatewayDTO> gateways = convertToListDto(gatewayEntities);
        GatewayResponse<List<GatewayDTO>> response = new GatewayResponse<>(200,
            "Gateways Retrieved successfully", gateways);
        return ResponseEntity.ok(response);

    }

    public ResponseEntity<GatewayResponse<?>> getGatewayBySerialNumber(
        String serialNumber) {
        Optional<GatewayEntity> gateway = gatewayRepository.findBySerialNumber(serialNumber);

        if (gateway.isPresent()) {
            GatewayResponse<GatewayDTO> response = new GatewayResponse<>(200,
                "Gateway retrieved successfully", convertToDto(gateway.get()));
            return ResponseEntity.ok(response);
        } else {
            GatewayResponse<String> response = new GatewayResponse<>(404,
                "Gateway not found with serial number: " + serialNumber);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    private List<GatewayDTO> convertToListDto(List<GatewayEntity> gatewayEntities) {
        List<GatewayDTO> gatewayDTOs = new ArrayList<>();
        for (GatewayEntity gatewayEntity : gatewayEntities) {
            gatewayDTOs.add(convertToDto(gatewayEntity));
        }
        return gatewayDTOs;
    }

    private GatewayDTO convertToDto(GatewayEntity gatewayEntity) {
        return new GatewayDTO(
            gatewayEntity.getSerialNumber(),
            gatewayEntity.getName(),
            gatewayEntity.getIpv4Address()
        );
    }
}
