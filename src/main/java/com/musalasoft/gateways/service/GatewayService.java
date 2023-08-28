package com.musalasoft.gateways.service;

import com.musalasoft.gateways.dtos.GatewayDTO;
import com.musalasoft.gateways.dtos.PeripheralDeviceDTO;
import com.musalasoft.gateways.entities.GatewayEntity;
import com.musalasoft.gateways.entities.PeripheralDeviceEntity;
import com.musalasoft.gateways.repository.GatewayRepository;
import com.musalasoft.gateways.repository.PeripheralDeviceRepository;
import com.musalasoft.gateways.util.GatewayResponse;
import com.musalasoft.gateways.util.GatewayUtility;
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

        // Convert List of entities to List of DTOs
        List<GatewayDTO> gateways = GatewayUtility.convertToListDto(gatewayEntities);
        GatewayResponse<List<GatewayDTO>> response = new GatewayResponse<>(200,
            "Gateways Retrieved successfully", gateways);
        return ResponseEntity.ok(response);

    }

    public ResponseEntity<GatewayResponse<?>> getGatewayBySerialNumber(
        String serialNumber) {
        Optional<GatewayEntity> gateway = gatewayRepository.findBySerialNumber(serialNumber);

        if (gateway.isPresent()) {
            GatewayResponse<GatewayDTO> response = new GatewayResponse<>(200,
                "Gateway retrieved successfully", GatewayUtility.convertToDto(gateway.get()));
            return ResponseEntity.ok(response);
        } else {
            GatewayResponse<String> response = new GatewayResponse<>(404,
                "Gateway not found with serial number: " + serialNumber);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    public ResponseEntity<GatewayResponse<?>> createGateway(GatewayDTO gatewayDTO) {
        Optional<GatewayEntity> gatewayEntity = gatewayRepository.findBySerialNumber(
            gatewayDTO.getSerialNumber());
        if (gatewayEntity.isPresent()) {
            GatewayResponse<String> response = new GatewayResponse<>(409,
                "Gateway with serial number: " + gatewayDTO.getSerialNumber() + " already exists!");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        GatewayEntity gateway = new GatewayEntity(
            gatewayDTO.getSerialNumber(),
            gatewayDTO.getName(),
            gatewayDTO.getIpv4Address()
        );
        gatewayRepository.save(gateway);
        GatewayResponse<GatewayDTO> response = new GatewayResponse<>(200,
            "Gateway saved successfully in database", GatewayUtility.convertToDto(gateway));
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<GatewayResponse<String>> addDeviceToGateway(String serialNumber,
        List<PeripheralDeviceDTO> peripheralDevice) {
        Optional<GatewayEntity> gatewayEntity = gatewayRepository.findBySerialNumber(serialNumber);

        if (gatewayEntity.isPresent()) {
            GatewayEntity gateway = gatewayEntity.get();

            if (gateway.getDevices().size() >= 10) {
                GatewayResponse<String> response = new GatewayResponse<>(400,
                    "No more than 10 peripheral devices are allowed for a gateway");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            ResponseEntity<GatewayResponse<String>> devicePresent = checkIfDeviceExistsInAGateway(
                peripheralDevice, gateway);
            if (devicePresent != null) {
                return devicePresent;
            }

            List<PeripheralDeviceEntity> peripheralDevices = peripheralDevice.stream()
                .map(peripheralDeviceDTO -> {
                    PeripheralDeviceEntity peripheralDeviceEntity = new PeripheralDeviceEntity(
                        peripheralDeviceDTO.getUid(),
                        peripheralDeviceDTO.getVendor(),
                        peripheralDeviceDTO.getStatus()
                    );
                    peripheralDeviceEntity.setGateway(gateway);
                    return peripheralDeviceEntity;
                }).collect(Collectors.toList());

            gateway.setDevices(peripheralDevices);
            gatewayRepository.save(gateway);
            GatewayResponse<String> response = new GatewayResponse<>(200,
                "Device added successfully in " + gateway.getName() + " gateway");
            return ResponseEntity.ok(response);
        } else {
            GatewayResponse<String> response = new GatewayResponse<>(404,
                "Gateway not found with serial number: " + serialNumber);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    private static ResponseEntity<GatewayResponse<String>> checkIfDeviceExistsInAGateway(
        List<PeripheralDeviceDTO> peripheralDevice, GatewayEntity gateway) {
        boolean deviceExists;
        for (PeripheralDeviceEntity entity : gateway.getDevices()) {
            deviceExists = peripheralDevice.stream()
                .anyMatch(device -> device.getUid().equals(entity.getUid()));
            if (deviceExists) {
                GatewayResponse<String> response = new GatewayResponse<>(409,
                    "Device with uid: " + entity.getUid() + " already exists in a gateway!");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        }
        return null;
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
}
