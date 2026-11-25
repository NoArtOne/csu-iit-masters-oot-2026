package ru.csu.iit.master2026.oot.hw01orderflow.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class SameDayDeliveryTest {
    @Test
    @DisplayName("SameDayDelivery стоит 900 руб., имеет срок 0 дней, является экспрессом и дает правильное обещание")
    void testSameDayDeliveryProperties(){
        SameDayDelivery delivery = new SameDayDelivery();

        // Сколько бы денег не стоил товар для SameDay всегда вернется 900
        assertThat(Money.rubles(900)).isEqualTo(delivery.cost(Money.rubles(350)));
        assertThat(Money.rubles(900)).isEqualTo(delivery.cost(Money.rubles(0)));
        assertThat(Money.rubles(900)).isEqualTo(delivery.cost(Money.rubles(100_000)));

        // Срок доставки
        assertThat(0).isEqualTo(delivery.days());

        // Флаг что доставка действительно экспресс
        assertThat(true).isEqualTo(delivery.isExpress());
        // Текст промисс для Sameday
        assertThat(Optional.of("Экспресс-доставка: привезём сегодня.")).isEqualTo(delivery.deliveryPromise());
    }
}
