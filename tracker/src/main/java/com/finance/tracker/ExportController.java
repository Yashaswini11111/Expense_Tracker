package com.finance.tracker;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


@RestController
@RequestMapping("/api/export")
@CrossOrigin(origins = "*")
public class ExportController {

    @Autowired 
    private TransactionRepository repo;

  
    @GetMapping("/pdf/{userId}")
    public void exportToPDF(@PathVariable Long userId, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=Financial_Report.pdf");

        List<Transaction> list = repo.findByUserIdOrderByDateDesc(userId);
        
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
        Paragraph title = new Paragraph("Transaction History Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10);

        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
        String[] headers = {"Date", "Category", "Type", "Amount (INR)"};

        for (String headerTitle : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(headerTitle, headFont));
            headerCell.setBackgroundColor(new Color(38, 70, 83)); 
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPadding(8);
            table.addCell(headerCell);
        }

        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
        for (Transaction t : list) {
            PdfPCell cell;
            
            table.addCell(new Phrase(t.getDate().toString(), dataFont));
            table.addCell(new Phrase(t.getCategory(), dataFont));
            
            Phrase typePhrase = new Phrase(t.getType(), dataFont);
            table.addCell(typePhrase);

            cell = new PdfPCell(new Phrase("₹" + String.format("%.2f", t.getAmount()), dataFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
        }

        document.add(table);
        document.close();
    }

    
    @GetMapping("/csv/{userId}")
    public void exportToCSV(@PathVariable Long userId, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=Finance_Spreadsheet.csv");
        
        List<Transaction> list = repo.findByUserIdOrderByDateDesc(userId);
        
        PrintWriter writer = response.getWriter();
        writer.write('\ufeff'); 
        
        writer.println("Date,Description,Category,Type,Amount (INR)");

        for (Transaction t : list) {
            String desc = (t.getDescription() != null) ? "\"" + t.getDescription().replace("\"", "\"\"") + "\"" : "";
            
            writer.printf("%s,%s,%s,%s,%.2f%n", 
                t.getDate(), 
                desc, 
                t.getCategory(), 
                t.getType(), 
                t.getAmount()
            );
        }
    }
}