package com.atm.atmwesite.repository;

import com.atm.atmwesite.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByCardNumberAndPin(String cardNumber, String pin);
    User findByCardNumber(String cardNumber);
}
