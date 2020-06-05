import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import java.util.Map;
import java.util.Scanner;
import java.util.LinkedHashMap;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.DayOfWeek;

import java.text.DateFormat;
import java.util.Locale;
import java.time.*;
import java.util.Formatter;
import java.util.Calendar;

class InnReservations{
   public static void main(String[] args){
      System.out.println("Hello World!");
      int option = 0;
      Scanner scanner = new Scanner(System.in);
      
      while(option != 7) {
         if (option == 0){
            System.out.println("\nWelcome to our Hotel Reservation System!");
            System.out.println("1 -- Rooms and Rates");
            System.out.println("2 -- Reservations");
            System.out.println("3 -- Reservation Change");
            System.out.println("4 -- Reservation Cancellation");
            System.out.println("5 -- Detailed Reservation Information");
            System.out.println("6 -- Revenue");
            System.out.println("7 -- Quit\n");
         }
         try{
            System.out.print("Enter an option: ");
            option = scanner.nextInt();
         }
         catch(Exception e){
            scanner.nextLine();
            option = 0;
         }
         
         switch(option){
            case 1:
               roomsAndRates();
               option = 0;
               break;
            case 2:
               gatherReservation();
               option = 0;
               break;
            case 3:
               changeReservation();
               option = 0;
               break;
            case 4:
               cancellation();
               option = 0;
               break;
            case 5:
               search();
               option = 0;
               break;
            case 6:
               option = 0;
               printRevenues();
               break;
            case 7:
               System.out.println("Goodbye, have a great day! (:");
               break;
            default:
               System.out.println("ERROR 404 OPTION NOT FOUND! D:<");
               option = 0;
               break;
         }
      }
      scanner.close();
   }
   
