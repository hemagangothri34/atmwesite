package com.atm.atmwesite;

import com.atm.atmwesite.model.User;
import com.atm.atmwesite.model.Transaction;
import com.atm.atmwesite.repository.UserRepository;
import com.atm.atmwesite.repository.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class AtmwesiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(AtmwesiteApplication.class, args);
    }

   
}
