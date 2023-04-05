package com.ucf.tessrect.controller;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Image;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ImagesMergeController {

    @PostMapping(value = "/merge-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void mergeFiles(@RequestPart("files") List<MultipartFile> files, HttpServletResponse response) {

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            List<Image> images = files.stream()
                    .filter(file -> file.getContentType().startsWith("image/"))
                    .map(file -> {
                        try {
                            // Read image and resize it to fit the page
                            BufferedImage originalImage = ImageIO.read(file.getInputStream());
                            float aspectRatio = (float) originalImage.getWidth() / originalImage.getHeight();
                            Image image = Image.getInstance(file.getBytes());
                            image.scaleToFit(document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin(),
                                    (document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin()) / aspectRatio);
                            return image;
                        } catch (IOException | DocumentException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());

            for (Image image : images) {
                document.add(image);
            }

            document.close();

            byte[] pdfBytes = outputStream.toByteArray();

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"merged-files.pdf\"");
            response.getOutputStream().write(pdfBytes);

        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
