import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionTest {

    @Test
    void shouldMirror() {
        Connection connection = new Connection("class1", "-->", "class2");
        Connection mirroredConnection = new Connection("class2", "-->", "class1");
        assertEquals(mirroredConnection.toString(), connection.mirror().toString());
        assertEquals(connection.toString(), connection.mirror().mirror().toString());
        assertNotEquals(connection.mirror().toString(), connection.toString());
    }

    @Test
    void shouldCorrectlyDetermineIfConnectionIsIdentical() {
        Connection connection = new Connection("class1", "label1",
                "-->","label2", "class2");
        Connection sameConnection = new Connection("class1", "label1",
                "-->","label2", "class2");
        Connection differentConnection = new Connection("class2", "-->", "class1");
        assertTrue(connection.isIdentical(connection));
        assertTrue(connection.isIdentical(sameConnection));
        assertFalse(connection.isIdentical(differentConnection));
    }
}