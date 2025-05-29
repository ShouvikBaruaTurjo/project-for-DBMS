package util;

import java.sql.Connection; // sql database er shathe connect korar jonno
import java.sql.DriverManager; // sql database er shathe interaction korar jonno
import java.sql.SQLException; //Error catching er jonno use korlam

public class DBConnection {
    //private static final use korsi karon ei variable gulo constant. aar class er baire kono access nai.
    private static final String URL = "jdbc:mysql://localhost:3306/bankdb"; //database er address
    private static final String USER = "root"; //database er username
    private static final String PASSWORD = "Turjo123*"; //database er password. apatotoh hardcoded. Huge security risk

    //getConnection method create korlam. Ei method ekta "Connection" type object return korbe. ei Object er use case onno jaygay thakbe
    // method tah public static. Karon kono instance creation charai amra ei method ta use korte chai.
    public static Connection getConnection() {
        //try-catch format ekhane besh useful. kono errors dekha dile sheta ekhanei dhora jabe.
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // very important. MySQL driver tah java teh load korbe ei line. Ekhane JAR file er usage ase
                                                                 // Online theke JDBC driver tah jar format e niye dependency teh
                                                                 // add korsilam. Ei dependency er karone onek bugs face korte hoisilo. Thankfully Intelliji IDE teh khub easily add kora gesilo, without command line shennanigans

            return DriverManager.getConnection(URL, USER, PASSWORD); // will actually form the connection
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace(); //ClassNotFoundException - SQL Driver tah missing chilo.
                                //SQLException - connection tah failed hoise, usually wrong credentials er jonno ei error tah ashbe
            return null;
        }
    }
}


