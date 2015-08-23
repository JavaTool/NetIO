package test.netty.client;

import java.io.IOException;
import java.net.HttpURLConnection;

import net.io.http.HttpBackInfo;
import net.io.util.HttpConnectUtil;

public class HttpClient extends HttpConnectUtil {
	
	public static void main(String[] args) {
		try {
			HttpURLConnection connection = createConnection("http://localhost:8888/");
			HttpBackInfo httpBackInfo = getConnectionStream(connection, new byte[]{});
			String strcoo = getCookies(connection);
			System.out.println(strcoo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
