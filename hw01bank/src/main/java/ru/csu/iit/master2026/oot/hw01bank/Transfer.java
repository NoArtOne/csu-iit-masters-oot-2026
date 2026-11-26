package ru.csu.iit.master2026.oot.hw01bank;

import java.util.Objects;

/**
 * Перевод между счетами. Атомарен: и выполнение, и отмена либо меняют оба счёта, либо ни одного.
 */
public final class Transfer implements Command {

    private final Account from;
    private final Account to;
    private final Amount amount;

    /** @throws IllegalArgumentException если {@code from} и {@code to} — один и тот же счёт */
    public Transfer(Account from, Account to, Amount amount) {
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.amount = Objects.requireNonNull(amount);
        if (from.id().equals(to.id())) {
            throw new IllegalArgumentException("Перевод на тот же счёт: " + from.id());
        }
    }

    @Override
    public void execute() {
        from.withdraw(amount);
        try {
            to.deposit(amount);
        } catch (RuntimeException e) {
            from.deposit(amount);
            throw e;
        }
    }

    @Override
    public void undo() {
        to.withdraw(amount);
        try {
            from.deposit(amount);
        } catch (RuntimeException e) {
            to.deposit(amount);
            throw e;
        }
    }
}
