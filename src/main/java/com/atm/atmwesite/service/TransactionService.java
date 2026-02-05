package com.atm.atmwesite.service;

import com.atm.atmwesite.model.Transaction;
import com.atm.atmwesite.model.User;
import com.atm.atmwesite.repository.TransactionRepository;
import com.atm.atmwesite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    public void saveTransaction(Long userId, String type, double amount) {
        User user = userRepository.findById(userId).orElseThrow();

        Transaction tx = new Transaction();
        tx.setUser(user);
        tx.setType(type);
        tx.setAmount(amount);

        transactionRepository.save(tx);
    }

    public List<Transaction> getTransactions(Long userId) {
        return transactionRepository.findByUserId(userId);
    }
}
