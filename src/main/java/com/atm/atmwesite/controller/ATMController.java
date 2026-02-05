package com.atm.atmwesite.controller;

import com.atm.atmwesite.model.User;
import com.atm.atmwesite.service.UserService;
import com.atm.atmwesite.service.TransactionService;
import com.atm.atmwesite.controller.LoginRequest;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/atm")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class ATMController {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    // ================= LOGIN =================
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request, HttpSession session) {

        User user = userService.login(
                request.getCardNumber(),
                request.getPin()
        );

        if (user == null) {
            throw new RuntimeException("Invalid card number or PIN");
        }

        session.setAttribute("userId", user.getId());
        return "Login successful";
    }

    // ================= BALANCE =================
    @GetMapping("/balance")
    public String balance(HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            throw new RuntimeException("Please login first");
        }

        double balance = userService.getBalance(userId);
        return String.valueOf(balance);
    }

    // ================= DEPOSIT =================
    @PostMapping("/deposit/{amount}")
public String deposit(@PathVariable double amount, HttpSession session) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) throw new RuntimeException("Login first");

    userService.deposit(userId, amount);
    transactionService.saveTransaction(userId, "DEPOSIT", amount);

    return "Deposit successful";
}


    // ================= WITHDRAW =================
    
@PostMapping("/withdraw/{amount}")
public String withdraw(@PathVariable double amount, HttpSession session) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) throw new RuntimeException("Login first");

    userService.withdraw(userId, amount);
    transactionService.saveTransaction(userId, "WITHDRAW", amount);

    return "Withdraw successful";
}
    // ================= LOGOUT =================
    @PostMapping("/logout")
    public void logout(HttpSession session) {
        session.invalidate();
    }
}
