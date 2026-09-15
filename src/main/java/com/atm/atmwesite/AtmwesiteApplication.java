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

    @Bean
    public CommandLineRunner dataSeeder(UserRepository userRepository, TransactionRepository transactionRepository) {
        return args -> {
            // User 1: Hema Gangothri (1234567890 / 1234)
            User u1 = userRepository.findByCardNumber("1234567890");
            if (u1 == null) {
                u1 = new User();
                u1.setCardNumber("1234567890");
                u1.setPin("1234");
                u1.setName("Hema Gangothri");
                u1.setAccountNumber("ACC-90482103");
                u1.setAccountType("Savings Account");
                u1.setStatus("ACTIVE");
                u1.setBalance(25000.00);
                u1 = userRepository.save(u1);

                Transaction t1 = new Transaction(20000.00, u1, "DEPOSIT", 20000.00, "TXN-INIT001", "Initial Account Opening Deposit");
                t1.setTimestamp(LocalDateTime.now().minusDays(5));
                transactionRepository.save(t1);

                Transaction t2 = new Transaction(5000.00, u1, "DEPOSIT", 25000.00, "TXN-DEP002", "Cash Deposit via ATM Terminal");
                t2.setTimestamp(LocalDateTime.now().minusDays(1));
                transactionRepository.save(t2);
            } else {
                u1.setPin("1234");
                if (u1.getName() == null) u1.setName("Hema Gangothri");
                if (u1.getAccountNumber() == null) u1.setAccountNumber("ACC-90482103");
                userRepository.save(u1);
            }

            // User 2: Manikanta Mattaparth (9876543210 / 4321)
            User u2 = userRepository.findByCardNumber("9876543210");
            if (u2 == null) {
                u2 = new User();
                u2.setCardNumber("9876543210");
                u2.setPin("4321");
                u2.setName("Manikanta Mattaparth");
                u2.setAccountNumber("ACC-77310944");
                u2.setAccountType("Checking Account");
                u2.setStatus("ACTIVE");
                u2.setBalance(50000.00);
                u2 = userRepository.save(u2);

                Transaction t3 = new Transaction(50000.00, u2, "DEPOSIT", 50000.00, "TXN-INIT002", "Initial Salary Transfer");
                t3.setTimestamp(LocalDateTime.now().minusDays(3));
                transactionRepository.save(t3);
            } else {
                u2.setPin("4321");
                if (u2.getName() == null) u2.setName("Manikanta Mattaparth");
                if (u2.getAccountNumber() == null) u2.setAccountNumber("ACC-77310944");
                userRepository.save(u2);
            }

            System.out.println("=================================================");
            System.out.println("   DEMO DATA SEEDED / VERIFIED FOR ATM WEBSITE   ");
            System.out.println("   Card 1: 1234567890 | PIN: 1234 | Hema         ");
            System.out.println("   Card 2: 9876543210 | PIN: 4321 | Manikanta    ");
            System.out.println("=================================================");
        };
    }
}
