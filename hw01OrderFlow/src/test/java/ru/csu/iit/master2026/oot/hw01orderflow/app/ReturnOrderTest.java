package ru.csu.iit.master2026.oot.hw01orderflow.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.csu.iit.master2026.oot.hw01orderflow.domain.CourierDelivery;
import ru.csu.iit.master2026.oot.hw01orderflow.domain.Customer;
import ru.csu.iit.master2026.oot.hw01orderflow.domain.CustomerId;
import ru.csu.iit.master2026.oot.hw01orderflow.domain.Email;
import ru.csu.iit.master2026.oot.hw01orderflow.domain.Money;
import ru.csu.iit.master2026.oot.hw01orderflow.domain.Order;
import ru.csu.iit.master2026.oot.hw01orderflow.domain.OrderId;
import ru.csu.iit.master2026.oot.hw01orderflow.domain.OrderStatus;
import ru.csu.iit.master2026.oot.hw01orderflow.infra.InMemoryOrderRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReturnOrderTest {

    private InMemoryOrderRepository repository;
    private RecordingNotifier notifier;
    private ReturnOrder returnOrderService;
    private Customer anna;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOrderRepository();
        notifier = new RecordingNotifier();
        returnOrderService = new ReturnOrder(repository, notifier);
        anna = new Customer(new CustomerId("c-1"), "Анна",
                new Email("anna@example.ru"), "+79000000001");
    }

    @Test
    @DisplayName("Успешный возврат отправленного заказа (SHIPPED -> RETURNED)")
    void successfulReturnFromShipped() {
        Order order = Order.builder()
                .customer(anna)
                .item("Кружка", 1, Money.rubles(350))
                .delivery(new CourierDelivery())
                .build(new OrderId("ORD-1"));
        order.pay();
        order.ship();
        repository.save(order);

        returnOrderService.returnOrder(new OrderId("ORD-1"));

        Order updated = repository.find(new OrderId("ORD-1")).orElseThrow();
        assertThat(updated.status()).isEqualTo(OrderStatus.RETURNED);
        assertThat(updated.status().paid()).isFalse();
        System.out.println(new YamlSalesReport(repository).render());
        assertThat(notifier.events).contains("cancelled ORD-1 refund 650.00 руб.");

        assertThat(new TextSalesReport(repository).render()).contains("выручка: 0.00 руб.");

        assertThatThrownBy(updated::pay).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(updated::ship).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(updated::cancel).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(updated::returnOrder).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Запрет возврата заказа из статусов NEW и PAID")
    void cannotReturnFromNewOrPaid() {
        Order order = Order.builder()
                .customer(anna)
                .item("Кружка", 1, Money.rubles(350))
                .delivery(new CourierDelivery())
                .build(new OrderId("ORD-1"));
        repository.save(order); // Статус NEW

        assertThatThrownBy(() -> returnOrderService.returnOrder(new OrderId("ORD-1")))
                .isInstanceOf(IllegalStateException.class);
        assertThat(notifier.events).isEmpty();

        order.pay();
        repository.save(order);

        assertThatThrownBy(() -> returnOrderService.returnOrder(new OrderId("ORD-1")))
                .isInstanceOf(IllegalStateException.class);
        assertThat(notifier.events).isEmpty();
        assertThat(order.status()).isEqualTo(OrderStatus.PAID);
    }
}
