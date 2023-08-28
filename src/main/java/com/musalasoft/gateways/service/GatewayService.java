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

/**
 * Service class that handles operations related to gateways and peripheral devices.
 */
@Service
public class GatewayService {

    @Autowired
    private GatewayRepository gatewayRepository;

    @Autowired
    private PeripheralDeviceRepository deviceRepository;


    /**
     * Retrieves a list of all gateways from the database.
     *
     * @return ResponseEntity containing a list of GatewayDTOs in the response body.
     */
    public ResponseEntity<GatewayResponse<List<GatewayDTO>>> getAllGateways() {
        List<GatewayEntity> gatewayEntities = gatewayRepository.findAll();

        // Convert List of entities to List of DTOs
        List<GatewayDTO> gateways = GatewayUtility.convertToListDto(gatewayEntities);
        GatewayResponse<List<GatewayDTO>> response = new GatewayResponse<>(200,
            "Gateways Retrieved successfully", gateways);
        return ResponseEntity.ok(response);

    }

    /**
     * Retrieves a gateway by its serial number from the database.
     *
     * @param serialNumber The serial number of the gateway to retrieve.
     * @return ResponseEntity containing the retrieved GatewayDTO in the response body.
     *         If the gateway is not found, a 404 response is returned.
     */
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

    /**
     * Creates a new gateway in the database.
     *
     * @param gatewayDTO The GatewayDTO containing information for the new gateway.
     * @return ResponseEntity indicating the status of the operation and a message in the response body.
     *         If a gateway with the same serial number already exists, a 409 conflict response is returned.
     */
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


    /**
     * Adds peripheral devices to a gateway.
     *
     * @param serialNumber     The serial number of the gateway.
     * @param peripheralDevice List of PeripheralDeviceDTOs to add.
     * @return ResponseEntity indicating the status of the operation and a message in the response body.
     *         If the gateway does not exist, a 404 response is returned.
     *         If there are more than 10 devices, a 400 bad request response is returned.
     *         If a device already exists in the gateway, a 409 conflict response is returned.
     */
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

    /**
     * Removes a device from a gateway.
     *
     * @param deviceId The ID of the device to remove.
     * @return ResponseEntity indicating the status of the operation and a message in the response body.
     *         If the device is not found, a 404 response is returned.
     */
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
