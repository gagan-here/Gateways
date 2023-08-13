package com.musalasoft.gateways.service;

import com.musalasoft.gateways.dtos.GatewayDTO;
import com.musalasoft.gateways.dtos.PeripheralDeviceDTO;
import com.musalasoft.gateways.entities.GatewayEntity;
import com.musalasoft.gateways.entities.PeripheralDeviceEntity;
import com.musalasoft.gateways.repository.GatewayRepository;
import com.musalasoft.gateways.repository.PeripheralDeviceRepository;
import com.musalasoft.gateways.util.GatewayResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GatewayService {

    @Autowired
    private GatewayRepository gatewayRepository;

    @Autowired
    private PeripheralDeviceRepository deviceRepository;

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

    public ResponseEntity<GatewayResponse<String>> addDeviceToGateway(String serialNumber,
        List<PeripheralDeviceDTO> peripheralDevice) {
        Optional<GatewayEntity> gatewayEntity = gatewayRepository.findBySerialNumber(serialNumber);

        if (gatewayEntity.isPresent()) {
            GatewayEntity gateway = gatewayEntity.get();
            List<PeripheralDeviceEntity> peripheralDevices = peripheralDevice.stream()
                .map(peripheralDeviceDTO -> {
                    PeripheralDeviceEntity peripheralDeviceEntity = new PeripheralDeviceEntity();
                    peripheralDeviceEntity.setUid(peripheralDeviceDTO.getUid());
                    peripheralDeviceEntity.setVendor(peripheralDeviceDTO.getVendor());
                    peripheralDeviceEntity.setDateCreated(LocalDate.now());

                    peripheralDeviceEntity.setGateway(gateway);
                    return peripheralDeviceEntity;
                }).collect(Collectors.toList());

            gateway.setDevices(peripheralDevices);
            gatewayRepository.save(gateway);
            GatewayResponse<String> response = new GatewayResponse<>(200,
                "Device added successfully in gateway");
            return ResponseEntity.ok(response);
        } else {
            GatewayResponse<String> response = new GatewayResponse<>(404,
                "Gateway not found with serial number: " + serialNumber);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    public ResponseEntity<GatewayResponse<String>> removeDeviceFromGateway(Long deviceId) {
        Optional<PeripheralDeviceEntity> device = deviceRepository.findById(deviceId);
        if (device.isPresent()) {
            PeripheralDeviceEntity peripheralDevice = device.get();
            GatewayEntity gateway = peripheralDevice.getGateway();
            gateway.getDevices().remove(peripheralDevice);
            deviceRepository.delete(peripheralDevice);
            GatewayResponse<String> response = new GatewayResponse<>(200,
                "Device removed successfully from a gateway");
            return ResponseEntity.ok(response);
        } else {
            GatewayResponse<String> response = new GatewayResponse<>(404,
                "Device not found with id: " + deviceId);
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
