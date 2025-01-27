package utils;

import authentication.Driver;
import authentication.User;
import data.Delivery;
import data.DatabaseManager;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ReportGenerator {

    public boolean generateWordReport(Date selectedDate, List<Delivery> deliveries) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = dateFormat.format(selectedDate);
            String fileName = "DeliveryReport_" + formattedDate + ".docx";

            // Create the XML content for the document
            String documentXml = generateDocumentXml(formattedDate, deliveries);

            // Create a zip archive containing the necessary files for a .docx
            byte[] docxBytes = createDocxZip(documentXml);

            // Write the zip archive to a file
            FileOutputStream outputStream = new FileOutputStream(fileName);
            outputStream.write(docxBytes);
            outputStream.close();

            System.out.println("Word document generated successfully: " + fileName);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error generating Word document.");
            return false;
        }
    }

    private String generateDocumentXml(String formattedDate, List<Delivery> deliveries) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
        xml.append("<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">\n");
        xml.append("  <w:body>\n");

        // Title Paragraph
        xml.append(
                "    <w:p><w:pPr><w:jc w:val=\"center\"/></w:pPr><w:r><w:rPr><w:b/><w:sz w:val=\"32\"/></w:rPr><w:t>")
                .append(formattedDate).append("</w:t></w:r></w:p>\n");

        for (Delivery delivery : deliveries) {
            if (dateFormat.format(delivery.getDeliveryDate()).equals(formattedDate)) {
                xml.append("    <w:p>\n");
                xml.append("    <w:r><w:rPr><w:b/></w:rPr><w:t>Delivery ID: ").append(delivery.getId())
                        .append(" - Customer ID: ").append(delivery.getCustomerId()).append("</w:t></w:r>\n");

                if (delivery.getDriverId() > 0) {
                    User user = DatabaseManager.getInstance().getUserById(delivery.getDriverId());
                    if (user instanceof Driver) {
                        Driver driver = (Driver) user;
                        xml.append("    <w:r><w:t>Driver: ").append(driver.getEmail()).append("</w:t></w:r>\n");
                        xml.append("    <w:r><w:t>Route: Warehouse -> ").append(delivery.getDeliveryAddress())
                                .append(" -> Warehouse").append("</w:t></w:r>\n");
                    }

                } else {
                    xml.append("    <w:r><w:t>No driver assigned yet</w:t></w:r>\n");
                }
                xml.append("    </w:p>\n");
            }
        }

        xml.append("  </w:body>\n");
        xml.append("</w:document>\n");

        return xml.toString();
    }

    private byte[] createDocxZip(String documentXml) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteStream)) {

            zipOutputStream.putNextEntry(new ZipEntry("word/document.xml"));
            zipOutputStream.write(documentXml.getBytes());
            zipOutputStream.closeEntry();

            zipOutputStream.putNextEntry(new ZipEntry("[Content_Types].xml"));
            zipOutputStream.write(getContentTypesXml().getBytes());
            zipOutputStream.closeEntry();

            zipOutputStream.putNextEntry(new ZipEntry("_rels/.rels"));
            zipOutputStream.write(getRelXml().getBytes());
            zipOutputStream.closeEntry();

            zipOutputStream.putNextEntry(new ZipEntry("word/_rels/document.xml.rels"));
            zipOutputStream.write(getDocumentRelsXml().getBytes());
            zipOutputStream.closeEntry();

        }
        return byteStream.toByteArray();
    }

    private String getContentTypesXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">\n" +
                "  <Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>\n"
                +
                "  <Default Extension=\"xml\" ContentType=\"application/xml\"/>\n" +
                "  <Override PartName=\"/word/document.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml\"/>\n"
                +
                "</Types>";
    }

    private String getRelXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n" +
                "  <Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"word/document.xml\"/>\n"
                +
                "</Relationships>";
    }

    private String getDocumentRelsXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n" +
                "</Relationships>";
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
}