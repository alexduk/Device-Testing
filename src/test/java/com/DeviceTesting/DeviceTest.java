package com.DeviceTesting;

import java.io.IOException;
import java.util.HashMap;

import org.snmp4j.smi.OID;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class DeviceTest {

	SNMPManager snmpClient;
	SSHManager sshClient;

	@BeforeTest
	public void setup() throws IOException {
		snmpClient = new SNMPManager("udp:10.104.62.221/161", "bkar");
		snmpClient.start();
		
		sshClient = new SSHManager("apple15", "apple15", "10.104.62.221");
	}

	@Test
	public void getAllInterfacesTest() throws Exception {
		HashMap<Integer, String> snmpResponse = snmpClient.getAsString(new OID(".1.3.6.1.2.1.2.2.1.2"));

		String cliResponseStr = sshClient.sendCommand("show snmp mib ifmib ifindex");
		HashMap<Integer, String> cliResponse = SSHManager.getResponseAsHashMap(cliResponseStr);

		System.out.println(snmpResponse.toString());
		System.out.println(cliResponse.toString());

		Assert.assertEquals(snmpResponse, cliResponse);
	}
}
