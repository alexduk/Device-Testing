package com.DeviceTesting;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;
import java.util.logging.*;

public class SNMPManager {

	Snmp snmp = null;
	String address = null;
	String community = "";

	public SNMPManager(String address, String community) {
		this.address = address;
		this.community = community;
	}

	void start() throws IOException {
		TransportMapping<?> transport = new DefaultUdpTransportMapping();
		snmp = new Snmp(transport);
		transport.listen();
	}

	public HashMap<Integer, String> getAsString(OID oid) throws IOException {
		Logger logger = Logger.getLogger(SNMPManager.class.getName());
		logger.setLevel(Level.WARNING);

		TableUtils tUtils = new TableUtils(snmp, new DefaultPDUFactory());
		List<TableEvent> events = tUtils.getTable(getTarget(), new OID[] { new OID(oid) }, null, null);
		HashMap<Integer, String> snmpMap = new HashMap<Integer, String>();
		for (TableEvent event : events) {
			if (event.isError()) {
				logger.warning(this.address + ": SNMP event error: " + event.getErrorMessage());
				continue;
			}
			for (VariableBinding vb : event.getColumns()) {
				String key = vb.getOid().toString();
				String value = vb.getVariable().toString();
				snmpMap.put(Integer.valueOf(key.substring(key.lastIndexOf(".") + 1)), value);
			}
		}
		return snmpMap;
	}

	public ResponseEvent<Address> get(OID oids[]) throws IOException {
		PDU pdu = new PDU();
		for (OID oid : oids) {
			pdu.add(new VariableBinding(oid));
		}
		pdu.setType(PDU.GET);
		ResponseEvent<Address> event = snmp.send(pdu, getTarget(), null);
		if (event != null) {
			return event;
		}
		throw new RuntimeException("GET timed out");
	}

	private Target<Address> getTarget() {
		Address targetAddress = GenericAddress.parse(address);
		CommunityTarget<Address> target = new CommunityTarget<Address>();
		target.setCommunity(new OctetString(community));
		target.setAddress(targetAddress);
		target.setRetries(2);
		target.setTimeout(1500);
		target.setVersion(SnmpConstants.version2c);
		return target;
	}
}