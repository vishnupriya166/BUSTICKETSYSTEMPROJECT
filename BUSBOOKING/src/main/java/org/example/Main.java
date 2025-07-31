package org.example;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.List;


public class Main{

    public void showAvailableBuses(){

        try{
            Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "yournewpassword");
            Statement st= con.createStatement();

            ResultSet rs=st.executeQuery("SELECT * FROM bus");
            while(rs.next()){
                System.out.println("BUS NO: "+rs.getString("bus_no")
                +"\n BUS NAME: "+rs.getString("bus_name")
                +"\n FROM POINT: "+rs.getString("from_location")
                +"\n TO POINT: "+rs.getString("to_location")
                +"\n FROM TIME: "+rs.getString("from_time")
                +"\n DESTINATION TIME: "+rs.getString("destination_time")
                +"\n BUS FARE: "+rs.getFloat("bus_fare"));


            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void ticketBooking(){
        try{
            Connection con= DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres","yournewpassword");
            Statement st = con.createStatement();
            Scanner input = new Scanner(System.in);
            System.out.print("Enter Pickup Point: ");
            String src=input.nextLine();
            System.out.print("Enter Drop Point: ");
            String dest=input.nextLine();
            String q1="SELECT * FROM bus where from_location='"+src+"' and to_location='"+dest+"'";
            PreparedStatement pst1=con.prepareStatement(q1);
            ResultSet rs=pst1.executeQuery();
            while(rs.next()){
                System.out.println("\n------Buses Available----");
                System.out.println("Bus No: " + rs.getString("bus_no"));
                System.out.println("Bus Name: " + rs.getString("bus_name"));
                System.out.println("From Location: " + rs.getString("from_location"));
                System.out.println("To Location: " + rs.getString("to_location"));
                System.out.println("From Time: " + rs.getString("from_time"));
                System.out.println("To Time: " + rs.getString("destination_time"));
                System.out.println("Bus Fare: " + rs.getFloat("bus_fare"));


            }
            System.out.print("\nEnter Bus No that you want to book: ");
           String busNo = input.nextLine();



            PreparedStatement ps = con.prepareStatement("Select * from bus where bus_no= ?");
                ps.setString(1, busNo);
                ResultSet rs1 = ps.executeQuery();

                if (rs1.next()) {
                    System.out.println("Selected bus found");
                    System.out.println("Bus No: " + rs1.getString("bus_no"));
                    System.out.println("Bus Name: " + rs1.getString("bus_name"));
                    System.out.println("From Location: " + rs1.getString("from_location"));
                    System.out.println("To Location: " + rs1.getString("to_location"));
                    System.out.println("From Time: " + rs1.getString("from_time"));
                    System.out.println("To Time: " + rs1.getString("destination_time"));
                    System.out.println("Bus Fare: " + rs1.getFloat("bus_fare"));
                }


                System.out.println("Enter Passenger Name: ");
                String name = input.nextLine();
                System.out.println("Passenger Age's: ");
                int age = input.nextInt();
                input.nextLine();
                System.out.println("Gender(M/F): ");
                String gender = input.nextLine();
                String proof="";
                String proof_id="";
                String contact_number="";
                while(true){
                    System.out.println("Proof type for verification (PAN / AADHAAR / DRIVING LICENSE ): ");
                    proof = input.nextLine();

                    if(proof.equalsIgnoreCase("PAN")){
                        while(true)
                        {
                            System.out.println("PAN Details: ");
                            proof_id = input.nextLine();
                            if(proof_id.matches("[A-Z]{5}[0-9]{4}[A-Z]"))
                                break;
                            System.out.println("Invalid Data, Try Again");
                        }
                        break;

                    }
                    else if(proof.equalsIgnoreCase("AADHAAR")){

                        while(true)
                        {
                            System.out.println("AADHAAR Details: ");
                            proof_id = input.nextLine();
                            if(proof_id.matches("^[2-9]{1}[0-9]{11}$"))
                                break;
                            System.out.println("Invalid Data, Try Again");
                        }
                        break;
                    }
                    else if(proof.equalsIgnoreCase("LICENSE")){

                        while(true)
                        {
                            System.out.println("DRIVING LICENSE Details: ");
                            proof_id = input.nextLine();
                            if(proof_id.matches("^[A-Z]{2}[0-9]{2}[0-9]{4}[0-9]{7}$"))
                                break;
                            System.out.println("Invalid Data, Try Again");
                        }break;
                    }
                    else {
                        System.out.println("Invalid Proof, Try Again!!!");
                    }


                }
                while(true){
                    System.out.println("Enter Passenger Contact: ");
                    contact_number = input.nextLine();
                    if(contact_number.matches("[6-9][0-9]{9}"))
                        break;
                    System.out.println("Invalid Data, Try Again");
                }



                String insertUser = "INSERT INTO users (user_name, age, gender, proof, proof_id, contact) VALUES (?, ?, ?, ?, ?, ?) RETURNING user_id";
                PreparedStatement pst = con.prepareStatement(insertUser);
                pst.setString(1, name);
                pst.setInt(2, age);
                pst.setString(3, gender);
                pst.setString(4, proof);
                pst.setString(5, proof_id);
                pst.setString(6, contact_number);

                ResultSet rs2 = pst.executeQuery();
                int userID = -1;
                if (rs2.next()) {
                    userID = rs2.getInt("user_id");
                }

                if (userID != -1) {
                    Random rd = new Random();
                    String characters = "0123456789";
                    StringBuilder bookingIDBuilder = new StringBuilder("BKT");
                    for (int i = 0; i < 6; i++) {
                        bookingIDBuilder.append(characters.charAt(rd.nextInt(characters.length())));
                    }
                    String tfQuery = "Select bus_fare from bus where bus_no= ?";
                    PreparedStatement pst3 = con.prepareStatement(tfQuery);
                    pst3.setString(1,busNo);
                    ResultSet rs3 = pst3.executeQuery();
                    float fare = 0;
                    if (rs3.next()) {
                        fare = rs3.getFloat("bus_fare");
                    }


                    String bookingID = bookingIDBuilder.toString();
                    String bookSql = "INSERT INTO booking (booking_id,user_id, bus_no, status,ticket_fare) VALUES (?,?, ?, 'booked',?)";
                    PreparedStatement pst2 = con.prepareStatement(bookSql);
                    pst2.setString(1, bookingID);
                    pst2.setInt(2, userID);
                    pst2.setString(3, busNo);
                    pst2.setFloat(4, fare);
                    pst2.executeUpdate();
                    System.out.println("Booking has been done successfully!!!");


                } else {
                    System.out.println("Booking User not found");
                }



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void viewBookings(){
        try{

            Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "yournewpassword");

            String query = "SELECT * FROM booking b JOIN users u ON u.user_id = b.user_id JOIN bus bs ON b.bus_no = bs.bus_no WHERE booking_id=?";
            Scanner input = new Scanner(System.in);
            System.out.println("Enter Booking ID: ");
            String bookingNo = input.nextLine();
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, bookingNo);
            ResultSet rs=pst.executeQuery();
            while(rs.next()){
                System.out.println("BUS NO: "+rs.getString("bus_no"));
                System.out.println("BUS NAME: "+rs.getString("bus_name"));
                System.out.println("Name: "+rs.getString("user_name"));
                System.out.println("Age: "+rs.getInt("age"));
                System.out.println("Gender: "+rs.getString("gender"));
                System.out.println("From Location: "+rs.getString("from_location"));
                System.out.println("To Location: "+rs.getString("to_location"));
                System.out.println("Bus Fare: "+rs.getFloat("bus_fare"));
                System.out.println("Booking Status: "+rs.getString("status"));


            }



        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void cancelBooking(){
        try{
            Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres","postgres","yournewpassword");

            String query = "UPDATE booking SET status='cancelled' WHERE booking_id=?";
            PreparedStatement pst = con.prepareStatement(query);
            Scanner input = new Scanner(System.in);
            System.out.println("Enter Booking No: ");
            String bookingNo = input.nextLine();
            pst.setString(1, bookingNo);
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Booking cancelled successfully.");
                // Show updated status
            } else {
                System.out.println("❌ No booking found with ID: " + bookingNo);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);
        Main obj = new Main();
        System.out.println("-----Welcome to Bus Ticket Booking System-----");
        System.out.println("-----Choose What to do in the booking System-----");
        System.out.println("\n 1. View Available Buses \n 2. Ticket Booking \n 3. View Booking info and Status \n 4. Cancel Booking \n 5. Exit ");
        boolean exit = false;
        while(!exit){
            System.out.println("\nEnter your choice: ");
            int choice = input.nextInt();

            switch (choice){
                case 1:
                    obj.showAvailableBuses();
                    break;
                case 2:
                    obj.ticketBooking();
                    break;
                case 3:
                    obj.viewBookings();
                    break;
                case 4:
                    obj.cancelBooking();
                    break;
                case 5:

                    System.out.println("Thanks for booking, Have a nice day!");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice");
            }

        }
    }
}
