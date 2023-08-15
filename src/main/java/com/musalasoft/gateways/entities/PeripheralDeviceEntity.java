package com.musalasoft.gateways.entities;

import com.musalasoft.gateways.enums.PeripheralDeviceStatus;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    public PeripheralDeviceEntity(String uid, String vendor, PeripheralDeviceStatus status) {
        this.uid = uid;
        this.vendor = vendor;
        this.dateCreated = LocalDate.now();
        this.status = status;
    }
}
