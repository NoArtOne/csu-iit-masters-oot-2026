package ru.csu.iit.master2026.oot.hw01orderflow.app;

import ru.csu.iit.master2026.oot.hw01orderflow.domain.Money;
import ru.csu.iit.master2026.oot.hw01orderflow.domain.Order;
import ru.csu.iit.master2026.oot.hw01orderflow.domain.OrderId;


//Сценарий возврата отправленного заказа (Вариант 26, Часть Б).

public final class ReturnOrder {

    private final OrderRepository orders;
    private final Notifier notifier;

    public ReturnOrder(OrderRepository orders, Notifier notifier) {
        this.orders = orders;
        this.notifier = notifier;
    }

    public void returnOrder(OrderId id) {
        Order order = orders.find(id)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден: " + id));

        Money refund = order.total();
        order.returnOrder();
        orders.save(order);
        notifier.orderCancelled(order, refund);
    }
}
