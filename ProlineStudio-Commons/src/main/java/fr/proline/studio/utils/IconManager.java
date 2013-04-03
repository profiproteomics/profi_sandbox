package fr.proline.studio.utils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;

/**
 * Manage all icons needed by the application. Each icon is loaded only one time
 * when it is first needed
 *
 * Each icon can be loaded greyed with an hourglass on it
 *
 * @author JM235353
 */
public class IconManager {

    public enum IconType {

        OK,
        CANCEL,
        DEFAULT,
        SAME_SET,
        SUB_SET,
        ADD_REMOVE,
        HOUR_GLASS,
        HOUR_GLASS_MINI,
        GEL,
        RSET,
        RSM,
        VIAL,
        PROJECT,
        USER,
        MAXIMIZE,
        MINIMIZE,
        ASSOCIATE,
        DISSOCIATE,
        DELETE,
        QUESTION,
        ERASER,
        OPEN_FILE,
        EXCLAMATION,
        INFORMATION,
        EMPTY,
        PLUS,
        CROSS,
        DOCUMENT_LIST,
        WEB_LINK
    }
    private static HashMap<IconType, ImageIcon> iconMap = new HashMap<>();
    private static HashMap<IconType, ImageIcon> iconHourGlassMap = new HashMap<>();

    public static ImageIcon getIcon(IconType iconType) {

        ImageIcon icon = iconMap.get(iconType);
        if (icon == null) {
            String path = getIconFilePath(iconType);
            icon = ImageUtilities.loadImageIcon(path, false);
            iconMap.put(iconType, icon);
        }

        return icon;
    }

    public static ImageIcon getIconWithHourGlass(IconType iconType) {

        ImageIcon iconWithHourGlass = iconHourGlassMap.get(iconType);
        if (iconWithHourGlass == null) {
            String path = getIconFilePath(iconType);
            Image imSource = ImageUtilities.loadImage(path, false);
            int sourceWidth = imSource.getWidth(null);
            int sourceHeight = imSource.getHeight(null);

            BufferedImage im = new BufferedImage(sourceWidth, sourceHeight, BufferedImage.TYPE_INT_ARGB);

            im.getGraphics().drawImage(imSource, 0, 0, null);

            // grey out the image
            for (int x = 0; x < sourceWidth; x++) {
                for (int y = 0; y < sourceHeight; y++) {
                    int color = im.getRGB(x, y);
                    int alpha = (color & 0xFF000000);
                    int red = (color & 0x00FF0000) >> 16;
                    int green = (color & 0x0000FF00) >> 8;
                    int blue = color & 0x000000FF;
                    int greyLevel = (int) Math.round(((double) (red + green + blue)) / 3.0);
                    // lighter grey
                    greyLevel  += (int) 100;
                    if (greyLevel>255) greyLevel = 255;
                    int grey = alpha + (greyLevel << 16) + (greyLevel << 8) + greyLevel;
                    im.setRGB(x, y, grey);
                }
            }

            if (miniHourGlassImage == null) {
                miniHourGlassImage = ImageUtilities.loadImage(getIconFilePath(IconType.HOUR_GLASS_MINI), false);
            }

            // add an hour glass at the right bottom corner
            im.getGraphics().drawImage(miniHourGlassImage, sourceWidth - miniHourGlassImage.getWidth(null), sourceHeight - miniHourGlassImage.getHeight(null), null);

            iconWithHourGlass = new ImageIcon(im);
            iconHourGlassMap.put(iconType, iconWithHourGlass);
        }

        return iconWithHourGlass;

    }
    private static Image miniHourGlassImage = null;

    private static String getIconFilePath(IconType iconType) {
        switch (iconType) {
            case OK:
                return "fr/proline/studio/images/tick.png";
            case CANCEL:
                return "fr/proline/studio/images/tick-red.png";
            case DEFAULT:
                return "fr/proline/studio/images/arrow-circle.png";
            case SAME_SET:
                return "fr/proline/studio/images/sameset.png";
            case SUB_SET:
                return "fr/proline/studio/images/subset.png";
            case ADD_REMOVE:
                return "fr/proline/studio/images/addremove.png";
            case HOUR_GLASS:
                return "fr/proline/studio/images/hourGlass.png";
            case HOUR_GLASS_MINI:
                return "fr/proline/studio/images/hourGlassMini.png";
            case GEL:
                return "fr/proline/studio/images/gel.png";
            case RSET:
                return "fr/proline/studio/images/resultSet.png";
            case RSM:
                return "fr/proline/studio/images/resultSummary.png";
            case VIAL:
                return "fr/proline/studio/images/vial.png";
            case PROJECT:
                return "fr/proline/studio/images/project.png";
            case USER:
                return "fr/proline/studio/images/user.png";
            case MAXIMIZE:
                return "fr/proline/studio/images/maximize.png";
            case MINIMIZE:
                return "fr/proline/studio/images/minimize.png";
            case ASSOCIATE:
                return "fr/proline/studio/images/associate.png";
            case DISSOCIATE:
                return "fr/proline/studio/images/dissociate.png";
            case DELETE:
                return "fr/proline/studio/images/deleteBlue.png";
            case QUESTION:
                return "fr/proline/studio/images/question.png";
            case ERASER:
                return "fr/proline/studio/images/eraser.png";
            case OPEN_FILE:
                return "fr/proline/studio/images/folder-open-document.png";
            case EXCLAMATION:
                return "fr/proline/studio/images/exclamation-red.png";
            case INFORMATION:
                return "fr/proline/studio/images/information.png";
            case EMPTY:
                return "fr/proline/studio/images/empty.png";
            case PLUS:
                return "fr/proline/studio/images/plus-small7x7.png";
            case CROSS:
                return "fr/proline/studio/images/cross-small7x7.png";
            case DOCUMENT_LIST:
                return "fr/proline/studio/images/document-list.png";
            case WEB_LINK:
                return "fr/proline/studio/images/weblink.png";
        }


        return null; // can not happen
    }
}
