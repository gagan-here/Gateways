package com.musalasoft.gateways.entities;

import com.musalasoft.gateways.enums.PeripheralDeviceStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
public class PeripheralDeviceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uid;

    private String vendor;

    private LocalDate dateCreated;

    @Enumerated(EnumType.STRING)
    private PeripheralDeviceStatus status;

    @ManyToOne
    @JoinColumn(name = "gateway_id")
    private GatewayEntity gateway;

}
