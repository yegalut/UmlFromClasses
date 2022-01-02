import java.io.*;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import static java.util.Map.entry;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {


    // keys of this map are a bitmask to compare against the value of .getModifiers() method from Reflection API
    private static Map<Integer,String> modifierValues = Map.ofEntries(
            entry(1,"+"),//"PUBLIC"
            entry(2,"-"),//"PRIVATE"
            entry(4,"#"),//"PROTECTED"
            entry(8,"{STATIC}"),
            entry(16,"FINAL"),
            entry(32,"SYNCHRONIZED"),
            entry(64,"VOLATILE"),
            entry(128,"TRANSIENT"),
            entry(256,"NATIVE"),
            entry(512,"INTERFACE"),
            entry(1024,"{ABSTRACT}"),
            entry(2048,"STRICT")
    );

    private static List<String> classesNames = new ArrayList<>();

    /**
    This function returns a string of values by checking the result of a bitwise AND operation between every
     key of modifierValues map and modifiers variable.
    For example a modifiers value of 9 will return "+ STATIC"
    */
    public static String getModifierValues(int modifiers){
        return modifierValues.entrySet().stream()
                .filter(x -> (x.getKey() & modifiers) != 0)
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.joining(" "));
    }

    public static List<Class<?>> loadClassesFromJar(String filePath) throws ClassNotFoundException, IOException {

        List<Class<?>> classes = new ArrayList<>();


        JarFile jarFile = new JarFile(filePath);
        Enumeration<JarEntry> entries = jarFile.entries();

        URL[] urls = {new URL("jar:file:" + filePath + "!/")};

        URLClassLoader loader = URLClassLoader.newInstance(urls);

        CustomClassLoader loader2 = new CustomClassLoader(urls);

        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();

            if (jarEntry.getName().endsWith(".class") && !jarEntry.isDirectory()) {
                //.class = 6 characters, so -6 to get rid of file extension from the name
                String className = jarEntry.getName().substring(0, jarEntry.getName().length() - 6);
                //changing the directory "/" to a qualified name "."
                className = className.replace('/', '.');
                classes.add(loader2.loadClass(className));
            }
        }

        loader.close();


        classesNames = classes.stream().map(Class::getName).collect(Collectors.toList());

        return classes;
    }

    //recursive function that attempts to create a field type with simple names
    public static String extractNestedParameters(List<Type> types) {
        List<String> typeNames= new ArrayList<>();
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                typeNames.add((((Class<?>) ((ParameterizedType) type).getRawType()).getSimpleName()) +
                        (extractNestedParameters(List.of(((ParameterizedType) type).getActualTypeArguments()))));
            }else {
                try {
                    //try to add a simple name of a type, example List<Integer> instead of java.util.List<java.lang.Integer>
                    typeNames.add((((Class<?>) type).getSimpleName()));
                }catch (ClassCastException e){
                    //if you can't add it in a simple form then just save it as it is, example would be List<? extends java.util.Collection>
                    typeNames.add(type.getTypeName());
                }
            }
        }
        return "<" + String.join(", ", typeNames) + ">";
    }

    //this method is used when you need to get actual type parameters from a given type, for example it extracts
    public static List<Type> typeHelper(Type type){
        try {
            return Stream.of(((ParameterizedType) type).getActualTypeArguments() ).collect(Collectors.toList());
        }catch ( ClassCastException e){
            return new ArrayList<>();
        }
    }

    public static String genericsInfo(TypeVariable<? extends Class<?>>[] typeParameters){
        String s= Stream.of(typeParameters)
                .map(typeVariable -> typeVariable.getName() + " extends " +
                        Arrays.stream(typeVariable.getBounds())
                                .map(Type::getTypeName)
                                .collect(Collectors.joining(" & ")))
                .collect(Collectors.joining(",\n"));
        if(s.isEmpty()){
            return "";
        }else {
            return "<" + s + ">";
        }
    }

    public static String classTypeInfo(Class<?> aClass){
        //todo abstract, enum, maybe record
        if(aClass.isInterface()){
            return "interface";
        }else if(aClass.isEnum()) {
            return "enum";
        } else{
            return "class";
        }
    }

    public static List<String> constructorInfo(Constructor<?>[] constructors, String packageName){
        //constructors
        return Arrays.stream(constructors)
                .map(constructor -> getModifierValues(constructor.getModifiers()) +" "+
                        constructor.getName().substring(packageName.length()+1) +"("+
                        Arrays.stream(constructor.getParameters())
                                .map(parameter -> parameter.getName() +": "+
                                        parameter.getType().getSimpleName())
                                .collect(Collectors.joining(", ")) +")")
                .collect(Collectors.toList());
    }

    public static List<String> methodInfo(Method[] declaredMethods){
        //methods (im so sorry me form the future)
        return Stream.of(declaredMethods)
                .map(method-> getModifierValues(method.getModifiers()) +" "+
                        method.getName() + "(" +
                        Arrays.stream(method.getParameters())
                                .map(parameter -> parameter.getName() + ": " +
                                        ((parameter.getParameterizedType() instanceof ParameterizedType)
                                                ?parameter.getType().getSimpleName() + extractNestedParameters(typeHelper(parameter.getParameterizedType()))
                                                :parameter.getType().getSimpleName()) )
                                .collect(Collectors.joining(", ")) +"): "+
                        ((method.getGenericReturnType() instanceof ParameterizedType)
                                ?method.getReturnType().getSimpleName() + extractNestedParameters(typeHelper(method.getGenericReturnType()))
                                :method.getReturnType().getSimpleName()))
                .collect(Collectors.toList());
    }

    public static void fieldInfo(Class<?> aClass, ClassInfo classInfo, List<Class<?>> classes){
        //Fields
        for (Field field : aClass.getDeclaredFields()) {
            //checking if field type can have parameters, for example List<String>
            if(field.getGenericType() instanceof ParameterizedType) {
                //checking if any of the type parameters are a Class of this program
                List<Connection> connections = classes.stream()
                        .filter(cls -> field.getGenericType().getTypeName().contains(cls.getName() + ">") ||
                                field.getGenericType().getTypeName().contains(cls.getName() + ","))
                        .map(cls -> new Connection(
                                classInfo.getClassName(),
                                "-->",
                                "0..*\n" + field.getName(),
                                cls.getSimpleName()))
                        .collect(Collectors.toList());

                if(connections.isEmpty()){
                    //if there aren't any then save it as a field
                    String nestedParameters = extractNestedParameters(typeHelper(field.getGenericType()));
                    classInfo.addField(getModifierValues(field.getModifiers()) + " " +
                            field.getName() + ": " +
                            field.getType().getSimpleName() + nestedParameters);
                }else{
                    classInfo.addConnections(connections);
                }

            }else if(field.getType().isArray()){

                //todo 0..<size of array> for the relationships
                if(classesNames.contains(field.getType().getComponentType().getName())){
                    classInfo.addConnection(new Connection(aClass.getSimpleName(),
                            "-->",
                            "0..*\n"+field.getName(),
                            field.getType().getComponentType().getSimpleName()));
                } else{
                    classInfo.addField(getModifierValues(field.getModifiers()) +" "+
                            field.getName() +": "+
                            field.getType().getSimpleName());
                }
            }else{
                //if field is not part of a collection, map, and not an array then check if it's type is a program class
                if(!classesNames.contains(field.getType().getName())){
                    //if it's not, then save it as a field
                    classInfo.addField(getModifierValues(field.getModifiers()) +" "+
                            field.getName() +": "+
                            field.getType().getSimpleName());
                }else{
                    //and if it is, then save it as a relation
                    classInfo.addConnection(new Connection(
                            aClass.getSimpleName(),
                            "-->",
                            "0..1\n"+field.getName(),
                            field.getType().getSimpleName()
                    ));
                }
            }
        }
    }


    public static List<ClassInfo> extractInfoFromClasses(List<Class<?>> classes) {

        List<ClassInfo> classInfos =new ArrayList<>();

        for (Class<?> aClass: classes) {
            ClassInfo classInfo = new ClassInfo();

            //package
            classInfo.setClassPackage(aClass.getPackageName());

            //modifiers
            classInfo.setModifiers(getModifierValues(aClass.getModifiers()));

            //Type
            classInfo.setType(classTypeInfo(aClass));

            //Name
            classInfo.setClassName( aClass.getSimpleName());

            //Generics
            classInfo.setGenerics(genericsInfo(aClass.getTypeParameters()));

            //Fields
            fieldInfo(aClass, classInfo, classes);

            //Constructors
            classInfo.setMethodList(constructorInfo(aClass.getConstructors(), aClass.getPackageName()));

            //Methods
            classInfo.addToMethodList(methodInfo(aClass.getDeclaredMethods()));

            //Extends connection
            if(classInfo.getType().equals("class") && classesNames.contains(aClass.getSuperclass().getName())){
                classInfo.setExtAndImp(" extends "+ aClass.getSuperclass().getSimpleName());
                //classInfo.addConnection(new Connection(aClass.getSimpleName(),"--|>", aClass.getSuperclass().getSimpleName()));
            }

            //Implements connection
            Class<?>[] interfaces = aClass.getInterfaces();
            if(interfaces.length!=0){
                classInfo.setExtAndImp(classInfo.getExtAndImp().concat(" implements "+ Stream.of(interfaces).map(Class::getSimpleName)
                        .collect(Collectors.joining(", "))));
            }

            //todo classes inside of classes connection

            classInfos.add(classInfo);
        }
        return classInfos;
    }

    public static List<Connection> formatConnections(List<Connection> connections){
        List<Connection> finalConnections = new ArrayList<>();
        List<Connection> used = new ArrayList<>();

        for (Connection connection: connections) {

            if (!used.contains(connection)) {

                Connection oppositeCon = connections.stream()
                        .filter(con ->
                                con.getClass1().equals(connection.getClass2()) &&
                                        con.getClass2().equals(connection.getClass1()) &&
                                        !used.contains(con)
                        )
                        .findFirst().orElse(null);

                if (oppositeCon != null && !used.contains(oppositeCon)) {

                    used.add(connection);
                    used.add(oppositeCon);

                    Connection tempCon = new Connection(connection.getClass1(),
                            oppositeCon.getLabel2(),
                            "---",
                            connection.getLabel2(),
                            connection.getClass2());
                    if (finalConnections.stream().noneMatch(finCon -> finCon.isIdentical(tempCon) ||
                            finCon.isIdentical(tempCon.mirror()))) {
                        finalConnections.add(tempCon);
                    }

                } else {
                    finalConnections.add(connection);
                }
            }
        }

        return finalConnections;
    }

    public static String escape(String label){
        return label.replace("\n","\\n");
    }

    public static void classInfoToUml(List<ClassInfo> classInfos, String savePath) throws IOException {
        //TODO savePath should be taken from user and test.puml is just a temporary name
        List<Connection> finalConnections = formatConnections(classInfos.stream()
                .flatMap(classInfo -> classInfo.getConnections().stream())
                .collect(Collectors.toList()));

        File pumlFile = new File(savePath + "test.puml");
        BufferedWriter writer = new BufferedWriter(new FileWriter(pumlFile));
        writer.write("@startuml\n" +

                classInfos.stream().map(classInfo ->
                                classInfo.getType() + " " + classInfo.getClassName() + escape(classInfo.getGenerics()) + classInfo.getExtAndImp() + " {\n" +
                                        String.join("\n",classInfo.getFieldList())+"\n"+
                                        String.join("\n",classInfo.getMethodList())+"\n}")
                        .collect(Collectors.joining("\n")) + "\n" +

                finalConnections.stream()
                        .map(con -> con.getClass1() +
                                (con.getLabel1().isEmpty()? " " : " \"" + escape(con.getLabel1()) + "\" ")+
                                con.getTypeOfConnection()+
                                (con.getLabel2().isEmpty()? " " : " \"" + escape(con.getLabel2()) + "\" ")+
                                con.getClass2())
                        .collect(Collectors.joining("\n")) + "\n"
                +"\n@enduml"
        );
        writer.close();
    }

    public static void main(String[] args) throws IOException {
        //String pathToJar="C:\\Users\\Admin\\IdeaProjects\\Loading\\out\\artifacts\\Loading_jar\\Loading.jar";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String pathToJar;
        boolean exit = false;
        while(!exit){
            System.out.println("Please provide a valid absolute path to a jar file:");
            try {
                pathToJar = reader.readLine();
                List<Class<?>> classes = loadClassesFromJar(pathToJar);
                List<ClassInfo> classInfos = extractInfoFromClasses(classes);
                classInfoToUml(classInfos, pathToJar.substring(0, pathToJar.lastIndexOf("\\")+1));
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Error while loading file, make sure that the provided path and file type is correct:\n");
            }

            while(true){
                System.out.println("Do you wish to continue?[y/n]: ");
                String answer = reader.readLine();
                if(answer.equals("n")) {
                    exit = true;
                    break;
                }else if(answer.equals("y")){
                    break;
                }else{
                    System.out.println("command not recognized.");
                }
            }
        }

    }
}
