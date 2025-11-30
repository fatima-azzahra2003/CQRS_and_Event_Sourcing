package com.enset.bdcc.query.services;

import com.enset.bdcc.commonapi.enums.OperationType;
import com.enset.bdcc.commonapi.events.AccountActivatedEvent;
import com.enset.bdcc.commonapi.events.AccountCreatedEvent;
import com.enset.bdcc.commonapi.events.AccountCreditedEvent;
import com.enset.bdcc.commonapi.events.AccountDebitedEvent;
import com.enset.bdcc.commonapi.queries.GetAccountByIdQuery;
import com.enset.bdcc.commonapi.queries.GetAllAccountsQuery;
import com.enset.bdcc.query.entities.Account;
import com.enset.bdcc.query.entities.Operation;
import com.enset.bdcc.query.repositories.AccountRepository;
import com.enset.bdcc.query.repositories.OperationRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class AccountServiceHandler {
    private AccountRepository accountRepository;
    private OperationRepository operationRepository;
    @EventHandler
    public void on(AccountCreatedEvent event) {
        log.info("********************");
        log.info("AccountCreatedEvent received");
        Account account = Account.builder()
                .id(event.getId())
                .balance(event.getInitialBalance())
                .currency(event.getCurrency())
                .status(event.getStatus())
                .build();
        accountRepository.save(account);
    }
    @EventHandler
    public void on(AccountActivatedEvent event) {
        log.info("********************");
        log.info("AccountActivatedEvent received");
        Account account = accountRepository.findById(event.getId()).get();
        account.setStatus(event.getStatus());
    }
    @EventHandler
    public void on(AccountCreditedEvent event) {
        log.info("********************");
        log.info("AccountCreditedEvent received");
        Account account = accountRepository.findById(event.getId()).get();
        Operation operation = Operation.builder()
                .amount(event.getAmount())
                .date(new Date())
                .type(OperationType.CREDIT)
                .account(account)
                .build();
        operationRepository.save(operation);
        account.setBalance(account.getBalance() + event.getAmount());
        accountRepository.save(account);
    }
    @EventHandler
    public void on(AccountDebitedEvent event) {
        log.info("********************");
        log.info("AccountDebitedEvent received");
        Account account = accountRepository.findById(event.getId()).get();
        Operation operation = Operation.builder()
                .amount(event.getAmount())
                .date(new Date())
                .type(OperationType.DEBIT)
                .account(account)
                .build();
        operationRepository.save(operation);
        account.setBalance(account.getBalance() - event.getAmount());
        accountRepository.save(account);
    }

    @QueryHandler
    public List<Account> on(GetAllAccountsQuery query){
        return accountRepository.findAll();
    }
    @QueryHandler
    public Account on(GetAccountByIdQuery query){
        return accountRepository.findById(query.getId()).get();
    }
}
