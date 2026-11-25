package ru.csu.iit.master2026.oot.hw01orderflow.domain;

import java.util.Optional;

/** Экспресс: 500 руб., на следующий день. */
public record ExpressDelivery() implements DeliveryTariff {

    private static final Money PRICE = Money.rubles(500);

    @Override
    public Money cost(Money goods) {
        return PRICE;
    }

    @Override
    public int days() {
        return 1;
    }

    @Override
    public boolean isExpress() {
        return true;
    }

    @Override
    public Optional<String> deliveryPromise() {
        return Optional.of("Экспресс-доставка: привезём завтра.");
    }
}
