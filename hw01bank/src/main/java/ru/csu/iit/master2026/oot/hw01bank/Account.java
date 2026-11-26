package ru.csu.iit.master2026.oot.hw01bank;

import java.util.Objects;

/**
 * Счёт клиента — сущность: равенство по номеру, баланс меняется.
 *
 * <p>Два вида счетов:
 * <ul>
 *   <li>дебетовый — баланс никогда не меньше нуля;</li>
 *   <li>кредитный с лимитом — баланс никогда не меньше {@code -limit}.</li>
 * </ul>
 *
 * <p>Как устроить два вида внутри — решать вам; снаружи доступны только фабрики ниже.
 * Добавлять поля, классы и интерфейсы можно, менять публичные сигнатуры — нельзя.
 */
public final class Account {

    // Баланс со знаком: кредитный счёт может уйти в минус, Amount отрицательным быть не может.
    // exact-арифметика: переполнение long не должно молча сменить знак.
    private final AccountId id;
    private final long limit;
    private long balance;

    private Account(AccountId id, long limit) {
        this.id = Objects.requireNonNull(id);
        this.limit = limit;
    }

    /** Дебетовый счёт с нулевым балансом. */
    public static Account debit(AccountId id) {
        return new Account(id, 0);
    }

    /** Кредитный счёт с нулевым балансом и лимитом {@code limit}. */
    public static Account credit(AccountId id, Amount limit) {
        return new Account(id, limit.kopecks());
    }

    public AccountId id() {
        return id;
    }

    /** Баланс в копейках; отрицательный — только у кредитного счёта. */
    public long balance() {
        return balance;
    }

    /** Сколько можно снять прямо сейчас, не нарушив инвариант счёта. */
    public Amount available() {
        return new Amount(Math.addExact(balance, limit));
    }

    public void deposit(Amount amount) {
        balance = Math.addExact(balance, amount.kopecks());
    }

    /**
     * @throws IllegalStateException если {@code amount} больше {@link #available()};
     *                               баланс при этом не меняется
     */
    public void withdraw(Amount amount) {
        if (amount.kopecks() > available().kopecks()) {
            throw new IllegalStateException("Недостаточно средств на счёте " + id);
        }
        balance = Math.subtractExact(balance, amount.kopecks());
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Account other && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
