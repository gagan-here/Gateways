package com.musalasoft.gateways.dtos;

import com.musalasoft.gateways.enums.PeripheralDeviceStatus;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PeripheralDeviceDTO {

    private String uid;

    private String vendor;

    private LocalDate dateCreated;

    private PeripheralDeviceStatus status;

}
