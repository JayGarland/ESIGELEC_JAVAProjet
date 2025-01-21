package utils;

import authentication.Driver;
import data.Delivery;
import data.Mission;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.wp.usermodel.ParagraphAlignment;

public class ReportGenerator {

    public boolean generateWordReport(Date selectedDate, List<Delivery> deliveries) {
        XWPFDocument document = new XWPFDocument();
        try {
            // Create title paragraph
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titleParagraph.createRun();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = dateFormat.format(selectedDate);
            titleRun.setText(formattedDate);
            titleRun.setBold(true);
            titleRun.setFontSize(16);

            // Iterate through deliveries
            for (Delivery delivery : deliveries) {
                if (dateFormat.format(delivery.getDeliveryDate()).equals(formattedDate)) {
                    XWPFParagraph missionParagraph = document.createParagraph();
                    XWPFRun missionRun = missionParagraph.createRun();
                    missionRun.setBold(true);
                    missionRun.setText(
                            "Delivery ID: " + delivery.getId() + " - Customer ID: " + delivery.getCustomerId() + "\n");

                    if (delivery.getDriverId() > 0) {
                        Driver driver = DatabaseManager.getInstance()
                                .getUserById(delivery.getDriverId()) instanceof Driver
                                        ? (Driver) DatabaseManager.getInstance().getUserById(delivery.getDriverId())
                                        : null;
                        if (driver != null) {
                            XWPFRun deliveryRun = missionParagraph.createRun();
                            deliveryRun.setText(
                                    "    Driver: " +
                                            driver.getEmail()
                                            + "\n");
                            XWPFRun routeRun = missionParagraph.createRun();
                            routeRun.setText(
                                    "  Route: Warehouse -> " + delivery.getDeliveryAddress() + " -> Warehouse" + "\n");

                        }

                    } else {
                        XWPFRun deliveryRun = missionParagraph.createRun();
                        deliveryRun.setText("  No driver assigned yet\n");
                    }

                }

            }

            // Save document
            String fileName = "DeliveryReport_" + formattedDate + ".docx";
            FileOutputStream outputStream = new FileOutputStream(fileName);
            document.write(outputStream);
            outputStream.close();
            document.close();
            System.out.println("Word document generated successfully: " + fileName);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error generating Word document.");
            return false;
        }
    }
}