package com.nexo.ecommerce.notifications.infrastructure.pdf;

import com.nexo.ecommerce.notifications.application.ports.ReceiptGeneratorPort;
import com.nexo.ecommerce.notifications.application.usecases.dto.OrderItemResponse;
import com.nexo.ecommerce.notifications.application.usecases.dto.OrderResponse;
import com.nexo.ecommerce.notifications.application.usecases.dto.UserResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JasperReceiptGeneratorAdapter implements ReceiptGeneratorPort {

    private static final String TEMPLATE_PATH = "reports/order-receipt.jrxml";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final JasperReport compiledReport;
    private final ReceiptProperties receiptProperties;

    @Autowired
    public JasperReceiptGeneratorAdapter(ReceiptProperties receiptProperties) {
        this(receiptProperties, loadTemplate());
    }

    JasperReceiptGeneratorAdapter(ReceiptProperties receiptProperties, InputStream template) {
        this.receiptProperties = receiptProperties;
        this.compiledReport = compileTemplate(template);
    }

    @Override
    public byte[] generateReceipt(OrderResponse order, UserResponse user) {
        try {
            Map<String, Object> parameters = parameters(order, user);
            JRMapCollectionDataSource itemDataSource = new JRMapCollectionDataSource(items(order));
            JasperPrint print = JasperFillManager.fillReport(compiledReport, parameters, itemDataSource);
            return JasperExportManager.exportReportToPdf(print);
        } catch (JRException exception) {
            throw new ReceiptGenerationException(exception);
        }
    }

    private Map<String, Object> parameters(OrderResponse order, UserResponse user) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ORDER_ID", order.id());
        parameters.put("ORDER_DATE", order.date() == null ? "" : DATE_FORMAT.format(order.date()));
        parameters.put("ORDER_STATUS", order.status());
        parameters.put("CUSTOMER_NAME", user.name());
        parameters.put("CUSTOMER_EMAIL", user.email());
        parameters.put("ADDRESS", order.address());
        parameters.put("CITY", order.city());
        parameters.put("PHONE", order.phone());
        parameters.put("SUBTOTAL", order.subTotal());
        parameters.put("TAX", order.tax());
        parameters.put("SHIPPING_FEE", order.shippingFee());
        parameters.put("TOTAL", order.total());
        parameters.put("CURRENCY", receiptProperties.currency());
        return parameters;
    }

    private Collection<Map<String, ?>> items(OrderResponse order) {
        List<OrderItemResponse> orderItems = order.items() == null ? List.of() : order.items();
        List<Map<String, ?>> rows = new ArrayList<>();
        for (OrderItemResponse item : orderItems) {
            Map<String, Object> row = new HashMap<>();
            row.put("productId", item.productId());
            row.put("quantity", item.quantity());
            row.put("price", item.price());
            row.put(
                    "lineTotal",
                    item.price().multiply(BigDecimal.valueOf(item.quantity().longValue())));
            rows.add(row);
        }
        return rows;
    }

    private static InputStream loadTemplate() {
        InputStream template = JasperReceiptGeneratorAdapter.class
                .getClassLoader()
                .getResourceAsStream(TEMPLATE_PATH);
        if (template == null) {
            throw new ReceiptGenerationException(new FileNotFoundException(TEMPLATE_PATH));
        }
        return template;
    }

    private static JasperReport compileTemplate(InputStream template) {
        try (InputStream input = template) {
            return JasperCompileManager.compileReport(input);
        } catch (JRException | java.io.IOException exception) {
            throw new ReceiptGenerationException(exception);
        }
    }
}
