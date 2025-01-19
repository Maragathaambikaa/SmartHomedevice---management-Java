package devicemanage;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


interface devicemanagement {
    String add_device(int deviceid, int userid, String name, String type, String status, String location);
    String control_device( int deviceid, int userid, String command, String status, int response_time);
  //  void DisplayAllDevices();
}

class Device {
    private int deviceid;
    private String name;
    private String type;
    private String status;
    private String location;

    public Device(int deviceid, String name, String type, String status, String location) {
        this.deviceid=deviceid;
        this.name = name;
        this.type = type;
        this.status = status;
        this.location = location;
    }

	public int getDeviceid() {
		return deviceid;
	}
	public String getname() {
		return name;
	}
	public String gettype()
	{
		return type;
	}
	public String getstatus()
	{
		
		return status;
	}
    public String location()
    {
    	return location;
    }
     
}

class User {
    private int userid;
    private String username;
    private String email;
    private String password;

    public User(int userid, String username, String email, String password) {
        this.userid=userid;
        this.username=username;
        this.email=email;
        this.password = password;
    }

	public int getUserid() {
		return userid;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}
    public String getpassword() {
		return password;
	}
}

class Devicecommands {
    private int commandid;
    private int deviceid;
    private String command;
    private String status;
    private int response_time;

    public Devicecommands(int commandid, int deviceid, String command, String status, int response_time) {
        this.commandid=commandid;
        this.deviceid = deviceid;
        this.command = command;
        this.status = status;
        this.response_time = response_time;
    }

	public int getcommandid() {
		return commandid;
	}
	public int getresponsetime()
	{
		return response_time;
	}

}

class devicemanager implements devicemanagement {
	
//	 private ArrayList<Device> Dev = new ArrayList<>();
//	 private ArrayList<Devicecommands> com = new ArrayList<>();
	
	 
    private User user = null;
    private Connection con;

    public devicemanager(Connection con) {
        this.con = con;
    }

    public String createUserAccount(int userid, String name, String email, String password) {
        try {
            String query = "SELECT count(*) FROM user WHERE name=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, userid);
            ResultSet r = pst.executeQuery();
            r.next();
            int count = r.getInt(1);
            if (count != 0) {
                return "Account already exists";
            }
        } catch (SQLException e) {
           
        }

        try {
            String query1 = "INSERT INTO user(userid, username, email, password) VALUES(?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(query1);
            pst.setInt(1, userid);
            pst.setString(2, name);
            pst.setString(3, email);
            pst.setString(4, password);
            pst.executeUpdate();
            System.out.println("Account successfully created");
        } 
        catch (SQLException e) {
            System.out.println("Error occurred while creating account");
        }
    
        return "";
    }

    public String login(String name1, String password) {
        if (user != null) {
            return "Multiple accounts logged in";
        }

        String query = "SELECT * FROM user where username=?";
        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, name1);
            ResultSet rs = pst.executeQuery();

            if (!rs.next()) {
                return "Account does not exist";
            }

            int userid = rs.getInt("userid");
            String email = rs.getString("email");
            String storedPassword = rs.getString("password");

