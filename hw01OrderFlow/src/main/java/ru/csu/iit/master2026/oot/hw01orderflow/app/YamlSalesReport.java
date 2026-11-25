package ru.csu.iit.master2026.oot.hw01orderflow.app;

import ru.csu.iit.master2026.oot.hw01orderflow.domain.Order;
import ru.csu.iit.master2026.oot.hw01orderflow.domain.OrderId;


//Отчёт по продажам в формате YAML (Вариант 26).

public final class YamlSalesReport extends SalesReport {

    private final OrderRepository orders;

    public YamlSalesReport(OrderRepository orders) {
        super(orders);
        this.orders = orders;
    }

    @Override
    protected void header(StringBuilder out) {
        if (orders.findAll().isEmpty()) {
            out.append("orders: []\n");
        } else {
            out.append("orders:\n");
        }
    }

    @Override
    protected void row(StringBuilder out, Order order) {
        out.append("  - id: ").append(order.id()).append("\n");
        out.append("    status: ").append(order.status()).append("\n");
        out.append("    totalKopecks: ").append(order.total().kopecks()).append("\n");
    }

    @Override
    protected void footer(StringBuilder out, Totals totals) {
        out.append("count: ").append(totals.count()).append("\n");
        out.append("express: ").append(totals.express()).append("\n");
        out.append("revenueKopecks: ").append(totals.revenue().kopecks()).append("\n");
        out.append("largest: ")
                .append(totals.largest().map(OrderId::toString).orElse("null"))
                .append("\n");
    }
}
