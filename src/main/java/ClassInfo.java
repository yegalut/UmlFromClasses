import java.util.ArrayList;
import java.util.List;

public class ClassInfo {

    private String classPackage = "";
    private String modifiers = "";
    private String type = "";
    private String className = "";
    private String generics = "";
    private String extAndImp = "";
    private List<String> fieldList = new ArrayList<>();
    private List<String> methodList = new ArrayList<>();
    private List<Connection> connections = new ArrayList<>();


    public ClassInfo() {
    }



    public String getModifiers() {
        return modifiers;
    }

    public void setModifiers(String modifiers) {
        this.modifiers = modifiers;
    }

    public String getExtAndImp() {return extAndImp;}

    public void setExtAndImp(String extAndInter) {this.extAndImp = extAndInter;}

    public String getClassPackage() {return classPackage;}

    public void setClassPackage(String classPackage) {this.classPackage = classPackage;}

    public String getGenerics() { return generics; }

    public void setGenerics(String generics) { this.generics = generics; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public List<Connection> getConnections() { return connections; }

    public void setConnections(List<Connection> connections) { this.connections = connections; }

    public void addConnection(Connection connection) { this.connections.add(connection); }

    public void addConnections(List<Connection> connection) { this.connections.addAll(connection); }

    public String getClassName() { return className; }

    public void setClassName(String className) { this.className = className; }

    public List<String> getFieldList() { return fieldList; }

    public void setFieldList(List<String> fieldList) { this.fieldList = fieldList; }

    public void addField(String field) { this.fieldList.add(field); }

    public List<String> getMethodList() {
        return methodList;
    }

    public void setMethodList(List<String> methodList) {
        this.methodList = methodList;
    }

    public void addToMethodList(List<String> methodList) {
        this.methodList.addAll(methodList);
    }

    @Override
    public String toString() {
        return "ClassInfo{" +
                "className='" + className + '\'' +
                ", fieldList=" + fieldList +
                ", methodList=" + methodList +
                '}';
    }
}
