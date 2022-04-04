package Receptionist;
import TheConnection.DBConnection;
import java.util.*;
import java.util.Date;

import javax.swing.undo.StateEdit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.ObjectInputFilter.Status;
//databse imports
import java.sql.*;


public class Receptionist {
    private static Connection conn = null;
    private static Scanner scanner;
    private static String firstName;
    private static String lastName;
    private static String middleName;
    private static String gender;
    private static int ssn;
    private static String dateOfBirth;
    private static Integer apartmentNumber;
    private static int streetNumber;
    private static String street;
    private static String city;
    private static String province;
    private static String postalCode;
    private static String insuranceNumber;

    
    private static String email;
    private static String phonenumber;
    private static PreparedStatement preparedStatement;
    private static ResultSet resultSet;
    private static int contactInformationId;
    private static int addressId;
    private static int userId;
    private static int appointmentId;
    private static int patientId;
    private static Statement statement;
    private static String[] rooms = new String[] {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

    public Receptionist(){
        // String user = "mzjycxzivsmkni";
        // String pass = "e2de58153c0f251dc70bd1de7544284d80d0032ea323d52bf512ab5f5d93b828";
        // String LINK = "jdbc:postgresql://ec2-52-73-155-171.compute-1.amazonaws.com:5432/dc2qa16v4lv078";
        
        // try {
        //     conn = DriverManager.getConnection(LINK, user, pass);
        //     if (conn != null) {
        //         System.out.println("Connected to the database!");
        //     } else {
        //         System.out.println("Failed to make connection!");
        //     }
        
        // } catch (SQLException e) {
        //     System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
        conn = new DBConnection().conn;
    }
    //TODO Add constraints on all inputs and change scanner to using UI
    public void helper(boolean flag){
        scanner = new Scanner(System.in);
        System.out.println("Please enter the patients First Name:");
        firstName = scanner.nextLine();
        System.out.println("Please enter the patients Middle Name:");
        middleName = scanner.nextLine();
        System.out.println("Please enter the patients Last Name:");
        lastName = scanner.nextLine();
        System.out.println("Please enter the patients Email:");
        email = scanner.nextLine();
        System.out.println("Please enter the patients Phone Number:");
        phonenumber = scanner.nextLine();
        System.out.println("Please enter the patients Gender:");
        gender = scanner.nextLine();
        System.out.println("Please enter the patients SSN:");
        ssn = Integer.parseInt(scanner.nextLine());
        System.out.println("Please enter the patients Date of Birth (yyyy-mm-dd):");
        dateOfBirth = scanner.nextLine();
        System.out.println("Please enter the patients Apartment Number:");
        try{
            apartmentNumber= (Integer) Integer.parseInt(scanner.nextLine());
        } catch(NumberFormatException ex){
            apartmentNumber=null;
        }
        System.out.println("Please enter the patients Street Number:");
        streetNumber = Integer.parseInt(scanner.nextLine());
        System.out.println("Please enter the patients Street Address:");
        street = scanner.nextLine();
        System.out.println("Please enter the patients City:");
        city = scanner.nextLine();
        System.out.println("Please enter the patients Province:");
        province = scanner.nextLine();
        System.out.println("Please enter the patients Postal Code:");
        postalCode = scanner.nextLine();
        System.out.println("Please enter the patients Insurance Number:");
        insuranceNumber = scanner.nextLine();
        if (flag) insertUserInformation();
       
        return;
    }
    
    public boolean isReceptionist(int id){
        try {
            //Get contact information id 
            preparedStatement = conn.prepareStatement("SELECT employee_role FROM public.employee WHERE employee_id="+id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){ 
                if(resultSet.getString("employee_role").equals("Receptionist")){
                    return true;
                }
            }

      } catch (SQLException e) {
          e.printStackTrace();
      }
      return false;
    }
    //Insert user information into ContactInfo, Address and User Tables 
    public void insertUserInformation() {
      try {
            //Get contact information id 
            preparedStatement = conn.prepareStatement("SELECT max(contactinfo_id) FROM public.contactinformation");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) contactInformationId = resultSet.getInt("max");
            contactInformationId++;
            //Get address id 
            preparedStatement = conn.prepareStatement("SELECT max(address_id) FROM public.address");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) addressId = resultSet.getInt("max");
            addressId++;
            //Get User id 
            preparedStatement = conn.prepareStatement("SELECT max(user_id) FROM public.user");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) userId = resultSet.getInt("max");
            userId++;

