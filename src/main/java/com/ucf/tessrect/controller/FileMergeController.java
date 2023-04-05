package com.ucf.tessrect.controller;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Image;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

@Controller
public class FileMergeController {

    @PostMapping("/merge-files")
    public void mergeFiles(@RequestParam("files") List<MultipartFile> files, HttpServletResponse response) {

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            for (MultipartFile file : files) {
                if (!file.getContentType().startsWith("image/")) {
                    continue; // skip non-image files
                }

                // Read image and resize it to fit the page
                BufferedImage originalImage = ImageIO.read(file.getInputStream());
                float width = originalImage.getWidth();
                float height = originalImage.getHeight();
                float aspectRatio = width / height;
                Image image = Image.getInstance(file.getBytes());
                image.scaleToFit(document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin(),
                        (document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin()) / aspectRatio);

                document.add(image);
            }

            document.close();

            byte[] pdfBytes = outputStream.toByteArray();

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"merged-files.pdf\"");
            response.getOutputStream().write(pdfBytes);

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
