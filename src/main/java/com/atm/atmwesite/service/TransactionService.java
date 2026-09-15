package com.atm.atmwesite.service;

import com.atm.atmwesite.model.Transaction;
import com.atm.atmwesite.model.User;
import com.atm.atmwesite.repository.TransactionRepository;
import com.atm.atmwesite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    public Transaction saveTransaction(Long userId, String type, double amount, double balanceAfter, String description) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        String refNo = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Transaction tx = new Transaction(amount, user, type, balanceAfter, refNo, description);
        return transactionRepository.save(tx);
    }

    public List<Transaction> getTransactions(Long userId) {
        return transactionRepository.findByUserIdOrderByTimestampDesc(userId);
    }
}
