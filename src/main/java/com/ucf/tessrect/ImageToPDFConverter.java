package com.ucf.tessrect;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageToPDFConverter {

    public static void main(String[] args) {
        String inputFolder = "C:\\Users\\Dell4\\IdeaProjects\\poc-java-tesseract\\src\\main\\resources\\images\\";
        String outputFile = "C:\\Users\\Dell4\\IdeaProjects\\poc-java-tesseract\\src\\main\\resources\\images\\merged.pdf";

        try {
            List<Image> images = getImages(inputFolder);
            createPDF(images, outputFile);
            System.out.println("PDF file created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Image> getImages(String inputFolder) throws Exception {
        List<Image> images = new ArrayList<>();
        File folder = new File(inputFolder);
        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (file.isFile() && isSupportedImageFormat(file.getName())) {
                    Image image = Image.getInstance(file.getAbsolutePath());
                    // resize the image to fit on the PDF page
                    image.scaleToFit(PageSize.A6.getWidth(), PageSize.A6.getHeight());
                    images.add(image);
                }
            }
        }
        return images;
    }

    private static boolean isSupportedImageFormat(String fileName) {
        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")
                || fileName.endsWith(".png") || fileName.endsWith(".bmp")
                || fileName.endsWith(".gif");
    }

    private static void createPDF(List<Image> images, String outputFile) throws Exception {
        Document document = new Document(images.get(0));
        PdfWriter.getInstance(document, new FileOutputStream(outputFile));
        document.open();
        for (Image image : images) {
            document.add(image);
        }
        document.close();
    }
}