   public static void search(){
      Scanner scanner = new Scanner(System.in);
      System.out.println("Enter the first name you would like to search for: (Leave Blank for Any)");
      String firstname = scanner.nextLine();
      System.out.println("Enter the last name you would like to search for: (Leave Blank for Any)");
      String lastname = scanner.nextLine();
      System.out.println("Enter the start date you would like to search for: (Leave Blank for Any)");
      String startDate = scanner.nextLine();
      System.out.println("Enter the end date you would like to search for: (Leave Blank for Any)");
      String endDate = scanner.nextLine();
      System.out.println("Enter the room code you would like to search for: (Leave Blank for Any)");
      String roomcode = scanner.nextLine();
      System.out.println("Enter the reservation code you would like to search for: (Leave Blank for Any)");
      String resvCodeStr = scanner.nextLine();
      int resvCode = -1;
      
      firstname += "%";
      lastname += "%";
      roomcode += "%";
      
      // if (firstname.equals("")) { firstname = "%"; }
      // if (lastname.equals("")) { lastname = "%"; }
      if (startDate.equals("")) { startDate = "%"; }
      if (endDate.equals("")) { endDate = "%"; }
      // if (roomcode.equals("")) { roomcode = "%"; }
      
      if (resvCodeStr.equals("")) {
         resvCodeStr = "%";
      }
      // else {
      //    resvCode = Integer.parseInt(resvCodeStr);
      //    resvCodeStr = Integer.toString(resvCode);
      //    System.out.println("after resvCodeStr = Integer.toString(resvCode)");
      // }
      
      try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                                                         System.getenv("HP_JDBC_USER"),
                                                         System.getenv("HP_JDBC_PW"))) {
         String sql = "SELECT lab7_rooms.roomcode, lab7_rooms.roomname, " +
         "lab7_reservations_copy.code, lab7_reservations_copy.checkin, lab7_reservations_copy.checkout, " +
         "lab7_reservations_copy.rate, lab7_reservations_copy.lastname, lab7_reservations_copy.firstname, " +
         "lab7_reservations_copy.adults, lab7_reservations_copy.kids " +
         "FROM lab7_rooms, lab7_reservations_copy " +
         "WHERE lab7_rooms.roomcode = lab7_reservations_copy.room " +
         "AND lab7_reservations_copy.firstname LIKE ? " +
         "AND lab7_reservations_copy.lastname LIKE ? " +
         "AND lab7_reservations_copy.checkIn LIKE ? " +
         "AND lab7_reservations_copy.checkOut LIKE ? " +
         "AND lab7_reservations_copy.room LIKE ? " +
         "AND lab7_reservations_copy.code LIKE ?;";
         PreparedStatement pstmt = conn.prepareStatement(sql);
         // pstmt.setString(1, "'" + firstname + "'");
         // pstmt.setString(2, "'" + lastname + "'");
         // pstmt.setString(3, "'" + startDate + "'");
         // pstmt.setString(4, "'" + endDate + "'");
         // pstmt.setString(5, "'" + roomcode + "'");
         // pstmt.setString(6, "'" + resvCodeStr + "'");
         
         pstmt.setString(1, firstname);
         pstmt.setString(2, lastname);
         pstmt.setString(3, startDate);
         pstmt.setString(4, endDate);
         pstmt.setString(5, roomcode);
         pstmt.setString(6, resvCodeStr);
         
         // System.out.println("done setString");
         
         int count = 0;
         
         ResultSet rs = pstmt.executeQuery();
         // ResultSet rs = pstmt.executeQuery(sql);
         
         while (rs.next()) {
            count++;
            roomcode = rs.getString("roomcode");
            String roomname = rs.getString("roomname");
            int resCode = rs.getInt("code");
            String checkin = rs.getString("checkin");
            String checkout = rs.getString("checkout");
            float rate = rs.getFloat("rate");
            lastname = rs.getString("lastname");
            firstname = rs.getString("firstname");
            int adults = rs.getInt("adults");
            int kids = rs.getInt("kids");
            
            System.out.format("%s | %s | %d | %s | %s | $%.2f | %s | %s | %d | %d | %n",
                              roomcode, roomname, resCode, checkin, checkout, rate, lastname, firstname, adults, kids);
         }
         if (count == 0) {
            System.out.println("No matching reservations found :(");
         }
         else {
            System.out.println(count + " matching reservations found");
         }
         
      }
      catch(Exception e){System.out.println("HERE " + e);}
   }
   
   public static void cancellation(){
      Scanner scanner = new Scanner(System.in);
      System.out.println("Please enter the reservation code you would like to cancel: ");
      int code = scanner.nextInt();
      scanner.nextLine();
      if(!existingReservation(code)){
         System.out.println("Reservation Number Does Not Exist!!");
         return;
      }
      System.out.println("Are you sure you would like to cancel reservation " + code + "? (yes/no)");
      String confirm = scanner.nextLine();
      if(confirm.equalsIgnoreCase("yes")){
         System.out.println("Cancelling Reservation... ");
         try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                                                            System.getenv("HP_JDBC_USER"),
                                                            System.getenv("HP_JDBC_PW"))) {
            // Step 4: Send SQL statement to DBMS
            String new_sql = "DELETE FROM lab7_reservations_copy WHERE CODE = ?";
            PreparedStatement pstmt = conn.prepareStatement(new_sql);
            pstmt.setInt(1, code);
            pstmt.executeUpdate();
         }
         catch(Exception e){System.out.println("HERE " + e);}
      }
   }
   
   public static void getChangeInfo(int resvCode){
      String Room = "";
      String checkIn = "";
      String checkOut = "";
      float rate = 0;
      String firstname = "";
      String lastname = "";
      int adult = 0;
      int children = 0;
      Scanner scanner = new Scanner(System.in);
      try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                                                         System.getenv("HP_JDBC_USER"),
                                                         System.getenv("HP_JDBC_PW"))) {
         String sql = "SELECT * FROM lab7_reservations_copy WHERE CODE = " + resvCode;
         // Step 4: Send SQL statement to DBMS
         try (PreparedStatement stmt = conn.prepareStatement(sql);
              ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()){
               Room = rs.getString("Room");
               checkIn = rs.getString("CheckIn");
               checkOut = rs.getString("Checkout");
               rate = rs.getFloat("Rate");
               firstname = rs.getString("FirstName");
               lastname = rs.getString("LastName");
               adult = rs.getInt("Adults");
               children = rs.getInt("Kids");
               
//               System.out.format("%s | %s | %s | $%.2f | %s | %s | %d | %d | %n", Room, checkIn, checkOut, rate, firstname, lastname, adult, children);
            }
            // Check Changes in First Name
            System.out.println("Would you like to change the first name? Enter a new firstname or input 'no change'");
            String change = scanner.nextLine().toUpperCase();
            if(!change.equalsIgnoreCase("no change")){
               String new_sql = "UPDATE lab7_reservations_copy "
                   + "SET FirstName = ? "
                   + "WHERE CODE = ?";
               PreparedStatement pstmt = conn.prepareStatement(new_sql);
               pstmt.setString(1, change);
               pstmt.setInt(2, resvCode);
               pstmt.executeUpdate();
               System.out.println("Your reservation has been changed. ");
            }
            // Check Changes in Last Name
            System.out.println("Would you like to change the last name? Enter a new lastname or input 'no change'");
            change = scanner.nextLine().toUpperCase();
            if(!change.equalsIgnoreCase("no change")){
               String new_sql = "UPDATE lab7_reservations_copy "
               + "SET LastName = ? "
               + "WHERE CODE = ?";
               PreparedStatement pstmt = conn.prepareStatement(new_sql);
               pstmt.setString(1, change);
               pstmt.setInt(2, resvCode);
               pstmt.executeUpdate();
               System.out.println("Your reservation has been changed. ");
            }
            // Check Changes in checkin/checkout dates
            System.out.println("Would you like to change the check in date? Enter a new date (YYYY-MM-DD) or input 'no change'");
            String checkinchange = scanner.nextLine();
            System.out.println("Would you like to change the check out date? Enter a new date (YYYY-MM-DD) or input 'no change'");
            String checkoutchange = scanner.nextLine();
            // no yes
            if(checkinchange.equalsIgnoreCase("no change") && !checkoutchange.equalsIgnoreCase("no change")){
               // check for valid input
               if(!checkCheckOut(checkIn, checkoutchange)){
                  System.out.println("Invalid Checkout date, must be after check in date!!!");
                  return;
               }
               // check if available.
               if(checkDateAvailability(resvCode, Room, checkIn, checkoutchange)){
                  String new_sql = "UPDATE lab7_reservations_copy "
                  + "SET Checkout = ? "
                  + "WHERE CODE = ?";
                  PreparedStatement pstmt = conn.prepareStatement(new_sql);
                  pstmt.setString(1, checkoutchange);
                  pstmt.setInt(2, resvCode);
                  pstmt.executeUpdate();
                  System.out.println("Your reservation has been changed. ");
               }
               else{
                  System.out.println("Room not available during that time!");
                  return;
               }
            }
            // yes no
            else if(!checkinchange.equalsIgnoreCase("no change") && checkoutchange.equalsIgnoreCase("no change")){
               // check for valid input
               if(!checkCheckIn(checkinchange)){
                  System.out.println("Invalid Checkin date, must be after today's date!!!");
                  return;
               }
               if(checkDateAvailability(resvCode, Room, checkinchange, checkOut)){
                  String new_sql = "UPDATE lab7_reservations_copy "
                  + "SET Checkin = ? "
                  + "WHERE CODE = ?";
                  PreparedStatement pstmt = conn.prepareStatement(new_sql);
                  pstmt.setString(1, checkinchange);
                  pstmt.setInt(2, resvCode);
                  pstmt.executeUpdate();
                  System.out.println("Your reservation has been changed. ");
               }
               else{
                  System.out.println("Room not available during that time!");
                  return;
               }
            }
            // yes yes
            else if(!checkinchange.equalsIgnoreCase("no change") && !checkoutchange.equalsIgnoreCase("no change")){
               // check for valid input
               if(!checkCheckIn(checkinchange)){
                  System.out.println("Invalid Checkin date, must be after today's date!!!");
                  return;
               }
               // check for valid input
               if(!checkCheckOut(checkinchange, checkoutchange)){
                  System.out.println("Invalid Checkout date, must be after check in date!!!");
                  return;
               }
               if(checkDateAvailability(resvCode, Room, checkinchange, checkoutchange)){
                  String new_sql = "UPDATE lab7_reservations_copy "
                  + "SET Checkin = ? , Checkout = ?"
                  + "WHERE CODE = ?";
                  PreparedStatement pstmt = conn.prepareStatement(new_sql);
                  pstmt.setString(1, checkinchange);
                  pstmt.setString(2, checkoutchange);
                  pstmt.setInt(3, resvCode);
                  pstmt.executeUpdate();
                  System.out.println("Your reservation has been changed. ");
               }
               else{
                  System.out.println("Room not available during that time!");
                  return;
               }
            }
            System.out.println("Would you like to change the number of adults? Enter a number or input 'no change'");
            String adultChange = scanner.nextLine();
            System.out.println("Would you like to change the number of children? Enter a number or input 'no change'");
            String childrenChange = scanner.nextLine();
            // no yes
            int newchild = 0;
            int newadult = 0;
            if(adultChange.equalsIgnoreCase("no change") && !childrenChange.equalsIgnoreCase("no change")){
               newchild = Integer.parseInt(childrenChange);
               System.out.println(adult);
               if(!checkRoomOcc(adult + newchild, Room)){
                  System.out.println("Total guests exceeds max room occupancy!!!");
                  return;
               }
               else{
                  String new_sql = "UPDATE lab7_reservations_copy "
                  + "SET Kids = ? "
                  + "WHERE CODE = ?";
                  PreparedStatement pstmt = conn.prepareStatement(new_sql);
                  pstmt.setInt(1, newchild);
                  pstmt.setInt(2, resvCode);
                  pstmt.executeUpdate();
                  System.out.println("Your reservation has been changed. ");
               }
            }
            // yes no
            else if(!adultChange.equalsIgnoreCase("no change") && childrenChange.equalsIgnoreCase("no change")){
               newadult = Integer.parseInt(adultChange);
               if(!checkRoomOcc(newadult + children, Room)){
                  System.out.println("Total guests exceeds max room occupancy!!!");
                  return;
               }
               else{
                  String new_sql = "UPDATE lab7_reservations_copy "
                  + "SET Adults = ? "
                  + "WHERE CODE = ?";
                  PreparedStatement pstmt = conn.prepareStatement(new_sql);
                  pstmt.setInt(1, newadult);
                  pstmt.setInt(2, resvCode);
                  pstmt.executeUpdate();
                  System.out.println("Your reservation has been changed. ");
               }
            }
            // yes yes
            else if(!adultChange.equalsIgnoreCase("no change") && !childrenChange.equalsIgnoreCase("no change")){
               newchild = Integer.parseInt(childrenChange);
               newadult = Integer.parseInt(adultChange);
               if(!checkRoomOcc(newadult + newchild, Room)){
                  System.out.println("Total guests exceeds max room occupancy!!!");
                  return;
               }
               else{
                  String new_sql = "UPDATE lab7_reservations_copy "
                  + "SET Adults = ? , Kids = ? "
                  + "WHERE CODE = ?";
                  PreparedStatement pstmt = conn.prepareStatement(new_sql);
                  pstmt.setInt(1, newadult);
                  pstmt.setInt(2, newchild);
                  pstmt.setInt(3, resvCode);
                  pstmt.executeUpdate();
                  System.out.println("Your reservation has been changed. ");
               }
            }
         }
         catch(Exception e){System.out.println(e);}
      }
      catch(Exception e){System.out.println("HERE " + e);}
   }
   
   public static void changeReservation(){
      Scanner scanner = new Scanner(System.in);
      System.out.println("What is your reservation number?");
      int resv = 0;
      try{
         resv = scanner.nextInt();
         if(!existingReservation(resv)){
            System.out.println("Reservation Number Does Not Exist!!");
            return;
         }
         getChangeInfo(resv);
      }
      catch(Exception e){
         System.out.println("Invalid Reservation Number");
         return;
      }
   }
   
   public static boolean existingReservation(int check){
      ArrayList<Integer> resv = getReservation();
      return resv.contains(check);
   }
   
   public static ArrayList<Integer> getReservation(){
      ArrayList<Integer> resv = new ArrayList<Integer>();
      try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                                                         System.getenv("HP_JDBC_USER"),
                                                         System.getenv("HP_JDBC_PW"))) {
         String sql = "SELECT DISTINCT CODE FROM lab7_reservations_copy;";
         try (PreparedStatement stmt = conn.prepareStatement(sql);
              ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()){
               int rc = rs.getInt("CODE");
               resv.add(rc);
            }
         }
         catch(Exception e){System.out.println(e);}
      }
      catch(Exception e){System.out.println("HERE " + e);}
      return resv;
   }

      // Outputs info for R1
   public static void roomsAndRates() {
      try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                                                         System.getenv("HP_JDBC_USER"),
                                                         System.getenv("HP_JDBC_PW"))) {
         String sql = "SELECT F3.roomcode, F3.roomname, F3.beds, F3.bedtype, F3.maxocc, F3.baseprice, F3.decor, " +
                      "F2.PopularityScore AS 'Popularity', F1.checkout, F1.length " +
                      "FROM (" +
                         "SELECT lab7_reservations_copy.Room, " + 
                                 "DATEDIFF(lab7_reservations_copy.checkout, lab7_reservations_copy.checkin) AS Length, " + 
                                 "lab7_reservations_copy.Checkout " +
                         "FROM lab7_reservations_copy " +
                         "WHERE (lab7_reservations_copy.Room, lab7_reservations_copy.checkout) IN (" +
                            "SELECT lab7_reservations_copy.Room, MAX(lab7_reservations_copy.checkout) AS lastCheckOutDate " +
                            "FROM lab7_reservations_copy " +
                            "WHERE lab7_reservations_copy.checkout <= CURDATE() " +
                            "GROUP BY lab7_reservations_copy.Room) " + 
                      ") AS F1 " + 
                      "JOIN (" +
                         "SELECT lab7_reservations_copy.Room, " +
                         "ROUND(SUM(DATEDIFF(lab7_reservations_copy.checkout, lab7_reservations_copy.checkin))/180,2) AS PopularityScore " +
                         "FROM lab7_reservations_copy " +
                         "WHERE CheckIn BETWEEN DATE_SUB(NOW(), INTERVAL 180 DAY) AND NOW() " +
                         "GROUP BY lab7_reservations_copy.Room " +
                      ") AS F2 " +
                      "ON F1.Room = F2.Room " +
                      "JOIN (SELECT * FROM lab7_rooms) AS F3 ON F2.room = F3.roomcode " +
                      "ORDER BY F2.popularityscore DESC;";

         // Step 4: Send SQL statement to DBMS
         System.out.println("Code |           Name           | Beds | Bed Type | Occ |  Price  |    Decor    | Score | Last CheckOut | Days stay | Next Available");
         System.out.println("-----|--------------------------|------|----------|-----|---------|-------------|-------|---------------|-----------|---------------");
         try (PreparedStatement stmt = conn.prepareStatement(sql);
              ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()){
               String roomcode = rs.getString("RoomCode");
               String roomname = rs.getString("RoomName");
               int beds = rs.getInt("Beds");
               String bedType = rs.getString("bedType");
               int maxOcc = rs.getInt("maxOcc");
               float basePrice = rs.getFloat("basePrice");
               String decor = rs.getString("decor");
               float popularity = rs.getFloat("popularity");
               String lastCheckOut = rs.getString("checkout");
               int lastStayLength = rs.getInt("length");
               String nextAvailableCheckIn = getNextAvailableCheckIn(roomcode, "");
               System.out.format("%4s | %24s | %4d | %8s | %3d | $%6.2f | %11s | %5.2f | %13s | %9d | %10s %n",
                  roomcode, roomname, beds, bedType, maxOcc, basePrice, decor, popularity, lastCheckOut, lastStayLength, nextAvailableCheckIn);
            }
         }
         catch(Exception e){System.out.println(e);}
      }
      catch(Exception e){System.out.println("HERE " + e);}
   }

   public static ArrayList<String> getCheckInDates(String room) {
      ArrayList<String> checkInDates = new ArrayList<String>();

      try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                                                         System.getenv("HP_JDBC_USER"),
                                                         System.getenv("HP_JDBC_PW"))) {
         String sql = "SELECT checkIn FROM lab7_reservations_copy WHERE lab7_reservations_copy.Room = '" + room + "' ORDER BY checkIn;";

         try (PreparedStatement stmt = conn.prepareStatement(sql);
              ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()) {
               String in = rs.getString("checkIn");
               checkInDates.add(in);
            }
         }
         catch(Exception e){System.out.println(e);}
      }
      catch(Exception e){System.out.println("HERE " + e);}

      return checkInDates;
   }

   public static ArrayList<String> getCheckOutDates(String room) {
      ArrayList<String> checkOutDates = new ArrayList<String>();

      try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                                                         System.getenv("HP_JDBC_USER"),
                                                         System.getenv("HP_JDBC_PW"))) {
         String sql = "SELECT checkOut FROM lab7_reservations_copy WHERE lab7_reservations_copy.Room = '" + room + "' ORDER BY checkIn;";

         try (PreparedStatement stmt = conn.prepareStatement(sql);
              ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()) {
               String out = rs.getString("checkOut");
               checkOutDates.add(out);
            }
         }
         catch(Exception e){System.out.println(e);}
      }
      catch(Exception e){System.out.println("HERE " + e);}

      return checkOutDates;
   }

   public static String getNextAvailableCheckIn(String room, String date) {
      // System.out.println("getNextAvailableCheckIn " + room);

      ArrayList<String> checkInDates = getCheckInDates(room);
      ArrayList<String> checkOutDates = getCheckOutDates(room);

      try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                                                         System.getenv("HP_JDBC_USER"),
                                                         System.getenv("HP_JDBC_PW"))) {
         String sql = "";
         
         if (date.equals("")) {
            sql = "SELECT t1.room, t1.checkIn, t1.checkOut " +
               "FROM (" + 
                  "SELECT lab7_reservations_copy.room, lab7_reservations_copy.checkIn, lab7_reservations_copy.checkOut, MAX(lab7_reservations_copy.checkIn) OVER (PARTITION BY lab7_reservations_copy.room) AS max_checkin " +
                  "FROM lab7_reservations_copy " +
                  // "WHERE lab7_reservations_copy.room = '" + room + "' AND lab7_reservations_copy.checkIn <= '2019-06-13'" +
                  "WHERE lab7_reservations_copy.room = '" + room + "' AND lab7_reservations_copy.checkIn <= CURDATE()" +
                  ") AS t1 " + 
               "WHERE t1.checkIn = t1.max_checkin;";
         }
         else {
            sql = "SELECT t1.room, t1.checkIn, t1.checkOut " +
               "FROM (" + 
                  "SELECT lab7_reservations_copy.room, lab7_reservations_copy.checkIn, lab7_reservations_copy.checkOut, MAX(lab7_reservations_copy.checkIn) OVER (PARTITION BY lab7_reservations_copy.room) AS max_checkin " +
                  "FROM lab7_reservations_copy " +
                  "WHERE lab7_reservations_copy.room = '" + room + "' AND lab7_reservations_copy.checkIn <= '" + date + "'" +
                  ") AS t1 " + 
               "WHERE t1.checkIn = t1.max_checkin;";
         }
         
         try (PreparedStatement stmt = conn.prepareStatement(sql);
              ResultSet rs = stmt.executeQuery(sql)) {
            if(rs.next()) {
               String sqlCheckIn = rs.getString("checkIn");
               String sqlCheckOut = rs.getString("checkOut");

               SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
               Date currDate = sdf.parse(sdf.format(new Date()));
               // Date currDate = sdf.parse("2019-06-13");
               // System.out.println("strCurrDate " + sdf.format(currDate));
               Date sqlCheckOutDate = sdf.parse(sqlCheckOut);
               // System.out.println("sqlCheckOut " + sqlCheckOut);

               int index = 0;
               // currDate before sqlCheckOut
               if (currDate.compareTo(sqlCheckOutDate) < 0) { 
                  // System.out.println("currDate " + sdf.format(currDate) + " before sqlCheckOut " + sqlCheckOut);
                  while (checkInDates.contains(sqlCheckOut)) {
                     // System.out.println("checkInDates contains sqlCheckOut " + sqlCheckOut);
                     index = checkInDates.indexOf(sqlCheckOut);
                     sqlCheckOut = checkOutDates.get(index);
                     // System.out.println("updated sqlCheckOut " + sqlCheckOut);
                  }
                  // System.out.println("Next available checkIn date: " + sqlCheckOut);
                  return sqlCheckOut;
               }
               else { // currDate on/after sqlCheckOut
                  // System.out.println("currDate on/after sqlCheckOut");
                  // System.out.println("Next available checkIn date: " + sdf.format(currDate));
                  return sdf.format(currDate);
               }
            }
         }
         catch(Exception e){System.out.println(e);}
      }
      catch(Exception e){System.out.println("HERE " + e);}

      return "";
   }
   
   // Selects the entire table from the SQL DB
   public static void selectAll(){
      // Step 1: Establish connection to RDBMS
      try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                                                         System.getenv("HP_JDBC_USER"),
                                                         System.getenv("HP_JDBC_PW"))) {
         String sql = "SELECT * FROM lab7_rooms";
         // Step 4: Send SQL statement to DBMS
         try (PreparedStatement stmt = conn.prepareStatement(sql);
              ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()){
               String RoomCode = rs.getString("RoomCode");
               String RoomName = rs.getString("RoomName");
               int Beds = rs.getInt("Beds");
               String bedType = rs.getString("bedType");
               int maxOcc = rs.getInt("maxOcc");
               float basePrice = rs.getFloat("basePrice");
               String decor = rs.getString("decor");
               System.out.format("%s | %s | %d | %s | %d | $%.2f | %s | %n", RoomCode, RoomName, Beds, bedType, maxOcc, basePrice, decor);
            }
         }
         catch(Exception e){System.out.println(e);}
      }
      catch(Exception e){System.out.println("HERE " + e);}
   }
   
   // Get all the information making a reservation
   public static void gatherReservation(){
      Scanner scanner = new Scanner(System.in);
      System.out.println("What is your first name?");
      String firstname = scanner.nextLine().toUpperCase();
      System.out.println("What is your last name?");
      String lastname = scanner.nextLine().toUpperCase();
      System.out.println("What is room code of your desired room?");
      String room = scanner.nextLine().toUpperCase();
      if(!checkRoomCode(room)){
         System.out.println("Roomcode not Found!!!");
         return;
      }
      System.out.println("What kind of bed would you like?");
      String bedType = scanner.nextLine().toLowerCase();
      if(!checkBedType(bedType)){
         System.out.println("Bed Type not Found!!!");
         return;
      }
      System.out.println("What is the check-in date? (YYYY-MM-DD)");
      String checkIn = scanner.nextLine();
      if(!checkCheckIn(checkIn)){
         System.out.println("Invalid Checkin date, must be after today's date!!!");
         return;
      }
      System.out.println("What is the check-out date? (YYYY-MM-DD)");
      String checkOut = scanner.nextLine();
      if(!checkCheckOut(checkIn, checkOut)){
         System.out.println("Invalid Checkout date, must be after check in date!!!");
         return;
      }
      System.out.println("# of Children?");
      int children = 0;
      try{
         children = scanner.nextInt();
      }
      catch(Exception e){
         System.out.println("Not a valid number!!");
         return;
      }
      System.out.println("# of Adult?");
      int adult = 0;
      try{
         adult = scanner.nextInt();
      }
      catch(Exception e){
         System.out.println("Not a valid number!!");
         return;
      }
      if(!checkMaxOcc(adult, children)){
         System.out.println("Total guests exceeds max room occupancy!!!");
         return;
      }
      // goes in here if room is not available or room = any
      if(!checkDateAvailability(-1, room, checkIn, checkOut)){
         if(!room.equalsIgnoreCase("any")){
            System.out.println("Room not available during these dates!");
         }
         makeSuggestions(firstname, lastname, checkIn, checkOut, adult, children);
         return;
      }
      else{
         String originalBed = getBedTypes(room);
         if(bedType.equalsIgnoreCase("any")){
            makeSuggestions(firstname, lastname, checkIn, checkOut, adult, children);
            return;
         }
         if(originalBed.equalsIgnoreCase(bedType)){
            if(checkRoomOcc(children + adult, room)){
               System.out.println("Booking Your Reseravtion Right Now...");
               int base = priceSearch(room);
               makeReservation(firstname, lastname, room, originalBed, checkIn, checkOut, adult, children, base);
            }
            else{
               System.out.println("Available but can't book, exceed " + room + " max capacity.");
               makeSuggestions(firstname, lastname, checkIn, checkOut, adult, children);
               return;
            }
         }
         else{
            System.out.println(room + " is available but bedType not match.");
            makeSuggestions(firstname, lastname, checkIn, checkOut, adult, children);
            return;
         }
      }
   }
   
   public static void makeReservation(String firstname, String lastname, String room, String bedType, String checkIn, String checkOut, int adult, int children, int rate){
      int resCode = getReservationCode();
      String roomName = "";
      
      try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                                                         System.getenv("HP_JDBC_USER"),
                                                         System.getenv("HP_JDBC_PW"))) {
         String sql = "SELECT RoomName FROM lab7_rooms WHERE RoomCode = '" + room +"';";
         try (PreparedStatement stmt = conn.prepareStatement(sql);
              ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()){
               roomName = rs.getString("RoomName");
            }
         }
         catch(Exception e){System.out.println(e);}
         
         System.out.println("\n-------------------------");
         System.out.println("Reservation Confirmation");
         System.out.println("-------------------------");
         System.out.println("Confirmation #: " + resCode);
         System.out.println("Name: " + firstname + " " + lastname);
         System.out.println("Room Code: " + room);
         System.out.println("Room Name: " + roomName);
         System.out.println("Bed Type: " + bedType);
         System.out.println("Dates: " + checkIn + " - " + checkOut);
         System.out.println("# of Adults: " + adult);
         System.out.println("# of Children: " + children);
         System.out.printf("Total cost of stay: %.2f\n\n", getTotalCost(checkIn, checkOut, rate));
         
         Scanner scanner = new Scanner(System.in);
         System.out.println("Would you like to make this reservation? (yes/no)");
         String confirm = scanner.nextLine();
         
         if(confirm.equalsIgnoreCase("yes")){
            System.out.println("Making Reservation... ");
            try {
               String new_sql = "INSERT INTO lab7_reservations_copy (CODE, Room, CheckIn, Checkout, Rate, LastName, FirstName, Adults, Kids) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
               PreparedStatement pstmt = conn.prepareStatement(new_sql);
               pstmt.setInt(1, resCode);
               pstmt.setString(2, room);
               pstmt.setString(3, checkIn);
               pstmt.setString(4, checkOut);
               pstmt.setInt(5, rate);
               pstmt.setString(6, lastname);
               pstmt.setString(7, firstname);
               pstmt.setInt(8, adult);
               pstmt.setInt(9, children);
               pstmt.executeUpdate();
            }
            catch(Exception e){System.out.println("HERE " + e);}
         }
      }
      catch(Exception e){System.out.println("makeReservation " + e);}
   }

   public static double getTotalCost(String checkIn, String checkOut, int rate) {
      double cost = 0;
      int weekends = 0;
      int weekdays = 0;

      LocalDate start = LocalDate.parse(checkIn);
      LocalDate end = LocalDate.parse(checkOut);

      while (start.compareTo(end) != 0) {
         if (start.getDayOfWeek() == DayOfWeek.SATURDAY ||
             start.getDayOfWeek() == DayOfWeek.SUNDAY) {
            weekends++;
         }
         else {
            weekdays++;
         }

         start = start.plusDays(1);
      }

      cost = (weekdays * rate) + (1.1 * weekends * rate);
      cost *= 1.18;
      return cost;
   }
   
   public static String getBedTypes(String roomcode){
      String originalBed = "";
      try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                                                         System.getenv("HP_JDBC_USER"),
                                                         System.getenv("HP_JDBC_PW"))) {
         String sql = "SELECT bedType FROM lab7_rooms WHERE RoomCode = '" + roomcode +"';";
         try (PreparedStatement stmt = conn.prepareStatement(sql);
              ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()){
               originalBed = rs.getString("bedType");
            }
         }
         catch(Exception e){System.out.println(e);}
      }
      catch(Exception e){System.out.println("HERE " + e);}
      return originalBed;
   }
   
   public static boolean checkRoomOcc(int input, String roomcode){
      int maxOcc = 0;
      try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                                                         System.getenv("HP_JDBC_USER"),
                                                         System.getenv("HP_JDBC_PW"))) {
         String sql = "SELECT maxOcc FROM lab7_rooms WHERE RoomCode = '" + roomcode +"';";
         try (PreparedStatement stmt = conn.prepareStatement(sql);
              ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()){
               maxOcc = rs.getInt("maxOcc");
            }
         }
         catch(Exception e){System.out.println(e);}
      }
      catch(Exception e){System.out.println("HERE " + e);}
      return input <= maxOcc;
   }
   
   public static long dateDiff(Date checkin, Date checkout) {
      long difference =  (checkout.getTime() - checkin.getTime())/86400000;
      return Math.abs(difference);
   }
   
   public static void makeSuggestions(String firstname, String lastname, String checkin, String checkout, int adult, int children){
      long datedifference = 0;
      try{
         Date checkinDate = new SimpleDateFormat("yyyy-MM-dd").parse(checkin);
         Date checkoutDate = new SimpleDateFormat("yyyy-MM-dd").parse(checkout);
         datedifference = dateDiff(checkinDate, checkoutDate);
      }
      catch(Exception e){
         System.out.println("Can't parse dates");
      }
      System.out.println("Here are some suggestions: ");
      int total = adult+children;
      Scanner scanner = new Scanner(System.in);
      int count = 0;
      ArrayList <String> listOfRooms = getRooms();
      ArrayList<Integer> listOfMax = getMaxOcc();
      ArrayList<String> listOfBeds = getBeds();
      ArrayList<Integer> listOfPrice = getBasePrice();
      String roomqueue [] = new String[5];
      String bedqueue [] = new String[5];
      String checkinqueue [] = new String[5];
      String checkoutqueue [] = new String[5];
      System.out.println("#  | Room |  Bed   | Check - In | Check Out  | Occ |");
      try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                                                         System.getenv("HP_JDBC_USER"),
                                                         System.getenv("HP_JDBC_PW"))) {
         // Step 4: Send SQL statement to DBMS
//         String new_sql = "DELETE FROM lab7_reservations_copy WHERE CODE = ?";
//         PreparedStatement pstmt = conn.prepareStatement(new_sql);
//         pstmt.setInt(1, code);
//         pstmt.executeUpdate();
         for(int i = 0; i < listOfMax.size(); i++){
            if(listOfMax.get(i) >= total){
               if(checkDateAvailability(-1, listOfRooms.get(i), checkin, checkout)){
                  if(count < 5){
                     
                     roomqueue[count] = listOfRooms.get(i);
                     bedqueue[count] = listOfBeds.get(i);
                     checkinqueue[count] = checkin;
                     checkoutqueue[count] = checkout;
                     
                     System.out.format("%d. | %4s | %6s | %10s | %10s | %3d | %n", count+1, listOfRooms.get(i), listOfBeds.get(i), checkin, checkout, listOfMax.get(i));
                     count++;
                  }
               }
               else{
                  if(count < 5){
                     String nextcheckin = getNextAvailableCheckIn(listOfRooms.get(i), checkin);
                     String nextcheckout = getLastAvailableCheckOut(listOfRooms.get(i), nextcheckin);
                     if(nextcheckout.equalsIgnoreCase("")){
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar c = Calendar.getInstance();
                        try{
                           c.setTime(sdf.parse(nextcheckin));
                        }
                        catch(Exception e){
                           e.printStackTrace();
                        }
                        c.add(Calendar.DAY_OF_MONTH, (int)datedifference);
                        nextcheckout = sdf.format(c.getTime());
                     }
                     roomqueue[count] = listOfRooms.get(i);
                     bedqueue[count] = listOfBeds.get(i);
                     checkinqueue[count] = nextcheckin;
                     checkoutqueue[count] = nextcheckout;
                     
                     System.out.format("%d. | %4s | %6s | %10s | %10s | %3d | %n", count+1, listOfRooms.get(i), listOfBeds.get(i), nextcheckin, nextcheckout, listOfMax.get(i));
                     count++;
                  }
               }
            }
         }
      }
      catch(Exception e){
         System.out.println("HERE 6.cancel spot" + e);
      }
      System.out.println("6. Cancel!");
      System.out.println("Please select the number you would like to book.");
      try{
         int choice = scanner.nextInt();
         if (choice == 6) {
            System.out.println("Cancelling...");
            return;
         }
         if(choice > 6){
            System.out.println("Invalid Choice!!");
            return;
         }
         int rate = priceSearch(roomqueue[choice-1]);
         makeReservation(firstname, lastname, roomqueue[choice-1], bedqueue[choice-1], checkinqueue[choice-1], checkoutqueue[choice-1], adult, children, rate);
      }
      catch(Exception e){
         System.out.println("Invalid Choice!");
         return;
      }
   }
   
   public static boolean checkMaxOcc(int adult, int children){
      int total = adult + children;
      int maxOcc = 0;
      try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                                                         System.getenv("HP_JDBC_USER"),
                                                         System.getenv("HP_JDBC_PW"))) {
         String sql = "SELECT MAX(maxOcc) AS OCC FROM lab7_rooms;";
         try (PreparedStatement stmt = conn.prepareStatement(sql);
              ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()){
               maxOcc = rs.getInt("OCC");
            }
         }
         catch(Exception e){System.out.println(e);}
      }
      catch(Exception e){System.out.println("HERE " + e);}
      return maxOcc >= total;
   }
   
   public static int getReservationCode(){
      int code = 0;
      try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                                                         System.getenv("HP_JDBC_USER"),
                                                         System.getenv("HP_JDBC_PW"))) {
         String sql = "SELECT MAX(CODE) AS CODE FROM lab7_reservations_copy;";
         try (PreparedStatement stmt = conn.prepareStatement(sql);
              ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()){
               code = rs.getInt("CODE");
            }
         }
         catch(Exception e){System.out.println(e);}
      }
      catch(Exception e){System.out.println("HERE " + e);}
      return code+1;
   }
   
   // checks to make sure checkin date is valid
   public static boolean checkCheckIn(String input){
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      try{
         Date currDate = sdf.parse(sdf.format(new Date()));
         Date inputDate = new SimpleDateFormat("yyyy-MM-dd").parse(input);
         if(inputDate.before(currDate)){
            return false;
         }
         return true;
      }
      catch(Exception e){
         System.out.println("Not a valid date format!");
         return false;
      }
   }
   
   // checks to make sure checkout date is after checkin.
   public static boolean checkCheckOut(String checkin, String checkout){
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      try{
         Date in = new SimpleDateFormat("yyyy-MM-dd").parse(checkin);
         Date out = new SimpleDateFormat("yyyy-MM-dd").parse(checkout);
         if(out.before(in)){
            return false;
         }
         return true;
      }
      catch(Exception e){
         System.out.println("Not a valid date format!");
         return false;
      }
   }
   
   // checks whether or not input bedtype is valid
   public static boolean checkBedType(String bed){
      if(bed.equals("any")){
         return true;
      }
      return getBedTypes().contains(bed);
   }
   
   // Get the bed types of all the beds and return an arraylist containing all of them
   public static ArrayList<String> getBedTypes(){
      ArrayList<String> bedTypes = new ArrayList<String>();
      try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                                                         System.getenv("HP_JDBC_USER"),
                                                         System.getenv("HP_JDBC_PW"))) {
         String sql = "SELECT DISTINCT bedType FROM lab7_rooms;";
         try (PreparedStatement stmt = conn.prepareStatement(sql);
              ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()){
               String rc = rs.getString("bedType").toLowerCase();
               bedTypes.add(rc);
            }
         }
         catch(Exception e){System.out.println(e);}
      }
      catch(Exception e){System.out.println("HERE " + e);}
      return bedTypes;
   }
   
   // checks whether or not input roomcode is valid
   public static boolean checkRoomCode(String room){
      if(room.equals("ANY")){
         return true;
      }
      return getRooms().contains(room);
   }
   
   public static ArrayList<String> getBeds(){
      ArrayList<String> beds = new ArrayList<String>();
      try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                                                         System.getenv("HP_JDBC_USER"),
                                                         System.getenv("HP_JDBC_PW"))) {
         String sql = "SELECT bedType FROM lab7_rooms ORDER BY roomcode;";
         try (PreparedStatement stmt = conn.prepareStatement(sql);
              ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()){
               String rc = rs.getString("bedType");
               beds.add(rc);
            }
         }
         catch(Exception e){System.out.println(e);}
      }
      catch(Exception e){System.out.println("HERE " + e);}
      return beds;
   }
   
   public static ArrayList<Integer> getBasePrice(){
      ArrayList<Integer> price = new ArrayList<Integer>();
      try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                                                         System.getenv("HP_JDBC_USER"),
                                                         System.getenv("HP_JDBC_PW"))) {
         String sql = "SELECT basePrice FROM lab7_rooms ORDER BY roomcode;";
         try (PreparedStatement stmt = conn.prepareStatement(sql);
              ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()){
               int rc = rs.getInt("basePrice");
               price.add(rc);
            }
         }
         catch(Exception e){System.out.println(e);}
      }
      catch(Exception e){System.out.println("HERE " + e);}
      return price;
   }
   
   public static int priceSearch(String roomcode){
      int price = 0;
      try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                                                         System.getenv("HP_JDBC_USER"),
                                                         System.getenv("HP_JDBC_PW"))) {
         String sql = "SELECT basePrice FROM lab7_rooms WHERE RoomCode = '" + roomcode + "';";
         try (PreparedStatement stmt = conn.prepareStatement(sql);
              ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()){
               price = rs.getInt("basePrice");
            }
         }
         catch(Exception e){System.out.println("pS " + e);}
      }
      catch(Exception e){System.out.println("price Search " + e);}
      return price;
   }
   
   public static ArrayList<Integer> getMaxOcc(){
      ArrayList<Integer> occ = new ArrayList<Integer>();
      try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                                                         System.getenv("HP_JDBC_USER"),
                                                         System.getenv("HP_JDBC_PW"))) {
         String sql = "SELECT maxOcc FROM lab7_rooms ORDER BY roomcode;";
         try (PreparedStatement stmt = conn.prepareStatement(sql);
              ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()){
               int rc = rs.getInt("maxOcc");
               occ.add(rc);
            }
         }
         catch(Exception e){System.out.println(e);}
      }
      catch(Exception e){System.out.println("HERE " + e);}
      return occ;
   }
   
   // Get the codes of all the rooms and return an arraylist containing all of them
   public static ArrayList<String> getRooms(){
      ArrayList<String> roomCodes = new ArrayList<String>();
      try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                                                         System.getenv("HP_JDBC_USER"),
                                                         System.getenv("HP_JDBC_PW"))) {
         String sql = "SELECT roomcode FROM lab7_rooms ORDER BY roomcode;";
         try (PreparedStatement stmt = conn.prepareStatement(sql);
              ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()){
               String rc = rs.getString("RoomCode");
               roomCodes.add(rc);
            }
         }
         catch(Exception e){System.out.println(e);}
      }
      catch(Exception e){System.out.println("HERE " + e);}
      return roomCodes;
   }
   
   public static boolean checkDateAvailability(int code, String room, String checkIn, String checkOut) {
      ArrayList<String> checkInDates = new ArrayList<String>();
      ArrayList<String> checkOutDates = new ArrayList<String>();
      
      try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                                                         System.getenv("HP_JDBC_USER"),
                                                         System.getenv("HP_JDBC_PW"))) {
         String sql = "";
         if(code == -1){
            sql = "SELECT checkIn, checkOut FROM lab7_reservations_copy WHERE lab7_reservations_copy.Room = '" + room + "' ORDER BY checkIn;";
         }
         else{
            sql = "SELECT checkIn, checkOut FROM lab7_reservations_copy WHERE lab7_reservations_copy.Room = '" + room + "' AND CODE <> " + code + " ORDER BY checkIn;";
         }
         
         try (PreparedStatement stmt = conn.prepareStatement(sql);
              ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()) {
               String in = rs.getString("checkIn");
               checkInDates.add(in);
               String out = rs.getString("checkOut");
               checkOutDates.add(out);
            }
         }
         catch(Exception e){System.out.println(e);}
         
         if(code == -1){
            sql = "SELECT t1.room, t1.checkIn, t1.checkOut " +
                  "FROM (" +
                  "SELECT lab7_reservations_copy.room, lab7_reservations_copy.checkIn, lab7_reservations_copy.checkOut, MAX(lab7_reservations_copy.checkIn) OVER (PARTITION BY lab7_reservations_copy.room) AS max_checkin " +
                  "FROM lab7_reservations_copy " +
                  "WHERE lab7_reservations_copy.room = '" + room + "' AND lab7_reservations_copy.checkIn <= '" + checkIn + "'" +
                  ") AS t1 " +
                  "WHERE t1.checkIn = t1.max_checkin;";
         }
         else{
            sql = "SELECT t1.room, t1.checkIn, t1.checkOut " +
            "FROM (" +
            "SELECT lab7_reservations_copy.room, lab7_reservations_copy.checkIn, lab7_reservations_copy.checkOut, MAX(lab7_reservations_copy.checkIn) OVER (PARTITION BY lab7_reservations_copy.room) AS max_checkin " +
            "FROM lab7_reservations_copy " +
            "WHERE lab7_reservations_copy.room = '" + room + "' AND CODE <> " + code + " AND lab7_reservations_copy.checkIn <= '" + checkIn + "'" +
            ") AS t1 " +
            "WHERE t1.checkIn = t1.max_checkin;";
         }
         
         try (PreparedStatement stmt = conn.prepareStatement(sql);
              ResultSet rs = stmt.executeQuery(sql)) {
            if(rs.next()) {
               String sqlCheckIn = rs.getString("checkIn");
               String sqlCheckOut = rs.getString("checkOut");
               
               SimpleDateFormat sdfo = new SimpleDateFormat("yyyy-MM-dd");
               Date inputCheckInDate = sdfo.parse(checkIn);
               Date sqlCheckOutDate = sdfo.parse(sqlCheckOut);
               
               // check checkIn date
               if (inputCheckInDate.compareTo(sqlCheckOutDate) < 0) {
                  // System.out.println("inputCheckInDate beore sqlCheckOutDate");
                  //System.out.println("Those dates are not available :(");
                  return false;
               }
               else {
                  if (checkInDates.contains(checkIn)) {
                     // System.out.println("checkInDates contains checkIn " + checkIn);
                     //System.out.println("Those dates are not available :(");
                     return false;
                  }
                  else {
                     // check checkOut date
                     int curIndex = checkInDates.indexOf(sqlCheckIn);
                     if (curIndex + 1 == checkInDates.size()) {
                        return true;
                     }
                     String nextCheckIn = checkInDates.get(curIndex + 1);
                     Date nextCheckInDate = sdfo.parse(nextCheckIn);
                     Date inputCheckOutDate = sdfo.parse(checkOut);
                     
                     if (inputCheckOutDate.compareTo(nextCheckInDate) < 0 ||
                         inputCheckOutDate.compareTo(nextCheckInDate) == 0) {
                        //System.out.println("Those dates are available :)");
                        return true;
                     }
                     else {
                        // System.out.println("inputCheckOutDate after nextCheckIn");
                        //System.out.println("Those dates are not available :(");
                        return false;
                     }
                  }
               }
            }
         }
         catch(Exception e) {System.out.println(":( " + e);}
      }
      catch(Exception e){System.out.println("HERE " + e);}
      
      //System.out.println("Those dates are not available :(");
      return false;
   }

   // Returns last available check out date, based on parameter checkIn
   // If there are no existing reservations after the inputted checkIn date, returns empty string ""
   public static String getLastAvailableCheckOut(String room, String checkIn) {
      // System.out.println("getLastAvailableCheckOut " + room + " " + checkIn);
      
      ArrayList<String> checkInDates = getCheckInDates(room);
      ArrayList<String> checkOutDates = getCheckOutDates(room);
      int size = checkInDates.size();
      Date inputCheckInDate;
      
      try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                                                         System.getenv("HP_JDBC_USER"),
                                                         System.getenv("HP_JDBC_PW"))) {
         // find what index new checkIn (parameter) would be at in checkInDates
         inputCheckInDate = new SimpleDateFormat("yyyy-MM-dd").parse(checkIn);
         String curCheckIn = "";
         Date curCheckInDate;
         int i = 0;
         
         while (true) {
            curCheckIn = checkInDates.get(i);
            curCheckInDate = new SimpleDateFormat("yyyy-MM-dd").parse(curCheckIn);
            if (inputCheckInDate.compareTo(curCheckInDate) > 0) {
               i++;
               if (i == size) {
                  // System.out.println("i == size, reached end of array, i " + i);
                  // System.out.println("curCheckIn " + curCheckIn);
                  return "";
               }
            }
            else {
               // System.out.println("curCheckIn " + curCheckIn);
               // System.out.println("i " + i);
               return curCheckIn;
            }
         }
      }
      catch(Exception e){System.out.println("HERE " + e);}
      
      return "";
   }
   
   // R6
   public static void printRevenues() {
      System.out.println(" Code |  Jan  |  Feb  |  Mar  |  Apr  |  May  |  Jun  |  Jul  |  Aug  |  Sep  |  Oct  |  Nov  |  Dec  | Total  |");
      System.out.println("------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|--------|");
      ArrayList<String> rooms = getRooms();

      int jan = 0;
      int feb = 0;
      int mar = 0;
      int apr = 0;
      int may = 0;
      int jun = 0;
      int jul = 0;
      int aug = 0;
      int sep = 0;
      int oct = 0;
      int nov = 0;
      int dec = 0;
      int tot = 0;

      int i = 0;
      for (i = 0; i < rooms.size(); i++) {
         try (Connection conn = DriverManager.getConnection(System.getenv("HP_JDBC_URL"),
                                                         System.getenv("HP_JDBC_USER"),
                                                         System.getenv("HP_JDBC_PW"))) {
            String sql = "SELECT MONTHNAME(re.checkin) as 'Month', COUNT(*) AS 'NReservations', " +
                           "ROUND(SUM(re.rate * DATEDIFF(re.checkout, re.checkin)),0) AS 'MonthlyRevenue' " +
                           "FROM lab7_rooms rm, lab7_reservations_copy re " + 
                           "WHERE rm.roomcode = re.room AND re.room = '" + rooms.get(i) + "' " + 
                           "GROUP BY Month " + 
                           "ORDER BY FIELD(Month, 'January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December')";

            System.out.print(" " + rooms.get(i) + "  | ");

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery(sql)) {
               int rev = 0;
               int totalMonth = 0;
               int index = 0;
               while(rs.next()){
                  index++;
                  rev = rs.getInt("MonthlyRevenue");
                  System.out.format("%5d | ", rev);
                  totalMonth += rev;
                  if (index == 1)  { jan += rev; }
                  if (index == 2)  { feb += rev; }
                  if (index == 3)  { mar += rev; }
                  if (index == 4)  { apr += rev; }
                  if (index == 5)  { may += rev; }
                  if (index == 6)  { jun += rev; }
                  if (index == 7)  { jul += rev; }
                  if (index == 8)  { aug += rev; }
                  if (index == 9)  { sep += rev; }
                  if (index == 10) { oct += rev; }
                  if (index == 11) { nov += rev; }
                  if (index == 12) { dec += rev; }
               }
               System.out.format("%6d |%n", totalMonth);
               tot += totalMonth;
            }
            catch(Exception e){System.out.println(e);}
         }
         catch(Exception e){System.out.println("HERE " + e);}
      }
      System.out.format("TOTAL | %d | %d | %d | %d | %d | %d | %d | %d | %d | %d | %d | %d | %d |%n",
            jan, feb, mar, apr, may, jun, jul, aug, sep, oct, nov, dec, tot);
   }
}
