package ru.csu.iit.master2026.oot.hw01bank;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Примеры базовых сценариев: перевод с отменой, дебетовый и кредитный счёт. */
class BankExampleTest {

    private final Account anna = Account.debit(new AccountId("40817-001"));
    private final Account boris = Account.credit(new AccountId("40817-002"), Amount.rubles(1000));
    private final Ledger ledger = new Ledger();

    @Test
    void перевод_и_его_отмена() {
        ledger.run(new Deposit(anna, Amount.rubles(500)));
        ledger.run(new Transfer(anna, boris, Amount.rubles(200)));

        assertThat(anna.balance()).isEqualTo(30_000);
        assertThat(boris.balance()).isEqualTo(20_000);

        ledger.undoLast();
        // Своя проверка что есть ещё операция пополнения для отмены
        assertThat(ledger.size()).isEqualTo(1);
        assertThat(anna.balance()).isEqualTo(50_000);
        assertThat(boris.balance()).isZero();
    }

    @Test
    void дебетовый_счёт_не_уходит_в_минус() {
        assertThatThrownBy(() -> anna.withdraw(Amount.rubles(1)))
                .isInstanceOf(IllegalStateException.class);
        assertThat(anna.balance()).isZero();
    }

    @Test
    void кредитный_счёт_уходит_в_минус_до_лимита() {
        boris.withdraw(Amount.rubles(1000));

        assertThat(boris.balance()).isEqualTo(-100_000);
        assertThat(boris.available()).isEqualTo(Amount.ZERO);
    }
}
