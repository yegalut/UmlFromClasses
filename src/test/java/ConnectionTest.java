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
    void isIdentical() {
    }
}