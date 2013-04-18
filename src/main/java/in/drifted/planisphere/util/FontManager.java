package in.drifted.planisphere.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

public class FontManager {

    private static final String FONT_BASE_PATH = "/in/drifted/planisphere/resources/fonts/";
    private ResourceBundle resources;
    private String country;

    public FontManager(Locale locale) {
        resources = ResourceBundle.getBundle("in.drifted.planisphere.resources.fonts.mapping");
        country = locale.getCountry();
    }

    public String translate(String content) throws Exception {
        String[] chunksRaw = content.split("\\$\\{");
        if (chunksRaw.length <= 1) {
            return content;
        }
        ArrayList<String> chunks = new ArrayList<String>();
        for (int c = 0; c < chunksRaw.length; c++) {
            if (!chunksRaw[c].contains("}")) {
                if (chunksRaw[c].length() > 0) {
                    chunks.add(chunksRaw[c]);
                }
            } else {
                int index = chunksRaw[c].indexOf("}");
                String key = chunksRaw[c].substring(0, index);
                if (key.contains(".name")) {
                    chunks.add(getValue(key.replace(".name", "")).replace(".ttf", ""));
                } else {
                    chunks.add(getFontBase64Encoded(getValue(key)));
                }
                if (index != chunksRaw[c].length() - 1) {
                    chunks.add(chunksRaw[c].substring(index + 1));
                }
            }
        }
        StringBuilder contentUpdated = new StringBuilder();
        for (String item : chunks) {
            contentUpdated.append(item);
        }
        return contentUpdated.toString();
    }

    private String getValue(String key) {
        String keyLocal = key + "." + country.toLowerCase(Locale.ENGLISH);
        String keyDefault = key + ".default";
        if (resources.containsKey(keyLocal)) {
            return resources.getString(keyLocal);
        } else if (resources.containsKey(keyDefault)) {
            return resources.getString(keyDefault);
        } else {
            return key;
        }
    }

    private String getFontBase64Encoded(String fontFileName) throws Exception {
        String fontPath = FONT_BASE_PATH + fontFileName;
        String [] fileNameFragments = fontFileName.split("\\.");
        String fontFormat = fileNameFragments[fileNameFragments.length - 1];
        StringBuilder fontInfo = new StringBuilder();
        fontInfo.append("data:font/");
        fontInfo.append(fontFormat);
        fontInfo.append(";base64,");
        InputStream fontData = FontManager.class.getResourceAsStream(fontPath);
        fontInfo.append(new String(Base64.encodeBase64(IOUtils.toByteArray(fontData)), "ISO-8859-1"));
        return fontInfo.toString();
    }
}