package fr.proline.studio.utils;

import static fr.proline.studio.utils.GlobalValues.PUBLIC_RELEASE_VERSION;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;

/**
 *
 * @author JM235353
 */
public class MiscellaneousUtils {
    
    /**
     * Transform URL of type
     * "http://biodev.extra.cea.fr/docs/proline/doku.php?id=how_to:studio:rsvalidation"
     * to
     * "http://www.profiproteomics.fr/doc/1.0/how_to/studio/rsvalidation.html"
     * @param helpURL
     * @return 
     */
    public static String convertURLToCurrentHelp(String helpURL) {
        if (helpURL == null) {
            return null;
        }
        final String START_URL = "http://biodev.extra.cea.fr/docs/proline/doku.php";
        final String START_URL_WITH_PARAMETER = "http://biodev.extra.cea.fr/docs/proline/doku.php?id=";
        final String NEW_URL = "http://proline.profiproteomics.fr/doc/1.3/";
        if (PUBLIC_RELEASE_VERSION && helpURL.startsWith(START_URL)) {
            if (helpURL.startsWith(START_URL_WITH_PARAMETER)) {
                helpURL = NEW_URL + helpURL.substring(START_URL_WITH_PARAMETER.length()).replaceAll(" ", "_").replaceAll(":", "/") + ".html";
            } else {
                helpURL = NEW_URL + "start.html";
            }
        }

        return helpURL;
    }

    public static String getFileName(String path, String[] suffix) {

        path = path.toLowerCase();

        if (path.contains("/")) {
            path = path.substring(path.lastIndexOf("/") + 1);
        }
        if (path.contains("\\")) {
            path = path.substring(path.lastIndexOf("\\") + 1);
        }
        for (int i = 0; i < suffix.length; i++) {
            if (path.contains(suffix[i].toLowerCase())) {
                path = path.substring(0, path.indexOf(suffix[i].toLowerCase()));
            }
        }

        return path;
    }

    public static String createRedirectPage(String url) {
        return "<html><head>"
                +"<meta http-equiv=\"refresh\" content=\"0;url="+url+"\" />"
                +"</head></html>";
    }

    public static URI createRedirectTempFile(String documentationSuffix) {
        BufferedWriter writer = null;
        File tmpFile = null;
        try {
            // creates temporary file
            tmpFile = File.createTempFile("redirect", ".html", null);
            // deletes file when the virtual machine terminate
            tmpFile.deleteOnExit();
            // writes redirect page content to file 
            writer = new BufferedWriter(new FileWriter(tmpFile));
            writer.write(createRedirectPage(new File(".").getCanonicalPath() + File.separatorChar + "documentation" + File.separatorChar + "Proline_UserGuide_1.4RC1.docx.html#"+documentationSuffix));
            writer.close();
        } catch (IOException e) {
            return null;
        }
        return tmpFile.toURI();
    }
}
