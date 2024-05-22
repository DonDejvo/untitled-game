package com.webler.untitledgame.level.levelmap;

import com.webler.untitledgame.level.exceptions.LevelMapFormatException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class LevelMapTest {

    @Test
    public void testLoadMethod_validLevel() {
        try {
            LevelMap levelMap = new LevelMap();

            URL url = ClassLoader.getSystemResource("valid_level.xml");
            File file = new File(url.toURI());
            levelMap.load(file.getAbsolutePath());

            LevelMap expected = new LevelMap();
            expected.setCeiling(10);
            expected.addPlatform(new Platform(1, 2, 3, 4, 0, 5));

            assertEquals(expected.getCeiling(), levelMap.getCeiling());
            assertEquals(expected.getMinX(), levelMap.getMinX());
            assertEquals(expected.getMinY(), levelMap.getMinY());
            assertEquals(expected.getMaxX(), levelMap.getMaxX());
            assertEquals(expected.getMaxY(), levelMap.getMaxY());
            assertEquals(expected.getPlatforms().size(), levelMap.getPlatforms().size());


        } catch (Exception e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = { "invalid_level_incorrect_type.xml", "invalid_level_missing_value.xml" })
    public void testLoadMethod_invalidLevel(String resourceName) {
        try {
            LevelMap levelMap = new LevelMap();

            URL url = ClassLoader.getSystemResource(resourceName);
            File file = new File(url.toURI());

            Exception exception = assertThrows(LevelMapFormatException.class, () -> {
                levelMap.load(file.getAbsolutePath());
            });

            String expectedMessage = "Level map file format error: " + file.getAbsolutePath();
            String actualMessage = exception.getMessage();

            assertEquals(expectedMessage, actualMessage);

        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void saveLevel() {
        try {
            File tempFile = File.createTempFile("level", ".xml");
            tempFile.deleteOnExit();

            LevelMap levelMap = new LevelMap();
            levelMap.setCeiling(10);
            levelMap.addPlatform(new Platform(1, 2, 3, 4, 0, 5));

            levelMap.save(tempFile.getAbsolutePath());

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(tempFile);
            doc.getDocumentElement().normalize();

            Node levelNode = doc.getElementsByTagName(LevelMap.TAG).item(0);

            assertNotNull(levelNode);

            Element levelElement = (Element) levelNode;

            assertEquals(levelMap.getCeiling(), Integer.parseInt(levelElement.getAttribute("ceiling")));
            assertEquals(levelMap.getPlatforms().size(), levelElement.getElementsByTagName(Platform.TAG).getLength());


        } catch (Exception e) {
            fail(e);
        }
    }
}
