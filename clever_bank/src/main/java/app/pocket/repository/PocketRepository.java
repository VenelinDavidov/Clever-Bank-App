package app.pocket.repository;

import app.pocket.model.Pocket;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface PocketRepository extends JpaRepository <Pocket, UUID> {


    List <Pocket> findAllByCustomerUsername(String username);

    Optional <Pocket> findByIdAndCustomerId(UUID pocketId, UUID customerId);



}

