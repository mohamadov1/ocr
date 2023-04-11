package com.ucf.tessrect.controller;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PDFToImagesConverter {

    public static void main(String[] args) {
        String pdfFilePath = "C:\\Users\\Dell4\\Documents\\pdf\\Carte de séjour - Belgique - version scannée.pdf";
        String outputFolderPath = "C:\\Users\\Dell4\\IdeaProjects\\poc-java-tesseract\\src\\main\\resources\\images";

        try {
            PDDocument document = PDDocument.load(new File(pdfFilePath));
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
                BufferedImage image = pdfRenderer.renderImageWithDPI(pageIndex, 300); // Résolution en DPI

                // Enregistrement de l'image en tant que fichier PNG
                String imagePath = outputFolderPath + "image_" + (pageIndex + 1) + ".png";
                ImageIO.write(image, "PNG", new File(imagePath));

                System.out.println("Page " + (pageIndex + 1) + " convertie en " + imagePath);
            }

            document.close();
            System.out.println("Conversion terminée !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
