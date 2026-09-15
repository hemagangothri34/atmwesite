package com.atm.atmwesite.controller;

import com.atm.atmwesite.model.User;
import com.atm.atmwesite.model.Transaction;
import com.atm.atmwesite.service.UserService;
import com.atm.atmwesite.service.TransactionService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/atm")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class ATMController {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    private Long getSessionUserId(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("Session expired. Please login again.");
        }
        return userId;
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request, HttpSession session) {
        if (request.getCardNumber() == null || request.getCardNumber().trim().isEmpty() ||
            request.getPin() == null || request.getPin().trim().isEmpty()) {
            throw new RuntimeException("Card Number and PIN are required.");
        }

        User user = userService.login(request.getCardNumber().trim(), request.getPin().trim());

        if (user == null) {
            throw new RuntimeException("Invalid Card Number or PIN");
        }

        session.setAttribute("userId", user.getId());
        return "Login successful";
    }

    // ================= USER INFO =================
    @GetMapping("/user-info")
    public User getUserInfo(HttpSession session) {
        Long userId = getSessionUserId(session);
        User user = userService.getUserInfo(userId);
        // Clear sensitive pin before returning to front-end
        User safeUser = new User();
        safeUser.setCardNumber(user.getCardNumber());
        safeUser.setName(user.getName());
        safeUser.setAccountNumber(user.getAccountNumber());
        safeUser.setAccountType(user.getAccountType());
        safeUser.setStatus(user.getStatus());
        safeUser.setBalance(user.getBalance());
        return safeUser;
    }

    // ================= BALANCE =================
    @GetMapping("/balance")
    public String balance(HttpSession session) {
        Long userId = getSessionUserId(session);
        double balance = userService.getBalance(userId);
        return String.valueOf(balance);
    }

    // ================= DEPOSIT =================
    @PostMapping("/deposit/{amount}")
    public String deposit(@PathVariable double amount, HttpSession session) {
        Long userId = getSessionUserId(session);
        User updatedUser = userService.deposit(userId, amount);
        transactionService.saveTransaction(userId, "DEPOSIT", amount, updatedUser.getBalance(), "Cash Deposit at ATM Terminal");
        return "Deposit of ₹" + String.format("%.2f", amount) + " successful!";
    }

    // ================= WITHDRAW =================
    @PostMapping("/withdraw/{amount}")
    public String withdraw(@PathVariable double amount, HttpSession session) {
        Long userId = getSessionUserId(session);
        User updatedUser = userService.withdraw(userId, amount);
        transactionService.saveTransaction(userId, "WITHDRAW", amount, updatedUser.getBalance(), "Cash Withdrawal from ATM Terminal");
        return "Withdrawal of ₹" + String.format("%.2f", amount) + " successful!";
    }

    // ================= TRANSFER =================
    @PostMapping("/transfer")
    public String transfer(@RequestBody TransferRequest request, HttpSession session) {
        Long userId = getSessionUserId(session);
        User updatedUser = userService.transfer(userId, request.getTargetCardNumber(), request.getAmount());
        transactionService.saveTransaction(userId, "TRANSFER", request.getAmount(), updatedUser.getBalance(), "Fund Transfer to Card: " + request.getTargetCardNumber());
        return "Successfully transferred ₹" + String.format("%.2f", request.getAmount()) + " to card " + request.getTargetCardNumber();
    }

    // ================= CHANGE PIN =================
    @PostMapping("/change-pin")
    public String changePin(@RequestBody ChangePinRequest request, HttpSession session) {
        Long userId = getSessionUserId(session);
        userService.changePin(userId, request.getCurrentPin(), request.getNewPin());
        return "PIN successfully updated!";
    }

    // ================= TRANSACTIONS =================
    @GetMapping("/transactions")
    public List<Transaction> getTransactions(HttpSession session) {
        Long userId = getSessionUserId(session);
        return transactionService.getTransactions(userId);
    }

    // ================= LOGOUT =================
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "Logged out successfully";
    }
}