            preparedStatement = conn.prepareStatement("SELECT max(patient_id) FROM public.patient");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) patientId = resultSet.getInt("max");
            patientId++;

            
           
            String contactInfo = contactInformationId + ", '"+ email + "', '"+ phonenumber + "'";
            String addressInfo = addressId + ","+ apartmentNumber + ", "+ streetNumber + ", '"+ street + "', '"+ city + "', '" + province +"', '"+ postalCode +"'" ;
            String userInfo = "'"+firstName + "', '"+ middleName+"', '"+lastName+"', '"+gender+"', "+ssn+ ", '"+dateOfBirth+"',"+contactInformationId+","+ userId+","+addressId;
            String sql1 = "INSERT INTO public.contactinformation values ("+contactInfo + ")";
            String sql2 = "INSERT INTO public.address values ("+addressInfo + ")";
            String sql3 = "INSERT INTO public.user values ("+userInfo + ")";
            String sql4 = "INSERT INTO public.Patient values ("+patientId + ", "+ userId + ", '"+ insuranceNumber+"')";
            statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.addBatch(sql1);
            statement.addBatch(sql2);
            statement.addBatch(sql3);
            statement.addBatch(sql4);
            statement.executeBatch();

      } catch (SQLException e) {
          e.printStackTrace();
      }
    }
    public void editUserInformation(){
        try {
            //Get contact information id 
            scanner = new Scanner(System.in);
            System.out.println("Please enter the SSN of the patient you would like to edit:");
            ssn = Integer.parseInt(scanner.nextLine());
            
            preparedStatement = conn.prepareStatement("SELECT * FROM public.user, public.address, public.contactinformation WHERE SSN="+ ssn+ "and public.user.address_id=public.address.address_id and public.user.contactinfo_id=public.contactinformation.contactinfo_id");
            resultSet = preparedStatement.executeQuery();
            System.out.println("------------------------------------------------------------------------------------------------------------");
            while (resultSet.next()){ 
                System.out.println("firstname: " +resultSet.getString("firstname") +",\nmiddlename: "+resultSet.getString("middlename")+ ",\nlastname: " +resultSet.getString("lastname")+ ",\ngender: " +resultSet.getString("gender") + ",\nssn: "+resultSet.getInt("ssn")+",\ndatebirth: "+ resultSet.getDate("datebirth")+
                ",\napartmentnumber: "+ resultSet.getInt("apartmentnumber")+ ",\nstreetnumber: "+resultSet.getInt("streetnumber")+ ",\nstreet: "+resultSet.getString("street")+ ",\ncity: "+resultSet.getString("city") + ",\nprovince: "+resultSet.getString("province")+ ",\npostalcode: "+resultSet.getString("postalcode") + ",\nemail: "+resultSet.getString("email")
                + ",\nphonenumber: "+resultSet.getString("phonenumber"));
                
                addressId = resultSet.getInt("address_id");
                userId = resultSet.getInt("user_id");
                contactInformationId = resultSet.getInt("contactinfo_id");
            }
            System.out.println("------------------------------------------------------------------------------------------------------------");
           
            System.out.println("Please enter the field you would like to update :");
            String field = scanner.nextLine();
            
            System.out.println("What would you like to change it to :");
            Object value;            
            if(field.equals("apartmentnumber") || field.equals("streetnumber")|| field.equals("ssn")){
                value = Integer.parseInt(scanner.nextLine());
            }else{
                value = scanner.nextLine();
            }
            
            String table ="";
            int id=0;
            String type="";
            if(field.equals("email")||field.equals("phonenumber")){
                table = "public.contactInformation";
                id = contactInformationId;
                type="contactinfo_id";
            }else if(field.equals("apartmentnumber")||field.equals("streetnumber")||field.equals("street")||field.equals("city")||field.equals("province")||field.equals("postalcode")){
                table = "public.address";
                id = addressId;
                type="address_id";
            }else if(field.equals("firstname")||field.equals("middlename")||field.equals("lastname")||field.equals("gender")||field.equals("ssn")||field.equals("datebirth")){
                table = "public.user";
                type="user_id";
                id = userId;
            }
            // preparedStatement = conn.prepareStatement("UPDATE "+ table + "SET " + field +" = " +value +" WHERE SSN = "+ssn );
            String sql = "UPDATE "+ table + " SET " + field +" = '" +value +"' WHERE " + type +" = " +id;
            statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.addBatch(sql);
            statement.executeBatch();

      } catch (SQLException e) {
          e.printStackTrace();
      }
      
    }

    public void setAppointment(){
        try {
            //Get contact information id 
            scanner = new Scanner(System.in);
            System.out.println("Please enter the Branch ID:");
            int branch = Integer.parseInt(scanner.nextLine());
            
            ArrayList<Integer> patients = new ArrayList<Integer>();
            preparedStatement = conn.prepareStatement("SELECT patient_id FROM public.appointment");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){ 
                patients.add(resultSet.getInt("patient_id"));
    
            }
            boolean flag=true;
            while(flag){
                System.out.println("Please enter the Patient ID:");
                int patientId = Integer.parseInt(scanner.nextLine());
                if(patients.contains(patientId)){
                    System.out.println("This patient already has an appointment");
                } else{
                    flag=false;
                }
            }
            
            System.out.println("Please enter the date (yyyy-mm-dd)");
            String date = scanner.nextLine();
            System.out.println("Please enter start time (in military time):");
            String startime = scanner.nextLine();
            System.out.println("Please enter end time (in military time):");
            String endtime = scanner.nextLine();

            
            preparedStatement = conn.prepareStatement("SELECT roomassigned FROM public.appointment WHERE date='"+ date+ "' and starttime='" +startime+"'");
            resultSet = preparedStatement.executeQuery();
            System.out.println("------------------------------------------------------------------------------------------------------------");
            ArrayList<String> roomsTaken = new ArrayList<String>();
            while (resultSet.next()){ 
                roomsTaken.add(resultSet.getString("roomassigned"));
            }
            System.out.println("The following rooms are available at that time:");
            for(int i=0;i<rooms.length;i++){
                if(!roomsTaken.contains(rooms[i])){
                    System.out.println(rooms[i]);
                }
            }
            flag = true;
            String room="";
            System.out.println("------------------------------------------------------------------------------------------------------------");
            while(flag){
                System.out.println("Please enter which available room you would like to book:");
                room = scanner.nextLine();
                if(Arrays.asList(rooms).contains(room) && !roomsTaken.contains(room)) flag = false;
                else{
                    System.out.println("Invalid Entry");
                }
            }
            System.out.println("------------------------------------------------------------------------------------------------------------");
            System.out.println("Are there any notes you like to add:");
            String notes = scanner.nextLine();
            flag=true;
            //get all busy dentists
            preparedStatement = conn.prepareStatement("SELECT employee_id FROM public.appointment WHERE date='"+ date+ "' and starttime='" +startime+"'");
            resultSet = preparedStatement.executeQuery();
            ArrayList<Integer> busyDentists = new ArrayList<Integer>();
            while (resultSet.next()){ 
                busyDentists.add(resultSet.getInt("employee_id"));
            }
            //get all dentists
            preparedStatement = conn.prepareStatement("SELECT employee_id FROM public.employee WHERE employee_role = 'Dentist' and branch_id="+branch);
            resultSet = preparedStatement.executeQuery();
            ArrayList<Integer> dentists = new ArrayList<Integer>();
            System.out.println("------------------------------------------------------------------------------------------------------------");
            System.out.println("The following Dentists are available:");
            while (resultSet.next()){ 
                dentists.add(resultSet.getInt("employee_id"));
                if(!busyDentists.contains(resultSet.getInt("employee_id"))) System.out.println(resultSet.getInt("employee_id"));
            }

            int dentistId=0;;
            while(flag){
                System.out.println("Please enter the employee_id of the Dentist");
                dentistId = Integer.parseInt(scanner.nextLine());
                
                if(!dentists.contains(dentistId)){
                    System.out.println("That is not a valid employee_id");
                }
                else if(busyDentists.contains(dentistId)){
                    System.out.println("This Dentist is busy at that time");
                } 
                else if(!busyDentists.contains(dentistId) && dentists.contains(dentistId)){
                    flag=false;
                }
            }
            preparedStatement = conn.prepareStatement("SELECT max(appointment_id) FROM public.appointment");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) appointmentId = resultSet.getInt("max");
            appointmentId++;
            // preparedStatement = conn.prepareStatement("UPDATE "+ table + "SET " + field +" = " +value +" WHERE SSN = "+ssn );
            String appointmentInfo = appointmentId +", '"+ date+"', '"+startime+"', '"+endtime+"',"+ null +",'" + room+"', '"+notes+"', "+patientId+", "+ dentistId;
            String sql = "INSERT INTO public.appointment values ("+appointmentInfo + ")";
            statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.addBatch(sql);
            statement.executeBatch();

      } catch (SQLException e) {
          e.printStackTrace();
      }
      
    }
    
}
