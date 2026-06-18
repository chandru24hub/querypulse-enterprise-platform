package com.querypulse.backend.repository;

import com.querypulse.backend.entity.DatabaseAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DatabaseAlertRepository
        extends JpaRepository<DatabaseAlert, UUID> {

    List<DatabaseAlert>
    findByDatabaseIdOrderByCreatedAtDesc(
            UUID databaseId
    );

}