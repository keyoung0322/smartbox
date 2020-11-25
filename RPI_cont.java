import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.net.*;
import java.io.*;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.*;

import java.sql.*;

public class RPI_cont {
    public static final GpioController gpio = GpioFactory.getInstance();

    final static GpioPinDigitalOutput led = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "led", PinState.LOW);
    final static GpioPinDigitalInput sensor = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_DOWN);
    
    static Connection conn;
    static PreparedStatement pstmt1;
    static PreparedStatement pstmt2;
    static ResultSet rs = null;
    
    static String url = "jdbc:mysql://127.0.0.1:3306/pirdb?autoReconnect=true";
    static String userId = "root";
    static String userPass = "tomong";
    static int logCnt = 0;
    static int cnt = 0;
    static String dTime = null;
    
    public static void main(String[] args) throws IOException {
	//System.out.println("program start!");
	try {
	    try {
		Class.forName("com.mysql.jdbc.Driver");
	    } catch(ClassNotFoundException e) {
		System.out.println("Driver Error!");
	    }

	    cnt = 0;
	    System.out.println(sensor.getState()); // 모션센서 현재 상태 출력
	    RPI_cont.sensor.addListener(new GpioPinListenerDigital() { // 모션센서 상태 HIGH로 바뀌면
		    @Override
		    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
			System.out.println(" --> PIRsensor state : " + event.getPin()+ " = " + event.getState());
			if (event.getState().isHigh()) {
			    RPI_cont.led.high(); // led on
			    
			    SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy.MM.dd HH:mm:ss", Locale.KOREA );
			    Date currentTime = new Date();
			    dTime = formatter.format(currentTime);
			    
			    try {
				conn = DriverManager.getConnection(url, userId, userPass);
				
				//if(logCnt == 0)
				//logCnt++;
				//else {
				pstmt1 = conn.prepareStatement("SELECT MAX(no) FROM logdata");
				rs = pstmt1.executeQuery();
				while(rs.next()) {
				    logCnt = rs.getInt("MAX(no)");
				}
				logCnt++;
				//}
				//System.out.println(logCnt);
				pstmt2 = conn.prepareStatement("INSERT INTO logdata values(?,?)");
				pstmt2.setLong(1, logCnt);
				pstmt2.setString(2, dTime);
				pstmt2.executeUpdate();
			    } catch (SQLException e) {
				e.printStackTrace();
			    } finally {
				if(rs != null) try {rs.close();} catch(SQLException sqle){}
				if(pstmt1 != null) try {pstmt1.close();} catch (SQLException sqle) {}
				if(pstmt2 != null) try {pstmt2.close();} catch(SQLException sqle){}
				if(conn != null) try {conn.close();} catch (SQLException sqle) {}
			    }
			}
			else {
			    cnt = 0;
			    //RPI_cont.led.low();
			}
		    }
		});
	    
	    for (;;) {
		Thread.sleep(500);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}

