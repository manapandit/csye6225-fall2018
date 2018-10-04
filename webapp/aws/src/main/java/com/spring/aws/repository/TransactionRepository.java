package com.spring.aws.repository;

import com.spring.aws.Pojo.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transactions,Long> {

   // Transactions findTransactionByTransactionId(String transactionId);

 //   List<Transactions> findTransactionByUserUserId(Long uId);
  //  List<Transactions> findTransactions(Long uId);

}