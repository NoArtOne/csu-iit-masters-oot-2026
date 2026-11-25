package ru.csu.iit.master2026.oot.hw01orderflow.domain;

import java.util.Optional;

/**
 * Тариф доставки: сколько стоит и сколько дней везти.
 *
 * <p>Новый тариф — новый класс; код, который считает доставку, при этом не меняется.
 * Подробно о полиморфизме — на занятии 2.
 */
public interface DeliveryTariff {

    Money cost(Money goods);

    int days();

    // Задал минимальное поведение, если нужно его можно переопределять
    default Optional<String>  deliveryPromise() {
        return Optional.empty();
    }

    // Аналогично навряд ли каждая доставка будет Срочной, если все срочно, то ничего не срочно.
    default boolean isExpress() {
        return false;
    }
}
