package mts.teta.resizer.imageprocessor;

import marvin.image.MarvinImage;
import marvin.io.MarvinImageIO;
import marvinplugins.MarvinPluginCollection;

import mts.teta.resizer.ResizerApp;
import net.coobird.thumbnailator.Thumbnails;

import java.awt.image.BufferedImage;
import java.io.IOException;


public class ImageProcessor {
    public void processImage(BufferedImage read, ResizerApp resizerApp) throws IOException, BadAttributesException {
        checkUsersParams(resizerApp, read);
        MarvinImage image = MarvinImageIO.loadImage(resizerApp.getInputFile().getAbsolutePath());
        if (resizerApp.isCropCheckBox()) {
            MarvinPluginCollection.crop(image.clone(), image, resizerApp.getCropX(), resizerApp.getCropY(), resizerApp.getCropWidth(), resizerApp.getCropHeight());
        }
        if (resizerApp.isBlurCheckBox()) {
            MarvinPluginCollection.gaussianBlur(image.clone(), image, resizerApp.getBlurRadius());
        }
        if (resizerApp.isResizeCheckBox() || resizerApp.isOutputFormatCheckbox() || resizerApp.isQualityCheckBox()) {
            read = image.getNewImageInstance();
            processByThumbnailator(read, resizerApp);
        } else
            MarvinImageIO.saveImage(image, resizerApp.getOutputFile().getAbsolutePath());
    }

    private void processByThumbnailator(BufferedImage read, ResizerApp resizerApp) throws IOException {
        int width;
        int height;
        if (resizerApp.isResizeCheckBox()) {
            width = resizerApp.getResizeWidth();
            height = resizerApp.getResizeHeight();
        } else {
            width = read.getWidth();
            height = read.getHeight();
        }

        if (resizerApp.isOutputFormatCheckbox() && resizerApp.isQualityCheckBox()) {
            Thumbnails.of(read).forceSize(width, height).outputFormat(resizerApp.getOutputFormat()).outputQuality(resizerApp.getDoubleHundredthOfNumberQuality()).toFile(resizerApp.getOutputFile());
        } else if (resizerApp.isOutputFormatCheckbox()) {
            Thumbnails.of(read).forceSize(width, height).outputFormat(resizerApp.getOutputFormat()).toFile(resizerApp.getOutputFile());
        } else if (resizerApp.isQualityCheckBox()) {
            Thumbnails.of(read).forceSize(width, height).outputQuality(resizerApp.getDoubleHundredthOfNumberQuality()).toFile(resizerApp.getOutputFile());
        } else
            Thumbnails.of(read).forceSize(width, height).toFile(resizerApp.getOutputFile());
    }

    private void checkUsersParams(ResizerApp resizerApp, BufferedImage read) throws BadAttributesException {
        if (resizerApp.isOutputFormatCheckbox() && !(resizerApp.getOutputFormat().equals("jpeg") || resizerApp.getOutputFormat().equals("png")))
            throw new BadAttributesException("Please check params!");
        if (resizerApp.isQualityCheckBox() && !(resizerApp.getQuality() >= 1 && resizerApp.getQuality() <= 100))
            throw new BadAttributesException("Please check params!");
        if (resizerApp.isResizeCheckBox() && !(resizerApp.getResizeHeight() > 0 && resizerApp.getResizeWidth() > 0))
            throw new BadAttributesException("Please check Params!");
        if (resizerApp.isBlurCheckBox() && !(resizerApp.getBlurRadius() > 0))
            throw new BadAttributesException("Please check Params!");
        if (resizerApp.isCropCheckBox() && !(resizerApp.getCropWidth() < read.getWidth() && resizerApp.getCropHeight() < read.getHeight() && resizerApp.getCropX() > 0 && resizerApp.getCropY() > 0 && resizerApp.getCropWidth() >= 0) && resizerApp.getCropHeight() >= 0)
            throw new BadAttributesException("Please check Params!");
    }
}
