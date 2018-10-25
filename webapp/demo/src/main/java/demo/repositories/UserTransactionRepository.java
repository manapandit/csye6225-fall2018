package demo.repositories;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import demo.models.UserTransaction;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTransactionRepository extends CrudRepository<UserTransaction, String> {

	@Query("from UserTransaction u WHERE u.user.id=:user_id")
	List<UserTransaction> findIdByUserId(@Param("user_id") int user_id);

	@Query("from UserTransaction u where u.id=:user_id")
	UserTransaction findAllIDByUserId(@Param("user_id") String ut_id);

	@Transactional
	@Modifying
	@Query("DELETE FROM UserTransaction u WHERE u.id=:id and u.user.id=:userid")
	void deleteTransaction(@Param("id") String id, @Param("userid") int userid);

	
	@Query("select id FROM UserTransaction u WHERE u.id=:id and u.user.id=:userid")
	Optional<String> findUid(@Param("id") String id, @Param("userid") int userid);

	@Query("select id FROM UserTransaction u WHERE u.id=:id")
	Optional<String> findTransactionId(@Param("id") String id);

	@Query("select id FROM UserTransaction u WHERE u.id=:id")
	String findTranactionId(@Param("id") String id);

//	@Query("select reciept_id FROM UserTransaction u WHERE u.id=:id")
//    String findReciept(@Param("id") String id);

//	@Transactional
//	@Modifying
//	@Query("UPDATE UserTransaction u SET attachment_id = null where u.id=:id and u.attachments_id=:attId")
//	void deleteAttachmentId(@Param("id") String id,@Param("attId") String attId);

	@Transactional
	@Modifying
	@Query("UPDATE UserTransaction u SET description=:d,merchant=:m,amount=:a,date=:dt,category=:c WHERE u.id=:id and u.user.id=:userid")
	void updateTransaction(@Param("id") String id, @Param("userid") int userid, @Param("d") String d,
			@Param("a") String a, @Param("c") String c, @Param("dt") String dt, @Param("m") String m);

}
