package app.repository;

import app.model.Notification;
import app.model.NotificationPreference;
import app.model.NotificationStatus;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {


    @Query("""
            SELECT n FROM Notification n WHERE n.customerId = :customerId AND n.deleted = false
            """)
    List<Notification> findAllByCustomerIdAndDeletedIsFalse(UUID customerId);

    List<Notification> findByCustomerIdAndStatus(UUID customerId, NotificationStatus status);

}

