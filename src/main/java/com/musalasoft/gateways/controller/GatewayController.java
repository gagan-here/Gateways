package com.musalasoft.gateways.controller;

import com.musalasoft.gateways.dtos.GatewayDTO;
import com.musalasoft.gateways.dtos.PeripheralDeviceDTO;
import com.musalasoft.gateways.service.GatewayService;
import com.musalasoft.gateways.util.GatewayResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gateways")
public class GatewayController {

    @Autowired
    private GatewayService gatewayService;

    @GetMapping
    public ResponseEntity<GatewayResponse<List<GatewayDTO>>> getAllGateways() {
        return gatewayService.getAllGateways();
    }

    @GetMapping("/{serialNumber}")
    public ResponseEntity<GatewayResponse<?>> getGatewayBySerialNumber(
        @PathVariable String serialNumber) {
        return gatewayService.getGatewayBySerialNumber(serialNumber);
    }

    @PostMapping("/{gatewaySerialNumber}/devices")
    public ResponseEntity<GatewayResponse<String>> addDeviceToGateway(
        @PathVariable String gatewaySerialNumber,
        @RequestBody List<PeripheralDeviceDTO> peripheralDevice) {
        return gatewayService.addDeviceToGateway(gatewaySerialNumber, peripheralDevice);
    }

    @DeleteMapping("/devices/{deviceId}")
    public ResponseEntity<GatewayResponse<String>> removeDeviceFromGateway(
        @PathVariable Long deviceId) {
        return gatewayService.removeDeviceFromGateway(deviceId);
    }
}
