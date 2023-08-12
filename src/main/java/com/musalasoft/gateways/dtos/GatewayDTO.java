package com.musalasoft.gateways.dtos;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GatewayDTO {

    private String serialNumber;

    private String name;

    @Pattern(regexp = "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b") // IPv4 validation regex
    private String ipv4Address;

}
