package com.musalasoft.gateways.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GatewayDTO {

    @NotBlank(message = "Serial number is not present in request body")
    private String serialNumber;

    @NotBlank(message = "Name is not present in request body")
    private String name;

    @NotBlank(message = "ipv4Address is not present in request body")
    // IPv4 validation regex
    @Pattern(regexp = "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b", message = "Invalid IPv4 address")
    private String ipv4Address;

    @JsonInclude(Include.NON_NULL)
    private List<PeripheralDeviceDTO> devices = new ArrayList<>();

}
