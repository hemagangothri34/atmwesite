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
        if (cardNumber == null || pin == null) return null;
        return userRepository.findByCardNumberAndPin(cardNumber.trim(), pin.trim());
    }

    public User getUserInfo(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public double getBalance(Long userId) {
        return getUserInfo(userId).getBalance();
    }

    public User deposit(Long userId, double amount) {
        if (amount <= 0) {
            throw new RuntimeException("Deposit amount must be greater than zero");
        }
        User user = getUserInfo(userId);
        user.setBalance(user.getBalance() + amount);
        return userRepository.save(user);
    }

    public User withdraw(Long userId, double amount) {
        if (amount <= 0) {
            throw new RuntimeException("Withdrawal amount must be greater than zero");
        }
        User user = getUserInfo(userId);

        if (user.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds. Current balance: ₹" + String.format("%.2f", user.getBalance()));
        }

        user.setBalance(user.getBalance() - amount);
        return userRepository.save(user);
    }

    public User transfer(Long senderId, String targetCardNumber, double amount) {
        if (amount <= 0) {
            throw new RuntimeException("Transfer amount must be greater than zero");
        }
        User sender = getUserInfo(senderId);

        if (sender.getCardNumber().equals(targetCardNumber)) {
            throw new RuntimeException("Cannot transfer funds to your own card");
        }

        if (sender.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds for transfer");
        }

        User recipient = userRepository.findByCardNumber(targetCardNumber);
        if (recipient == null) {
            throw new RuntimeException("Recipient card number not found");
        }

        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);

        userRepository.save(recipient);
        return userRepository.save(sender);
    }

    public void changePin(Long userId, String currentPin, String newPin) {
        if (newPin == null || newPin.length() < 4) {
            throw new RuntimeException("New PIN must be at least 4 digits");
        }
        User user = getUserInfo(userId);

        if (!user.getPin().equals(currentPin)) {
            throw new RuntimeException("Current PIN is incorrect");
        }

        user.setPin(newPin);
        userRepository.save(user);
    }
}
