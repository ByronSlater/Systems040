package systems.team040.functions;

import com.mysql.cj.protocol.Resultset;

import javax.xml.transform.Result;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Student {
    private static final int REG_NO_CHARS = 8;
    private static final int PASSWORD_LENGTH = 12;

    private String studentId, forename, surname, email, username, title;
    private GraduateStatus graduateStatus;

    public Student(String studentId, String forename, String surname, String email, String username, int title) {
        this.studentId = studentId;
        this.forename = forename;
        this.surname = surname;
        this.email = email;
        this.username = username;
        this.title = new String[] { "Mr", "Ms" }[title];
        this.graduateStatus = null;
    }

    public static void inputNewStudent(String forename, String surname, String title) {}

    /**
     * returns -1 on error
     */
    public int getCreditsTakenTotal() {
        String query = "" +
                "SELECT SUM(Credits)" +
                "  FROM Module" +
                "       JOIN Grades" +
                "         ON Module.ModuleID = Grades.ModuleID" +
                "       JOIN StudentStudyPeriod" +
                "         ON Grades.StudentPeriod = StudentStudyPeriod.StudentPeriod" +
                "       JOIN Student" +
                "         ON StudentStudyPeriod.StudentID = Student.StudentID" +
                " WHERE Student.StudentID = ?;";

        try(Connection con = SQLFunctions.connectToDatabase();
            PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, studentId);

            try(ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()) {
                    return rs.getInt(1);
                } else {
                    System.out.println("Couldn't find a grades total for student");
                    return -1;
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Saves the newly created accounts to both relevant tables without any passwords as they're created and shown
     * to the student at a later date
     */
    private static void saveNewAccounts(ArrayList<Student> students) {
        String query1 = "INSERT INTO users(Username, Password, AccountType)" +
                "VALUES(?, NULL, 'S');";
        String query2 = "INSERT INTO students(StudentID, Title, Forename, Surname, Email, Username)" +
                "Values(?, ?, ?, ?, ?, ?);";

        try(Connection con = SQLFunctions.connectToDatabase();
            PreparedStatement pstmt1 = con.prepareStatement(query1);
            PreparedStatement pstmt2 = con.prepareStatement(query2)) {

            con.setAutoCommit(false);

            for(Student student : students) {
                pstmt1.setString(1, student.username);
                pstmt1.addBatch();

                pstmt2.setString(1, student.studentId);
                pstmt2.setString(2, student.title);
                pstmt2.setString(3, student.forename);
                pstmt2.setString(4, student.surname);
                pstmt2.setString(5, student.email);
                pstmt2.setString(6, student.username);
                pstmt2.addBatch();
            }

            pstmt1.executeBatch();
            pstmt2.executeBatch();

            con.commit();
            con.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a randomly generated password with PASSWORD_LENGTH random alphabetic characters and one random
     * number interspersed
     */
    public static char[] generateRandomPassword() {
        char[] availChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890".toCharArray();
        char[] password = new char[PASSWORD_LENGTH];

        for(int i = 0; i < PASSWORD_LENGTH ; i++) {
            password[i] = availChars[(int)(Math.random() * availChars.length)];
        }

        return password;
    }

    public static char[] generatePasswordForUser(String regNo) {
        String query = "UPDATE users SET password = ? WHERE regNo = ?;";
        try(Connection con = SQLFunctions.connectToDatabase();
            PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(2, regNo);

            char[] password = generateRandomPassword();

            String digest = Hasher.generateDigest(password);
            pstmt.setString(1, digest);
            pstmt.execute();

            return password;


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static int getMaxRegNo() {
        String query = "SELECT MAX(regno) FROM students;";

        try(Connection conn = SQLFunctions.connectToDatabase();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {

            if(rs.next()) {
                return 0;
            } else {
                return rs.getInt(1);
            }


        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Takes a file with a list of firstname lastname pairings and converts them into
     * student objects with registration numbers starting from the highest one already in
     * the system
     *
     * These objects then need their emails sorting and then passwords
     */
    public static ArrayList<Student> studentsFromFile(String fileName) {
        ArrayList<Student> students = new ArrayList<>();

        try(FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr)) {

            String line;

            while((line = br.readLine()) != null) {
                String[] parts = line.split("\\s+", 2);
                String regNo = String.format("%0" + REG_NO_CHARS + "d", getMaxRegNo());
                String forename = parts[0];
                String surname = parts[1];

                students.add(new Student(regNo, forename, surname, null, null, 1));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return students;
    }


    /**
     * Second step after importing names, gives a unique email to each student in the
     * list of students. Every student's email is in the form jsmith1 etc. numbers
     * after emails start either at 1 or after the very highest number recorded to
     * a given name-surname combination on the current database to ensure no collisions
     */
    private static String generateEmail(String username) {

        int maxInt = 1;
        String query = "SELECT EmailAddress from Student WHERE EmailAddress LIKE ? || ?;";
        Pattern pattern = Pattern.compile("(?<text>.+?)(?<number>\\d+)@Sheffield.ac.uk", Pattern.CASE_INSENSITIVE);

        try(Connection con = SQLFunctions.connectToDatabase();
            PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, username);
            try(ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    String email = rs.getString("EmailAddress");
                    Matcher match = pattern.matcher(email);

                    // regex not matched, skip
                    if(!match.find()) {
                        continue;
                    }

                    // username doesn't match, skip
                    if(!match.group("text").equals(username)) {
                        continue;
                    }

                    int intPart = Integer.parseInt(match.group("number"));
                    if((intPart + 1) > maxInt) {
                        maxInt = intPart + 1;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return username + maxInt;
    }

    private static void giveStudentsEmails(ArrayList<Student> students) {
        // get a map to show us what number has already been taken up to
        // for each possible email
        ArrayList<String> emails = getEmails();
        Collections.sort(emails);
        Pattern pattern = Pattern.compile("(?<text>.+?)(?<number>\\d+)@Sheffield.ac.uk");
        HashMap<String, Integer> emailToNumber = new HashMap<>();

        for (String email : emails) {
            Matcher match = pattern.matcher(email);
            if (!match.find()) {
                System.out.println("Email in incorrect format found: '" + email + "'");
            }

            String textPart = match.group("text");
            int number = Integer.parseInt(match.group("number"));

            emailToNumber.put(textPart, Math.max(number + 1, emailToNumber.getOrDefault(textPart, 1)));
        }

        // for each student get a preliminary email which we'll append numbers
        // to if need be
        for(Student student : students) {
            student.email = namesToEmail(student.forename, student.surname);
        }

        // sorting by email so it's easier/faster in future
        students.sort(Comparator.comparing(student -> student.email));

        String rootEmail = "";
        int num = 0;

        for (Student student : students) {
            if (rootEmail.equals(student.email)) {
                num++;
                student.email += num;
            } else {
                num = emailToNumber.getOrDefault(student.email, 1);
                student.email += num;
            }
        }
    }

    private static ArrayList<String> getEmails() {
        ArrayList<String> emails = new ArrayList<>();
        String query = "SELECT email FROM students";

        try(Connection conn = SQLFunctions.connectToDatabase();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {

            while(rs.next()) {
                emails.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return emails;
    }

    /**
     * Converts a students name to their email
     */
    private static String namesToEmail(String forename, String surname) {
        return (forename.charAt(0) + surname)
                .toLowerCase()
                .replaceAll("[^A-Za-z]", "");
    }

    /**
     * Returns an array of every student, created by looking at the rows of the table
     */
    static ArrayList<Student> getAllStudents() {
        ArrayList<Student> students = new ArrayList<>();

        String query = "" +
                "SELECT studentId, forename, surname, email, username, title" +
                "  FROM students;";

        try(Connection conn = SQLFunctions.connectToDatabase();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {


            while(rs.next()) {
                String studentId = rs.getString(1);
                String forename = rs.getString(2);
                String surname = rs.getString(3);
                String email = rs.getString(4);
                String username = rs.getString(5);
                int title = rs.getInt(6);

                students.add(new Student(studentId, forename, surname, email, username, title));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }
}

