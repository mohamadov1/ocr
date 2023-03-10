package com.ucf.tessrect;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImagePreprocessing {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {

        // Charger l'image
        String imagePath = "C:\\Users\\Dell4\\Downloads\\image_67191809.JPG";
        //Mat image = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_GRAYSCALE);
        Mat image = Imgcodecs.imread(imagePath);

        // Appliquer le traitement d'image
       preprocessImage(image);

    }

    public static void preprocessImage(Mat image) {

        // Convertir l'image en niveaux de gris
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // Appliquer un filtre Gaussien pour éliminer le bruit
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(gray, blurred, new Size(5, 5), 0);

        // Appliquer la détection de contours pour redresser les caractères inclinés
        Mat edges = new Mat();
        Imgproc.Canny(blurred, edges, 100, 200);
        Mat lines = new Mat();
        Imgproc.HoughLinesP(edges, lines, 1, Math.PI/180, 100, 100, 10);
        double angle = 0.0;
        int count = 0;
        for (int i = 0; i < lines.rows(); i++) {
            double[] data = lines.get(i, 0);
            angle += Math.atan2(data[3] - data[1], data[2] - data[0]);
            count++;
        }
        if (count > 0) {
            angle /= count;
        }

        Mat resizedImage = new Mat();
        Size size = new Size(gray.width() * 2, gray.height() * 2); // Augmenter la taille de l'image par un facteur de 2
        Imgproc.resize(gray, resizedImage, size, 0, 0, Imgproc.INTER_LINEAR); // Redimensionner l'image en utilisant la méthode INTER_LINEAR

        // Appliquer un seuillage adaptatif pour binariser l'image
        Mat binary = new Mat();
        Imgproc.adaptiveThreshold(resizedImage, binary, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 10);

        // Appliquer une ouverture pour éliminer le bruit
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Mat opened = new Mat();
        Imgproc.morphologyEx(binary, opened, Imgproc.MORPH_OPEN, kernel);

        // Enregistrer l'image résultante
        Imgcodecs.imwrite("image.jpg", opened);
    }
}
