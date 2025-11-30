package com.enset.bdcc.commands.aggregates;



import com.enset.bdcc.commonapi.commands.CreateAccountCommand;
import com.enset.bdcc.commonapi.commands.CreditAccountCommand;
import com.enset.bdcc.commonapi.commands.DebitAccountCommand;
import com.enset.bdcc.commonapi.enums.AccountStatus;
import com.enset.bdcc.commonapi.events.AccountActivatedEvent;
import com.enset.bdcc.commonapi.events.AccountCreatedEvent;
import com.enset.bdcc.commonapi.events.AccountCreditedEvent;
import com.enset.bdcc.commonapi.events.AccountDebitedEvent;
import com.enset.bdcc.commonapi.exceptions.AmountNegativeException;
import com.enset.bdcc.commonapi.exceptions.BallanceInsufficientException;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;


@Aggregate
public class AccountAggregate {
    @AggregateIdentifier
    private String accountId;
    private double balance;
    private String currency;
    private AccountStatus status;

    public AccountAggregate() {
        // REQUIRED by AXON
    }

    @CommandHandler
    public AccountAggregate(CreateAccountCommand createAccountCommand) {
        if (createAccountCommand.getInitialBalance() < 0) throw new RuntimeException("Balance negativee!!!");

        AggregateLifecycle.apply(new AccountCreatedEvent(
                createAccountCommand.getId(),
                createAccountCommand.getInitialBalance(),
                createAccountCommand.getCurrency(),
                AccountStatus.CREATED
        ));
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        this.accountId = event.getId();
        this.balance = event.getInitialBalance();
        this.currency = event.getCurrency();
        this.status = AccountStatus.CREATED;

        AggregateLifecycle.apply(new AccountActivatedEvent(
                event.getId(), AccountStatus.ACTIVATED
        ));
    }

    @EventSourcingHandler
    public void on(AccountActivatedEvent event) {
        this.status = event.getStatus();
    }
    @CommandHandler
    public void handle(CreditAccountCommand creditAccountCommand){
        if(creditAccountCommand.getAmount() < 0) throw new AmountNegativeException("Amount negative!!!");
        AggregateLifecycle.apply(new AccountCreditedEvent(
                creditAccountCommand.getId(),
                creditAccountCommand.getAmount(),
                creditAccountCommand.getCurrency()
        ));
    }
    @EventSourcingHandler
    public void on(AccountCreditedEvent event) {
        this.balance += event.getAmount();
    }

    @CommandHandler
    public void handle(DebitAccountCommand debitAccountCommand){
        if(debitAccountCommand.getAmount() < 0) throw new AmountNegativeException("Amount negative!!!");
        if (this.balance < debitAccountCommand.getAmount()) throw new BallanceInsufficientException("Balance not sufficient!!!");
        AggregateLifecycle.apply(new AccountDebitedEvent(
                debitAccountCommand.getId(),
                debitAccountCommand.getAmount(),
                debitAccountCommand.getCurrency()
        ));
    }
    @EventSourcingHandler
    public void on(AccountDebitedEvent event) {
        this.balance -= event.getAmount();
    }
}