package main;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;


import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;


/**
 * Makes a gif from a sequence of snapshots
 */
public class GIFMaker {

    private static List<WritableImage> images;
    public static final int DEFAULT_TIME = 95000;
    private static Timer timer;

    private static final int DELAY_TIME = 60;

    private static ImageOutputStream out;

    private static int time = DEFAULT_TIME;


    static {
        images = new ArrayList<>();
    }

    /**
     * Sets time of a gif
     * @param input text input
     * @return corrected input
     */
    public static String setTime(String input) {

        if (input.equals("Default") || input.equals("default")) {
            time = DEFAULT_TIME;
            return input;
        }

        int oldTime = time / 1000;
        try {
            time = Integer.parseInt(input);
            if (time < 1)
                throw new IllegalArgumentException("Time value must be a positive number");

            if (time > 20)
                throw new IllegalArgumentException("Time must not exceed 20 seconds");


        } catch (NumberFormatException ex) {
            PopupMessage.showMessage("Invalid time value");
            return "Default";
        } catch (IllegalArgumentException ex) {
            PopupMessage.showMessage(ex.getMessage());
            return "Default";
        }
        if (oldTime != time)
            PopupMessage.showMessage("Time set to " + time + " sec");
        time *= 1000;
        return input;
    }

    /**
     * Creates a gif from a ready sequence
     */
    public static void createGif() {
        timer.cancel();

        if (images.isEmpty())
            return;

        specifyOutput();

        if (out == null)
            return;


        BufferedImage buf = SwingFXUtils.fromFXImage(images.get(0), null);
        ImageWriter writer = ImageIO.getImageWritersBySuffix("gif").next();
        ImageWriteParam param = writer.getDefaultWriteParam();

        ImageTypeSpecifier sp = ImageTypeSpecifier.createFromBufferedImageType(buf.getType());
        IIOMetadata metadata = writer.getDefaultImageMetadata(sp, param);

        try {
            specifyMetaData(metadata);
            writer.setOutput(out);
            writer.prepareWriteSequence(null);

            for (WritableImage image : images) {
                buf = SwingFXUtils.fromFXImage(image, null);
                writer.writeToSequence(new IIOImage(buf, null, metadata), param);
            }

            writer.endWriteSequence();
            out.close();
        } catch (IOException ex) {
            PopupMessage.showMessage("Failed to write the file");
            ex.printStackTrace();
        }

    }


    /**
     * Runs a thread which makes snapshots every delay_time millis
     */
    public static void takeSnapshots() {
        out = null;
        images.clear();
        timer = new Timer();
        long begin = System.currentTimeMillis();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (System.currentTimeMillis() > begin + time ||
                        !Visualizer.isRunning())
                    timer.cancel();
                Platform.runLater(() ->
                        images.add(Drawer.getInstance().takeSnap()));

            }
        }, 10, DELAY_TIME);

    }

    public static void stopTimer(){
        timer.cancel();
    }

    public static boolean isTimeDefault(){
        return time == DEFAULT_TIME;
    }

    /**
     * Specifies gif writer parameters
     * @param metadata metadata to edit
     * @throws IOException shit happens
     */
    private static void specifyMetaData(IIOMetadata metadata) throws IOException {

        String metaFormatName = metadata.getNativeMetadataFormatName();
        IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metaFormatName);

        IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");
        graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
        graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("delayTime", Integer.toString(DELAY_TIME / 10));
        graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0");

        IIOMetadataNode appExtensionsNode = getNode(root, "ApplicationExtensions");
        IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");

        child.setAttribute("applicationID", "NETSCAPE");
        child.setAttribute("authenticationCode", "2.0");

        child.setUserObject(new byte[]{0x1, (byte) 0, (byte) 0});
        appExtensionsNode.appendChild(child);
        metadata.setFromTree(metaFormatName, root);
    }


    private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
        int nNodes = rootNode.getLength();
        for (int i = 0; i < nNodes; i++) {
            if (rootNode.item(i).getNodeName().equalsIgnoreCase(nodeName)) {
                return (IIOMetadataNode) rootNode.item(i);
            }
        }
        IIOMetadataNode node = new IIOMetadataNode(nodeName);
        rootNode.appendChild(node);
        return (node);
    }


    /**
     * Gets a file where a gif will be saved
     */
    private static void specifyOutput() {
        PopupMessage.fixMessage("Please,wait...");
        Drawer.getInstance().enableDialog(true);
        File file = FileManager.getGifFile();

        if (file == null)
            return;


        try {
            out = new FileImageOutputStream(file);
        } catch (FileNotFoundException ex1) {
            PopupMessage.showMessage("File not found");
        } catch (IOException ex) {
            PopupMessage.showMessage("Failed to open the file");
        }

    }


}


