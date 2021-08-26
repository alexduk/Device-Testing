package com.DeviceTesting;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SSHManager {

	public static String sshWithDevice(String username, String password, String host, String command) throws Exception {

		Session session = null;
		ChannelExec channel = null;

		try {
			System.out.println("Connecting!");
			session = new JSch().getSession(username, host, 22);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();

			channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);
			ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
			channel.setOutputStream(responseStream);
			channel.connect();

			System.out.println("Connected");
			while (channel.isConnected()) {
				Thread.sleep(100);
			}

			String responseString = new String(responseStream.toByteArray());

			return responseString;
		} finally {
			if (session != null) {
				session.disconnect();
			}
			if (channel != null) {
				channel.disconnect();
			}
		}
	}

	public static HashMap<Integer, String> getResponseAsHashMap(String responseStr) {
		HashMap<Integer, String> cliResponse = new HashMap<Integer, String>();
		String[] responseArr = responseStr.trim().split("\n");
		for (int i = 0; i < responseArr.length; i++) {
			String[] resIndiv = responseArr[i].split(":");
			String value = resIndiv[0];
			int key = Integer.valueOf(resIndiv[1].trim().split(" ")[2]);
			cliResponse.put(key, value);
		}
		return cliResponse;
	}
}
