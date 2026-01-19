package com.service_booking_backend.service;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.service_booking_backend.entity.Invoice;
import com.service_booking_backend.entity.Order;
import com.service_booking_backend.entity.OrderItem;
import com.service_booking_backend.repository.InvoiceRepository;
import com.service_booking_backend.repository.OrderItemRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepo;

    @Autowired
    private OrderItemRepository orderItemRepo;

    public Invoice generateInvoice(Order order, String paymentId) {

        try {
            Invoice inv = new Invoice();
            inv.setInvoiceNumber("INV-" + System.currentTimeMillis());
            inv.setOrderId(order.getId());
            inv.setUserId(order.getUser().getId());
            inv.setSubtotal(order.getTotalAmount());
            inv.setGst(order.getTotalAmount() * 0.18);
            inv.setTotal(order.getTotalAmount() * 1.18);
            inv.setRazorpayPaymentId(paymentId);
            inv.setCreatedAt(LocalDateTime.now());
            inv.setPublicToken(UUID.randomUUID().toString());

            File folder = new File(System.getProperty("user.home"), "service-booking-invoices");
            folder.mkdirs();

            File pdfFile = new File(folder, inv.getInvoiceNumber() + ".pdf");

            createPdf(inv, order, pdfFile);

            inv.setPdfPath(pdfFile.getAbsolutePath());
            return invoiceRepo.save(inv);

        } catch (Exception e) {
            throw new RuntimeException("Invoice generation failed", e);
        }
    }

    private void createPdf(Invoice inv, Order order, File file) throws Exception {

        Document doc = new Document(PageSize.A4, 36, 36, 80, 36);
        PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();

        Font title = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.WHITE);
        Font header = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        Font normal = FontFactory.getFont(FontFactory.HELVETICA, 11);

        /* ===== TOP BRAND BAR ===== */
        PdfPTable top = new PdfPTable(2);
        top.setWidthPercentage(100);
        top.setWidths(new float[]{70, 30});

        PdfPCell brandCell = new PdfPCell();
        brandCell.setBorder(Rectangle.NO_BORDER);
        brandCell.setBackgroundColor(new Color(74,108,247));
        brandCell.setPadding(15);

        try {
            Image logo = Image.getInstance(
                getClass().getClassLoader().getResource("static/logo.png")
            );
            logo.scaleToFit(120, 40);
            brandCell.addElement(logo);
        } catch (Exception e) {
            brandCell.addElement(new Paragraph("SERVICE BOOKING", title));
        }

        PdfPCell invoiceCell = new PdfPCell();
        invoiceCell.setBorder(Rectangle.NO_BORDER);
        invoiceCell.setBackgroundColor(new Color(74,108,247));
        invoiceCell.setPadding(15);
        invoiceCell.addElement(new Paragraph("INVOICE", title));

        top.addCell(brandCell);
        top.addCell(invoiceCell);
        doc.add(top);

        doc.add(new Paragraph(" "));

        /* ===== CUSTOMER & ORDER INFO ===== */
        PdfPTable info = new PdfPTable(2);
        info.setWidthPercentage(100);
        info.setSpacingBefore(10);

        info.addCell(cell("Invoice No", header));
        info.addCell(cell(inv.getInvoiceNumber(), normal));

        info.addCell(cell("Date", header));
        info.addCell(cell(inv.getCreatedAt().toLocalDate().toString(), normal));

        info.addCell(cell("Customer", header));
        info.addCell(cell(order.getUser().getName(), normal));

        info.addCell(cell("Email", header));
        info.addCell(cell(order.getUser().getEmail(), normal));

        info.addCell(cell("Payment ID", header));
        info.addCell(cell(inv.getRazorpayPaymentId(), normal));

        doc.add(info);
        doc.add(new Paragraph(" "));

        /* ===== SERVICE TABLE ===== */
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4, 1, 2, 2});
        table.setSpacingBefore(10);

        headerCell(table, "Service");
        headerCell(table, "Qty");
        headerCell(table, "Price");
        headerCell(table, "Total");

        List<OrderItem> items = orderItemRepo.findByOrder_Id(order.getId());

        for (OrderItem i : items) {
            table.addCell(data(i.getServiceName()));
            table.addCell(data(String.valueOf(i.getQty())));
            table.addCell(data("₹ " + i.getPrice()));
            table.addCell(data("₹ " + (i.getQty() * i.getPrice())));
        }

        table.addCell(empty());
        table.addCell(empty());
        table.addCell(data("GST (18%)"));
        table.addCell(data("₹ " + inv.getGst()));

        table.addCell(empty());
        table.addCell(empty());
        table.addCell(bold("Total"));
        table.addCell(bold("₹ " + inv.getTotal()));

        doc.add(table);

        Paragraph thanks = new Paragraph("Thank you for choosing Service Booking!");
        thanks.setAlignment(Element.ALIGN_CENTER);
        thanks.setSpacingBefore(20);
        doc.add(thanks);

        doc.close();
    }

    /* ===== STYLE HELPERS ===== */

    private PdfPCell cell(String text, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setPadding(8);
        return c;
    }

    private void headerCell(PdfPTable t, String text) {
        PdfPCell c = new PdfPCell(new Phrase(text));
        c.setBackgroundColor(new Color(230, 236, 255));
        c.setPadding(8);
        t.addCell(c);
    }

    private PdfPCell data(String text) {
        PdfPCell c = new PdfPCell(new Phrase(text));
        c.setPadding(8);
        return c;
    }

    private PdfPCell bold(String text) {
        PdfPCell c = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        c.setPadding(8);
        return c;
    }

    private PdfPCell empty() {
        PdfPCell c = new PdfPCell(new Phrase(""));
        c.setBorder(Rectangle.NO_BORDER);
        return c;
    }
}
