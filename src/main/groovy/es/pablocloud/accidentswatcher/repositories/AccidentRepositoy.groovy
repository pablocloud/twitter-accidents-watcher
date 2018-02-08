package es.pablocloud.accidentswatcher.repositories

import es.pablocloud.accidentswatcher.domain.Accident
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccidentRepositoy extends JpaRepository<Accident, Integer> {

    Accident findByTwitterId(Long id)

    Accident findByInstagramId(String id)

}