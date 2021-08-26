package com.DeviceTesting;

import java.io.IOException;
import java.util.HashMap;

import org.snmp4j.smi.OID;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class DeviceTest {

	SNMPManager client;

	@BeforeTest
	public void setup() throws IOException {
		client = new SNMPManager("udp:10.104.62.221/161", "bkar");
		client.start();
	}

	@Test
	public void getAllInterfacesTest() throws Exception {
		HashMap<Integer, String> snmpResponse = client.getAsString(new OID(".1.3.6.1.2.1.2.2.1.2"));

		String cliResponseStr = SSHManager.sshWithDevice("apple15", "apple15", "10.104.62.221",
				"show snmp mib ifmib ifindex");
		HashMap<Integer, String> cliResponse = SSHManager.getResponseAsHashMap(cliResponseStr);

		System.out.println(snmpResponse.toString());
		System.out.println(cliResponse.toString());

		Assert.assertEquals(snmpResponse, cliResponse);
	}
}
