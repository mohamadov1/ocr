package com.ucf.tessrect;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class PassportTextExtraction {

    public static void main(String[] args) {

        // Charger l'image
        String imageFile = "chemin/vers/mon_image.png";
        Mat image = Imgcodecs.imread(imageFile);

        // Convertir l'image en niveaux de gris
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // Appliquer un seuillage adaptatif pour convertir l'image en noir et blanc
        Mat thresh = new Mat();
        Imgproc.adaptiveThreshold(gray, thresh, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 11, 10);



        // Appliquer une dilatation pour épaissir les contours du texte
        Mat dilated = new Mat();
        Imgproc.dilate(thresh, dilated, new Mat(), new Point(-1, -1), 3);

        // Appliquer une érosion pour réduire la taille des caractères et éliminer les bruits
        Mat eroded = new Mat();
        Imgproc.erode(dilated, eroded, new Mat(), new Point(-1, -1), 3);


        //System.out.println(text);
    }
}
