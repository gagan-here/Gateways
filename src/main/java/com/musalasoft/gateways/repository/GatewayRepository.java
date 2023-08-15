package com.musalasoft.gateways.repository;

import com.musalasoft.gateways.entities.GatewayEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GatewayRepository extends JpaRepository<GatewayEntity, Long> {

   Optional<GatewayEntity> findBySerialNumber(String serialNumner);
}
