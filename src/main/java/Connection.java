import java.util.Objects;

public class Connection {
    private String class1 = "";
    private String label1 = "";
    private String typeOfConnection = "";
    private String label2 = "";
    private String class2 = "";

    public Connection() {
    }

    public Connection(String class1, String typeOfConnection, String class2) {
        this.class1 = class1;
        this.typeOfConnection = typeOfConnection;
        this.class2 = class2;
    }

    public Connection(String class1, String typeOfConnection, String label2, String class2) {
        this.class1 = class1;
        this.typeOfConnection = typeOfConnection;
        this.label2 = label2;
        this.class2 = class2;
    }

    public Connection(String class1, String label1, String typeOfConnection, String label2, String class2) {
        this.class1 = class1;
        this.label1 = label1;
        this.typeOfConnection = typeOfConnection;
        this.label2 = label2;
        this.class2 = class2;
    }

    public String getLabel1() {
        return label1;
    }

    public void setLabel1(String label1) {
        this.label1 = label1;
    }

    public String getLabel2() {
        return label2;
    }

    public void setLabel2(String label2) {
        this.label2 = label2;
    }

    public String getClass1() {
        return class1;
    }

    public void setClass1(String class1) {
        this.class1 = class1;
    }

    public String getTypeOfConnection() {
        return typeOfConnection;
    }

    public void setTypeOfConnection(String typeOfConnection) {
        this.typeOfConnection = typeOfConnection;
    }

    public String getClass2() {
        return class2;
    }

    public void setClass2(String class2) {
        this.class2 = class2;
    }

    public Connection mirror(){
        return new Connection(this.class2,
                this.label2,
                this.typeOfConnection,
                this.label1,
                this.class1);
    }

    @Override
    public String toString() {
        return  class1 +  " " + label1 + ' ' + typeOfConnection + ' ' + label2 + ' ' + class2;
    }

   public boolean isIdentical(Connection con) {
       return (this.class1.equals(con.class1) &&
               this.label1.equals(con.label1) &&
               this.typeOfConnection.equals(con.typeOfConnection) &&
               this.label2.equals(con.label2) &&
               this.class2.equals(con.class2));
   }
}
