package demo.repositories;

import demo.models.Reciept;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface AttachmentRepo extends CrudRepository<Reciept, String> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Reciept r WHERE r.id=:rid")
    void deleteRecieptBy(@Param("rid") String id);
}
