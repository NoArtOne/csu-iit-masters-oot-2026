package ru.csu.iit.master2026.oot.hw01orderflow.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.csu.iit.master2026.oot.hw01orderflow.domain.*;
import ru.csu.iit.master2026.oot.hw01orderflow.infra.InMemoryOrderRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.csu.iit.master2026.oot.hw01orderflow.domain.Money.rubles;

class SalesReportTest {

    private final InMemoryOrderRepository repository = new InMemoryOrderRepository();
    private Customer anna;

    @BeforeEach
    void twoOrders() {
        anna = new Customer(new CustomerId("c-1"), "Анна",
                new Email("anna@example.ru"), "+79000000001");
        PlaceOrder placeOrder = new PlaceOrder(repository, new RecordingNotifier());
        Order paid = placeOrder.place(Order.builder()
                .customer(anna)
                .item("Ноутбук", 1, rubles(64990))
                .delivery(new ExpressDelivery()));
        paid.pay();
        placeOrder.place(Order.builder()
                .customer(anna)
                .item("Кружка", 1, rubles(350))
                .delivery(new CourierDelivery()));
    }

    @Test
    void один_скелет_текстовый_формат() {
        assertThat(new TextSalesReport(repository).render()).isEqualTo("""
                ORD-1  PAID      65490.00 руб.
                ORD-2  NEW       650.00 руб.
                Заказов: 2, экспресс: 1, выручка: 65490.00 руб., крупнейший: ORD-1""");
    }

    @Test
    void один_скелет_csv() {
        assertThat(new CsvSalesReport(repository).render()).isEqualTo("""
                id;status;total_kopecks
                ORD-1;PAID;6549000
                ORD-2;NEW;65000
                TOTAL;2;6549000
                """);
    }

    @Test
    @DisplayName("Отчёт должен считать экспрессом и ExpressDelivery, и SameDayDelivery")
    void reportCountsBothExpressTariffs() {
        PlaceOrder placeOrder = new PlaceOrder(repository, new RecordingNotifier());
        placeOrder.place(Order.builder()
                .customer(anna)
                .item("Флешка", 1, rubles(1000))
                .delivery(new SameDayDelivery()));

        String rendered = new TextSalesReport(repository).render();
        System.out.println(rendered);
        assertThat(rendered).contains("Заказов: 3, экспресс: 2");
    }

}
