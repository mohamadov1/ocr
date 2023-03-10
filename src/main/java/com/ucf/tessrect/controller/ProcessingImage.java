package com.ucf.tessrect.controller;

import com.ucf.tessrect.MrzParser;
import com.ucf.tessrect.MrzRecord;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProcessingImage {

    public static MrzRecord processImg(BufferedImage ipimage) throws IOException, TesseractException {
        // 1
        Tesseract1 it = new Tesseract1();
        it.setDatapath("C:\\OCR\\tessdata");
        it.setLanguage("eng");
        String str = it.doOCR(ipimage);


        String[] lines = str.split("\n");
        boolean pass = false;
        List<String> mrzBuilder = new ArrayList<>();
        for (String line : lines) {
            line = line.replaceAll(" ", "").replaceAll("~", "").replaceAll("[^a-zA-Z0-9 <]", "");
            if (line.startsWith("P<") || pass) {
                String lineOne = line;
                if (lineOne.length() == 44) mrzBuilder.add(lineOne);
                else {
                    mrzBuilder.add(ajusterLongueur(lineOne));
                }
                pass = true;
            }
        }

        if (mrzBuilder.size() < 2) {
            for (String line : lines) {
                line = line.replaceAll(" ", "").replaceAll("~", "").replaceAll("[^a-zA-Z0-9 <]", "");
                if (line.startsWith("PK") || pass) {
                    String lineOne = line.replace("PK", "P<");
                    if (lineOne.length() == 44) mrzBuilder.add(lineOne);
                    else {
                        mrzBuilder.add(ajusterLongueur(lineOne));
                    }
                    pass = true;
                }
            }
        }
        if (mrzBuilder.size() < 2) {
            for (String line : lines) {
                line = line.replaceAll(" ", "").replaceAll("~", "").replaceAll("[^a-zA-Z0-9 <]", "");
                if (line.contains("P<") || pass) {
                    String lineOne = removeTextBeforeKeyword(line);
                    if (lineOne.length() == 44) mrzBuilder.add(lineOne);
                    else {
                        mrzBuilder.add(ajusterLongueur(lineOne));
                    }
                    pass = true;
                }
            }
        }
        if (mrzBuilder.size() < 2) {
            int ligne = 0;
            for (String line : lines) {
                line = line.replaceAll(" ", "").replaceAll("~", "").replaceAll("[^a-zA-Z0-9 <]", "");
                if (ligne == lines.length - 1 || ligne == lines.length - 2) {
                    if (line.startsWith("P") || pass) {
                        String lineOne = line.replace("P", "P<");
                        if (lineOne.length() == 44) mrzBuilder.add(lineOne);
                        else {
                            mrzBuilder.add(ajusterLongueur(lineOne));
                        }
                        pass = true;
                    }
                }
                ligne++;
            }
        }
        if (mrzBuilder.size() < 2) {
            return MrzParser.parse("");
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (mrzBuilder.size() == 3) {
            if (mrzBuilder.get(1).length() < 10) {
                stringBuilder.append(mrzBuilder.get(0));
                stringBuilder.append("\n");
                stringBuilder.append(mrzBuilder.get(2));
            } else {
                stringBuilder.append(mrzBuilder.get(0));
                stringBuilder.append("\n");
                stringBuilder.append(mrzBuilder.get(1));
            }


        } else {
            stringBuilder.append(mrzBuilder.get(0));
            stringBuilder.append("\n");
            stringBuilder.append(mrzBuilder.get(1));
        }


        String cleanText = replaceLowercaseChars(stringBuilder.toString());
        cleanText = cleanText.replaceAll("LLL", "<<<");
        cleanText = cleanText.replaceAll("LL<", "<<<");
        cleanText = cleanText.replaceAll("LL<", "<<<");
        cleanText = cleanText.replaceAll("<L<", "<<<");
        cleanText = cleanText.replaceAll("<LL<", "<<<");
        cleanText = cleanText.replaceAll("<LL", "<<<").replaceAll("<KLL", "<<<<").replaceAll("<K<K<KLK", "");
        System.out.println(cleanText);
        final MrzRecord record = MrzParser.parse(cleanText);
        return record;
    }

    public static String removeTextBeforeKeyword(String input) {
        int index = input.indexOf("P<");
        if (index >= 0) {
            return input.substring(index);
        } else {
            return input;
        }
    }

    public static String replaceLowercaseChars(String inputString) {
        String outputString = "";
        for (int i = 0; i < inputString.length(); i++) {
            char c = inputString.charAt(i);
            if (Character.isLowerCase(c)) {
                outputString += "<";
            } else {
                outputString += c;
            }
        }
        return outputString;
    }

    public static String ajusterLongueur(String chaine) {
        int longueurMax = 44;
        int longueurActuelle = chaine.length();

        if (longueurActuelle > longueurMax) {
            chaine = chaine.substring(0, longueurMax);
        } else {
            int difference = longueurMax - longueurActuelle;
            for (int i = 0; i < difference; i++) {
                chaine += "<";
            }
        }

        return chaine;
    }


    public static File convertToBlackAndWhite(File file, String newFileName) {
        try {
            final BufferedImage colorImage = ImageIO.read(file);
            Graphics2D g = colorImage.createGraphics();
            g.drawImage(colorImage, null, 0, 0);
            final BufferedImage grayImage = new BufferedImage(colorImage.getWidth(), colorImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            g = grayImage.createGraphics();
            g.drawImage(colorImage, 0, 0, null);
            g.dispose();
            File output = new File("C:/Users/Dell4/" + newFileName + ".png");
            ImageIO.write(grayImage, "png", output);
            return output;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
