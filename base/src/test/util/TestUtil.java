package test.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestUtil {
	
	public static String getFormattedDate(){
		LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
        return now.format(formatter);
	}

}
