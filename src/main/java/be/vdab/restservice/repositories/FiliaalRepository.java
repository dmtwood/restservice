package be.vdab.restservice.repositories;

import be.vdab.restservice.domain.Filiaal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FiliaalRepository extends JpaRepository<Filiaal, Long> {

}
