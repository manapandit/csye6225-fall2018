package demo.repositories;

import demo.models.Attachments;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttachmentRepository extends CrudRepository<Attachments, String> {

      @Query("select id FROM Attachments a WHERE a.id=:id")
       Optional<String> findAttachmentId(@Param("id") String id);



//      @Query("select * from Attachments a where a.user_transaction_id=:id")
//      String findTransAttach(@Param("id") String id);

//    @Query("DELETE Attachments a WHERE a.id=:id and a.user_transaction.id=:userid")
//    void deleteAttachment(@Param("id") String id, @Param("userid") String userid);

    @Transactional
    @Modifying
    @Query("DELETE FROM Attachments a WHERE a.id=:attachmentId")
    void deleteAttachment(@Param("attachmentId") String id);
}