            if (storedPassword.equals(password)) {
                user = new User(userid, name1, email, storedPassword);
                return "Logged in successfully";
            }
        } catch (SQLException e) {
        	  System.out.println("Error ocurred");
        }
        return "Incorrect password";
    }

    public String logout() {
        if (user != null) {
            user = null;
            return "Logged out successfully";
        }
        return "No user logged in";
    }

    public String add_device(int deviceid, int userid, String name, String type, String status, String location) {
        if (user == null) {
            return "Login first then try";
        }
        
      //  Device de= new Device(deviceid,name,type,status,location);
        

        try {
            String query = "INSERT INTO device (deviceid,userid, name, type, status, location) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, deviceid);
            pst.setInt(2, userid);
            pst.setString(3, name);
            pst.setString(4, type);
            pst.setString(5, status);
            pst.setString(6, location);
            int rs = pst.executeUpdate();

            if (rs > 0) {
            	 devicelogs(deviceid, "Device Added", "Device " + name + " added to the system.");
            	
                return "Device added successfully";
                
            }
          //  Dev.put(name,de);
        }
        catch (SQLException e) {
        	  System.out.println("Wrong values");
        }
		return "";
      
    }

    public String control_device( int deviceid, int userid, String command, String status, int response_time) {
        if (user == null) {
            return "Please login first";
        }
        try {    
        String query = "SELECT command FROM devicecommands WHERE deviceid=?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1, deviceid);
        ResultSet r = pst.executeQuery();
        r.next();
        String command1 = r.getString(1);
        if (command1.equals(command)) {
            return "Device is already"+command;
        }
        }catch(Exception e)
        {
        	return " add the device first";
        }
     /*   try {
        if(command.equals(command))
        {
        	System.out.println("Device is already"+ command);
        }}
        catch(Exception e)
        {
        	return "Give right command";
        }*/
       
        try {
            String query1 = "INSERT INTO devicecommands ( deviceid, userid, command, status, response_time) VALUES ( ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query1);
         //   pst.setInt(1, command_id);
            ps.setInt(1, deviceid);
            ps.setInt(2, userid);
            ps.setString(3, command);
            ps.setString(4, status);
            ps.setInt(5, response_time);
            int rs = ps.executeUpdate();

            if (rs > 0) {
             //   executor.submit(new DeviceTask(new Camera(deviceid, "Test Camera", "Camera", "On", "Living Room"))); // Example task
              devicelogs(deviceid, "Device Controlled", "Command " + command + " executed with status: " + status);
                  return "Device control command added successfully";
               
            }
        } catch (SQLException e) {
        	  System.out.println("Incorrect value");
        }
        return "Error occurred while controlling device";
    
   }

    public void devicelogs(int deviceid,String event,String details )
    {
         String query="insert into devicelogs(deviceid,event,details)values(?,?,?)";
    	
    	try {
    		
    	PreparedStatement pst=con.prepareStatement(query);
    	//pst.setInt(1, logid);
    	pst.setInt(1, deviceid);
    	pst.setString(2, event);
    	pst.setString(3, details);
        pst.executeUpdate();
    
    	}catch(SQLException e)
    	{
    		
    	}
    }
   
    	public void DisplayAllDevices() {
    		 String query = "SELECT * FROM device";
    	        try {
    	            PreparedStatement pst = con.prepareStatement(query);
    	            ResultSet rs = pst.executeQuery();
    	            while (rs.next()) {
    	                String result = rs.getInt("deviceid") + "\t" + rs.getString("name") + "\t" + rs.getString("type") + "\t" + rs.getString("status") + "\t" + rs.getString("location") + "\t";
    	                System.out.println(result);
    	                try (FileWriter f= new FileWriter("DeviceHistory.txt",true);//true to avoid overwrite
    	            			BufferedWriter bf=new BufferedWriter(f);) 
    	                {
    	                	
    	                    bf.write(result);
    	                    
    	                }catch (Exception e) {
    	                    
    	                }
    	              }
    	           
    	            }
    	               catch (Exception e) {
    	                   
    	           }     
  }   
}

public class SmartHome {
	
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            Connection con = DBConnection.getConnection();
            devicemanager manage = new devicemanager(con);

            boolean run = true;

            while (run) {
                System.out.println("Menu:");
                System.out.println("1. Create User account");
                System.out.println("2. Login");
                System.out.println("3. Logout");
                System.out.println("4. Add device");
                System.out.println("5. Control device");
                System.out.println("6. Display all devices");
                System.out.println("7. Exit");

                System.out.print("Enter your choice: ");
                int choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        System.out.print("Enter user id, username, email, password: ");
                        int userid = sc.nextInt();
                        String username = sc.next();
                        String email = sc.next();
                        String password = sc.next();
                        System.out.println(manage.createUserAccount(userid, username, email, password));
                        break;
                    case 2:
                        System.out.print("Enter username and password: ");
                        String name = sc.next();
                        String pass = sc.next();
                        System.out.println(manage.login(name, pass));
                        break;
                    case 3:
                        System.out.println(manage.logout());
                        break;
                    case 4:
                        System.out.print("Enter device details ( device id,user id, name, type, status(success/fail), location): ");
                        int deviceid = sc.nextInt();
                        int user_id = sc.nextInt();
                        String name1 = sc.next();
                        String type = sc.next();
                        String status = sc.next();
                        String location = sc.next();
                        System.out.println(manage.add_device(deviceid, user_id, name1, type, status, location));
                        break;
                    case 5:
                        System.out.print("Enter command details ( device id, user id, command(on/off/user ip), status, response time(ms)): ");
                      //  int commandid = sc.nextInt();
                        int device_id = sc.nextInt();
                        int user_id2 = sc.nextInt();
                        String command = sc.next();
                        String status2 = sc.next();
                        int response_time = sc.nextInt();
                        System.out.println(manage.control_device(device_id, user_id2, command, status2, response_time));
                        break;
                    case 6:
                    	 System.out.print("Displaying the devices:");
                        manage.DisplayAllDevices();
                        break;
                    case 7:
                        run = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
            sc.close();
        } catch (SQLException e) {
            
        }
    }
}

