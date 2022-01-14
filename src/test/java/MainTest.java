import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    private static List<Class<?>> classes;
    private static String testJarPath = ".\\src\\test\\resources\\Loading.jar";


    @BeforeAll
    static void initialize() throws IOException, ClassNotFoundException {
    classes = Main.loadClassesFromJar(testJarPath);
    }

    @Test
    void shouldReturnModifierValues() {
        assertEquals("+", Main.getModifierValues(1));
        assertEquals("+ -", Main.getModifierValues(3));
    }

    @Test
    void shouldNotReturnModifierValuesForOutOfRangeBits() {
        assertEquals("", Main.getModifierValues(4096));
        assertEquals("+", Main.getModifierValues(4097));
        assertEquals("+ -", Main.getModifierValues(4099));
    }

    @Test
    void shouldLoadClassesFromJar() throws IOException, ClassNotFoundException {
        List<String> expected = List.of("lul", "Account", "AccountHolder", "Be",
                "Main", "May", "Maybe", "test", "test2");
        List<String> actual = Main.loadClassesFromJar(testJarPath)
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
    void shouldFormatConnections() {
        List<Connection> connections = Arrays.asList(
                new Connection("class1","-->", "0..*" ,"class2"),
                new Connection("class2","-->", "0..1" ,"class1"),
                new Connection("class1","-->", "0..*" ,"class2"));

        List<Connection> expectedConnections = Arrays.asList(
                new Connection("class1","0..1","---", "0..*" ,"class2"),
                new Connection("class1","-->", "0..*" ,"class2")
        );
        List<Connection> actualConnections = Main.formatConnections(connections);

        assertEquals(actualConnections.size(), expectedConnections.size());
        assertTrue(expectedConnections.get(0).isIdentical(actualConnections.get(0)));
        assertTrue(expectedConnections.get(1).isIdentical(actualConnections.get(1)));
    }

    @Test
    void shouldReturnGenericsInfo() {
        Class<?> aClass = classes.get(1);
        String expectedGenerics = "<T extends com.patryk.AccountHolder " +
                "& com.patryk.test & java.lang.Comparable<? super T>,\n" +
                "E extends java.lang.Object>";
        String actualGenerics = Main.genericsInfo(aClass.getTypeParameters());
        assertEquals(expectedGenerics, actualGenerics);
    }

    @Test
    void shouldReturnClassTypeInfo() {
        List<Class<?>> loadedClasses = new ArrayList<>();
        loadedClasses.add(classes.get(1));
        loadedClasses.add(classes.get(8));

        List<String> expectedTypeInfo = Arrays.asList(
                "class",
                "interface");

        List<String> actualTypeInfo = loadedClasses.stream()
                .map(Main::classTypeInfo)
                .collect(Collectors.toList());

        assertArrayEquals(expectedTypeInfo.toArray(),actualTypeInfo.toArray());
    }

    @Test
    void shouldReturnConstructorInfo() {
        Class<?> aClass = classes.get(1);
        List<String> expectedConstructors = List.of(
                "+ Account(arg0: int, arg1: AccountHolder, arg2: Map<Integer, Maybe>)");
        List<String> actualConstructors = Main.constructorInfo(aClass.getConstructors(),
                aClass.getPackageName());

        assertEquals(expectedConstructors.size(), actualConstructors.size());
        assertTrue(expectedConstructors.containsAll(actualConstructors));
    }

    @Test
    void shouldReturnMethodInfo() {
        Class<?> aClass = classes.get(1);
        List<String> expectedMethods = Arrays.asList(
                "+ getId(): int",
                "+ setId(arg0: int): void",
                "+ randomMathod(arg0: List<? extends com.patryk.Maybe>): void",
                "+ getMaybeMap(): Map<Integer, Maybe>",
                "+ getHolderID(): AccountHolder",
                "+ setHolderID(arg0: AccountHolder): void",
                "+ setMaybeMap(arg0: Map<Integer, Maybe>): void");
        List<String> actualMethods = Main.methodInfo(aClass.getDeclaredMethods());
        assertEquals(expectedMethods.size(), actualMethods.size());
        assertTrue(actualMethods.containsAll(expectedMethods));
    }

    @Test
    void shouldReturnFieldInfo() {
        Class<?> aClass = classes.get(1);
        ClassInfo actualClassInfo = new ClassInfo();
        List<String> expectedFields = Arrays.asList(
                "- id: int",
                "- strings: List<List<String>>",
                "- collections: List<? extends java.util.Collection>"
        );

        Main.fieldInfo(aClass, actualClassInfo, classes);

        assertArrayEquals(expectedFields.toArray(),actualClassInfo.getFieldList().toArray());
    }

}