package app.pocket.repository;

import app.pocket.model.Pocket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface PocketRepository extends JpaRepository <Pocket, UUID> {


    List <Pocket> findAllByCustomerUsername(String username);
}

