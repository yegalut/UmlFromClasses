import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    @Disabled("not implemented")
    void extractInfoFromClasses() {

    }

    @Test
    void shouldReturnModifierValues() {
        assertEquals("+", Main.getModifierValues(1));
        assertEquals("+ -", Main.getModifierValues(3));
    }

    @Test
    void shouldNotReturnModifierValues() {
        assertEquals("", Main.getModifierValues(4096));
    }

    @Test
    void shouldLoadClassesFromJar() throws IOException, ClassNotFoundException {
        String pathToTestJarFile = "C:\\Users\\Admin\\IdeaProjects\\UmlFromClasses\\src\\test\\resources\\Loading.jar";
        List<String> expected = List.of("lul", "Account", "AccountHolder", "Be", "Main", "May", "Maybe", "test", "test2");
        List<String> actual = Main.loadClassesFromJar(pathToTestJarFile)
                .stream()
                .map(Class::getSimpleName)
                .collect(Collectors.toList());

        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowExceptionWhileLoadingFromAWrongPath() {
        String incorrectPath = "C:\\Users\\Admin\\IdeaProjects\\UmlFromClasses\\src\\test\\resources";
        assertThrows(IOException.class, () -> Main.loadClassesFromJar(incorrectPath));
    }



    @Test
    @Disabled("not implemented")
    void extractRelationsFromNestedTypes() {
    }

    @Test
    @Disabled("not implemented")
    void extractNestedParameters() {
    }

    @Test
    @Disabled("not implemented")
    void typeHelper() {
    }

    @Test
    @Disabled("not implemented")
    void testExtractInfoFromClasses() {
    }

    @Test
    @Disabled("not implemented")
    void formatConnections() {


    }

    @Test
    @Disabled("not implemented")
    void escape() {
    }

    @Test
    @Disabled("not implemented")
    void classInfoToUml() {
    }
}