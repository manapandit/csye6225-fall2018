package demo.repositories;

import demo.models.Attachments;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface AttachmentRepository extends CrudRepository<Attachments, String> {

//    @Query("from Attachments a WHERE a.attachments.id=:user_transaction_id")
//    List<Attachments> findIdByTransactionId(@Param("user_transaction_id") String user_transaction_id);
//
//
//    @Transactional
//    @Modifying
//    @Query("DELETE FROM Attachments a WHERE a.id=:id and a.transaction.id=:transactionid")
//    void deleteAttachments(@Param("id") String id, @Param("transactionid") String transactionid);
//
//    @Query("select id FROM Attachments a WHERE a.id=:id and a.transaction.id=:transactionid")
//    Optional<String> findUid(@Param("id") String id, @Param("transactionid") String transactionid);
//
//    @Transactional
//    @Modifying
//    @Query("UPDATE Attachments a SET file_name=:f,file_location=:s WHERE a.id=:id and u.transaction.id=:transactionid")
//    void updateAttachments(@Param("id") String id, @Param("transactionid") String transactionid, @Param("f") String file_name,
//                           @Param("s") String file_location);

}
