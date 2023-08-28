package com.musalasoft.gateways.util;

import com.musalasoft.gateways.dtos.GatewayDTO;
import com.musalasoft.gateways.dtos.PeripheralDeviceDTO;
import com.musalasoft.gateways.entities.GatewayEntity;
import com.musalasoft.gateways.entities.PeripheralDeviceEntity;
import java.util.ArrayList;
import java.util.List;

public class GatewayUtility {

    public static List<GatewayDTO> convertToListDto(List<GatewayEntity> gatewayEntities) {
        List<GatewayDTO> gatewayDTOs = new ArrayList<>();
        for (GatewayEntity gatewayEntity : gatewayEntities) {
            gatewayDTOs.add(convertToDto(gatewayEntity));
        }
        return gatewayDTOs;
    }

    public static GatewayDTO convertToDto(GatewayEntity gatewayEntity) {
        return new GatewayDTO(
            gatewayEntity.getSerialNumber(),
            gatewayEntity.getName(),
            gatewayEntity.getIpv4Address(),
            convertToListDeviceDto(gatewayEntity.getDevices())
        );
    }

    public static List<PeripheralDeviceDTO> convertToListDeviceDto(
        List<PeripheralDeviceEntity> deviceEntities) {
        List<PeripheralDeviceDTO> peripheralDeviceDTOS = new ArrayList<>();
        for (PeripheralDeviceEntity deviceEntity : deviceEntities) {
            peripheralDeviceDTOS.add(convertDeviceToDto(deviceEntity));
        }
        return peripheralDeviceDTOS;
    }

    public static PeripheralDeviceDTO convertDeviceToDto(PeripheralDeviceEntity deviceEntity) {
        return new PeripheralDeviceDTO(
            deviceEntity.getUid(),
            deviceEntity.getVendor(),
            deviceEntity.getDateCreated(),
            deviceEntity.getStatus()
        );
    }

}
