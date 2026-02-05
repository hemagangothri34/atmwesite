package com.atm.atmwesite.service;

import com.atm.atmwesite.model.User;
import com.atm.atmwesite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User login(String cardNumber, String pin) {
        return userRepository.findByCardNumberAndPin(cardNumber, pin);
    }

    public double getBalance(Long userId) {
        return userRepository.findById(userId).orElseThrow().getBalance();
    }

    public void deposit(Long userId, double amount) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setBalance(user.getBalance() + amount);
        userRepository.save(user);
    }

    public void withdraw(Long userId, double amount) {
        User user = userRepository.findById(userId).orElseThrow();

        if (user.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        user.setBalance(user.getBalance() - amount);
        userRepository.save(user);
    }
}
