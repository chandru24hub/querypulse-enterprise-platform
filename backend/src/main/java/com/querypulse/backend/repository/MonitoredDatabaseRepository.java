package com.querypulse.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.querypulse.backend.entity.MonitoredDatabase;

@Repository
public interface MonitoredDatabaseRepository
extends JpaRepository<MonitoredDatabase, UUID> {
}