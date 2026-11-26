package ru.csu.iit.master2026.oot.hw01bank;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankTest {

    @Test
    void amount_значимый_объект() {
        assertThat(new Amount(12_345)).isEqualTo(new Amount(12_345));
        assertThat(new Amount(12_345)).hasSameHashCodeAs(new Amount(12_345));
        assertThat(new Amount(123_405)).hasToString("1234.05 руб.");
        assertThat(Amount.rubles(5).plus(Amount.rubles(2))).isEqualTo(Amount.rubles(7));
        assertThat(Amount.rubles(5).minus(Amount.rubles(2))).isEqualTo(Amount.rubles(3));
        assertThat(Amount.rubles(5).isAtLeast(Amount.rubles(5))).isTrue();
        assertThatThrownBy(() -> new Amount(-1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Amount.rubles(2).minus(Amount.rubles(5)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void account_равенство_по_номеру() {
        Account first = Account.debit(new AccountId("same"));
        Account second = Account.credit(new AccountId("same"), Amount.rubles(1000));
        Account other = Account.debit(new AccountId("other"));

        assertThat(first).isEqualTo(second);
        assertThat(first).hasSameHashCodeAs(second);
        assertThat(first).isNotEqualTo(other);
    }

    @Test
    void кредитный_счёт_снимает_до_лимита() {
        Account account = Account.credit(new AccountId("credit"), Amount.rubles(1000));

        assertThat(account.available()).isEqualTo(Amount.rubles(1000));
        account.withdraw(Amount.rubles(1000));

        assertThat(account.balance()).isEqualTo(-100_000);
        assertThat(account.available()).isEqualTo(Amount.ZERO);
    }

    @Test
    void отмена_пополнения_без_денег_ничего_не_меняет() {
        Account account = Account.debit(new AccountId("debit"));
        Ledger ledger = new Ledger();
        ledger.run(new Deposit(account, Amount.rubles(100)));
        account.withdraw(Amount.rubles(100));

        assertThatThrownBy(ledger::undoLast).isInstanceOf(IllegalStateException.class);
        assertThat(account.balance()).isZero();
        assertThat(ledger.size()).isEqualTo(1);
    }

    @Test
    void отмена_пополнения_кредитного_счёта_не_уходит_в_кредит() {
        Account account = Account.credit(new AccountId("credit"), Amount.rubles(1000));
        Ledger ledger = new Ledger();
        ledger.run(new Deposit(account, Amount.rubles(500)));
        account.withdraw(Amount.rubles(500));

        assertThatThrownBy(ledger::undoLast).isInstanceOf(IllegalStateException.class);
        assertThat(account.balance()).isZero();
        assertThat(ledger.size()).isEqualTo(1);
    }

    @Test
    void перевод_на_тот_же_счёт_запрещён() {
        Account first = Account.debit(new AccountId("same"));
        Account second = Account.debit(new AccountId("same"));

        assertThatThrownBy(() -> new Transfer(first, second, Amount.rubles(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void отмена_перевода_атомарна() {
        Account from = Account.debit(new AccountId("from"));
        Account to = Account.debit(new AccountId("to"));
        from.deposit(Amount.rubles(500));
        Ledger ledger = new Ledger();
        ledger.run(new Transfer(from, to, Amount.rubles(200)));
        to.withdraw(Amount.rubles(200));

        assertThatThrownBy(ledger::undoLast).isInstanceOf(IllegalStateException.class);
        assertThat(from.balance()).isEqualTo(30_000);
        assertThat(to.balance()).isZero();
        assertThat(ledger.size()).isEqualTo(1);
    }

    @Test
    void откат_перевода_не_теряет_деньги() {
        Account from = Account.debit(new AccountId("from"));
        Account to = Account.debit(new AccountId("to"));
        from.deposit(Amount.rubles(1));
        to.deposit(new Amount(Long.MAX_VALUE - 50));

        assertThatThrownBy(() -> new Transfer(from, to, Amount.rubles(1)).execute())
                .isInstanceOf(ArithmeticException.class);
        assertThat(from.balance()).isEqualTo(100);
        assertThat(to.balance()).isEqualTo(Long.MAX_VALUE - 50);
    }

    @Test
    void упавшая_команда_не_попадает_в_журнал() {
        Account account = Account.debit(new AccountId("debit"));
        Ledger ledger = new Ledger();

        assertThatThrownBy(() -> ledger.run(new Withdraw(account, Amount.rubles(1))))
                .isInstanceOf(IllegalStateException.class);
        assertThat(ledger.size()).isZero();
    }
}
