package com.querypulse.backend.repository;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.querypulse.backend.entity.DatabaseHealthHistory;

@Repository
public interface DatabaseHealthHistoryRepository
        extends JpaRepository<DatabaseHealthHistory, UUID> {

                List<DatabaseHealthHistory>
findByDatabaseIdOrderByRecordedAtAsc(
        UUID databaseId
);

void deleteByDatabaseId(UUID databaseId);

}