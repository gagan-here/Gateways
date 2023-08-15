package com.musalasoft.gateways.entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GatewayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serialNumber;

    private String name;

    private String ipv4Address;

    @OneToMany(mappedBy = "gateway", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PeripheralDeviceEntity> devices = new ArrayList<>();

    public GatewayEntity(String serialNumber, String name, String ipv4Address) {
        this.serialNumber = serialNumber;
        this.name = name;
        this.ipv4Address = ipv4Address;
    }
}
