package ru.csu.iit.master2026.oot.hw01orderflow.domain;

import java.util.Optional;

public record SameDayDelivery() implements DeliveryTariff {

    private static final Money PRICE = Money.rubles(900);

    @Override
    public Money cost(Money goods) {
        return PRICE;
    }

    @Override
    public int days() {
        return 0;
    }

    @Override
    public Optional<String> deliveryPromise() {
        return Optional.of("Экспресс-доставка: привезём сегодня.");
    }

    @Override
    public boolean isExpress() {
        return true;
    }
}
