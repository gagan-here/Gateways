package com.musalasoft.gateways.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GatewayResponse<T> {

    private int status;
    private String message;

    @JsonInclude(Include.NON_NULL)
    private T data;

    public GatewayResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

}
