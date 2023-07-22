/*
 * Copyright (c) 2023 Mango Family
 * All rights reserved or may not! :)
 */

package com.dqtri.mango.safeguard.security;

import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class TokenResolverTest {

    @Test
    void addMaster_giveMaster_thenOnlyVNM() {
        String masterAddress = "192.168.1.1";
        HashSet<String> aggrAddresses = new HashSet<>();
        aggrAddresses.add("192.168.1.2");
        HashSet<String> pollerAddresses = new HashSet<>();
        pollerAddresses.add("192.168.1.3");
        String vvMasterAddress = "192.168.1.4";
        String reportingAddress = "192.168.1.5";
        String pollerFailoverAddress = "192.168.1.6";
        HashMap<String, String> stringStringHashMap = hashAddressRolesInMap(masterAddress, aggrAddresses,
                pollerAddresses, vvMasterAddress, reportingAddress, pollerFailoverAddress);
        //test
        assertThat(stringStringHashMap).isNotNull();
        String masterRole = stringStringHashMap.get("192.168.1.1");
        assertThat(masterRole).isEqualTo("VNM");
        String aggrRole = stringStringHashMap.get("192.168.1.2");
        assertThat(aggrRole).isEqualTo("VNA");
        String pollerRole = stringStringHashMap.get("192.168.1.3");
        assertThat(pollerRole).isEqualTo("VNP");
        String vvMasterRole = stringStringHashMap.get("192.168.1.4");
        assertThat(vvMasterRole).isEqualTo("VV");
        String reportingRole = stringStringHashMap.get("192.168.1.5");
        assertThat(reportingRole).isEqualTo("VNR");
        String pollerFailoverRole = stringStringHashMap.get("192.168.1.6");
        assertThat(pollerFailoverRole).isEqualTo("VNP-standby");
    }

    @Test
    void addMaster_giveMasterNull_thenEmpty() {
        HashSet<String> aggrAddresses = new HashSet<>();
        aggrAddresses.add("192.168.1.2");
        HashSet<String> pollerAddresses = new HashSet<>();
        pollerAddresses.add("192.168.1.3");
        String vvMasterAddress = "192.168.1.4";
        String reportingAddress = "192.168.1.5";
        String pollerFailoverAddress = "192.168.1.6";
        HashMap<String, String> stringStringHashMap = hashAddressRolesInMap(null, aggrAddresses,
                pollerAddresses, vvMasterAddress, reportingAddress, pollerFailoverAddress);
        //test
        assertThat(stringStringHashMap).isNotNull();
//        String masterRole = stringStringHashMap.get("192.168.1.1");
//        assertThat(masterRole).isEqualTo("VNM");
        String aggrRole = stringStringHashMap.get("192.168.1.2");
        assertThat(aggrRole).isEqualTo("VNA");
        String pollerRole = stringStringHashMap.get("192.168.1.3");
        assertThat(pollerRole).isEqualTo("VNP");
        String vvMasterRole = stringStringHashMap.get("192.168.1.4");
        assertThat(vvMasterRole).isEqualTo("VV");
        String reportingRole = stringStringHashMap.get("192.168.1.5");
        assertThat(reportingRole).isEqualTo("VNR");
        String pollerFailoverRole = stringStringHashMap.get("192.168.1.6");
        assertThat(pollerFailoverRole).isEqualTo("VNP-standby");
    }

    @Test
    void addMaster_giveAggrAddressesNull_thenWithoutAgger() {
        String masterAddress = "192.168.1.1";
//        HashSet<String> aggrAddresses = new HashSet<>();
//        aggrAddresses.add("192.168.1.2");
        HashSet<String> pollerAddresses = new HashSet<>();
        pollerAddresses.add("192.168.1.3");
        String vvMasterAddress = "192.168.1.4";
        String reportingAddress = "192.168.1.5";
        String pollerFailoverAddress = "192.168.1.6";
        HashMap<String, String> stringStringHashMap = hashAddressRolesInMap(masterAddress, null,
                pollerAddresses, vvMasterAddress, reportingAddress, pollerFailoverAddress);
        //test
        assertThat(stringStringHashMap).isNotNull();
        String masterRole = stringStringHashMap.get("192.168.1.1");
        assertThat(masterRole).isEqualTo("VNM");
//        String aggrRole = stringStringHashMap.get("192.168.1.2");
//        assertThat(aggrRole).isEqualTo("VNA");
        String pollerRole = stringStringHashMap.get("192.168.1.3");
        assertThat(pollerRole).isEqualTo("VNP");
        String vvMasterRole = stringStringHashMap.get("192.168.1.4");
        assertThat(vvMasterRole).isEqualTo("VV");
        String reportingRole = stringStringHashMap.get("192.168.1.5");
        assertThat(reportingRole).isEqualTo("VNR");
        String pollerFailoverRole = stringStringHashMap.get("192.168.1.6");
        assertThat(pollerFailoverRole).isEqualTo("VNP-standby");
    }

    @Test
    void addMaster_giveAggrPollerAddressesNull_thenWithoutPoller() {
        String masterAddress = "192.168.1.1";
        HashSet<String> aggrAddresses = new HashSet<>();
        aggrAddresses.add("192.168.1.2");
//        HashSet<String> pollerAddresses = new HashSet<>();
//        pollerAddresses.add("192.168.1.3");
        String vvMasterAddress = "192.168.1.4";
        String reportingAddress = "192.168.1.5";
        String pollerFailoverAddress = "192.168.1.6";
        HashMap<String, String> stringStringHashMap = hashAddressRolesInMap(masterAddress, aggrAddresses,
                null, vvMasterAddress, reportingAddress, pollerFailoverAddress);
        //test
        assertThat(stringStringHashMap).isNotNull();
        String masterRole = stringStringHashMap.get("192.168.1.1");
        assertThat(masterRole).isEqualTo("VNM");
        String aggrRole = stringStringHashMap.get("192.168.1.2");
        assertThat(aggrRole).isEqualTo("VNA");
//        String pollerRole = stringStringHashMap.get("192.168.1.3");
//        assertThat(pollerRole).isEqualTo("VNP");
        String vvMasterRole = stringStringHashMap.get("192.168.1.4");
        assertThat(vvMasterRole).isEqualTo("VV");
        String reportingRole = stringStringHashMap.get("192.168.1.5");
        assertThat(reportingRole).isEqualTo("VNR");
        String pollerFailoverRole = stringStringHashMap.get("192.168.1.6");
        assertThat(pollerFailoverRole).isEqualTo("VNP-standby");
    }

    @Test
    void addMaster_giveVvMasterNull_thenWithoutVvMaster() {
        String masterAddress = "192.168.1.1";
        HashSet<String> aggrAddresses = new HashSet<>();
        aggrAddresses.add("192.168.1.2");
        HashSet<String> pollerAddresses = new HashSet<>();
        pollerAddresses.add("192.168.1.3");
        String reportingAddress = "192.168.1.5";
        String pollerFailoverAddress = "192.168.1.6";
        HashMap<String, String> stringStringHashMap = hashAddressRolesInMap(masterAddress, aggrAddresses,
                pollerAddresses, null, reportingAddress, pollerFailoverAddress);
        //test
        assertThat(stringStringHashMap).isNotNull();
        String masterRole = stringStringHashMap.get("192.168.1.1");
        assertThat(masterRole).isEqualTo("VNM");
        String aggrRole = stringStringHashMap.get("192.168.1.2");
        assertThat(aggrRole).isEqualTo("VNA");
        String pollerRole = stringStringHashMap.get("192.168.1.3");
        assertThat(pollerRole).isEqualTo("VNP");
//        String vvMasterRole = stringStringHashMap.get("192.168.1.4");
//        assertThat(vvMasterRole).isEqualTo("VV");
        String reportingRole = stringStringHashMap.get("192.168.1.5");
        assertThat(reportingRole).isEqualTo("VNR");
        String pollerFailoverRole = stringStringHashMap.get("192.168.1.6");
        assertThat(pollerFailoverRole).isEqualTo("VNP-standby");
    }

    @Test
    void addMaster_giveReportingNull_thenWithoutReporting() {
        String masterAddress = "192.168.1.1";
        HashSet<String> aggrAddresses = new HashSet<>();
        aggrAddresses.add("192.168.1.2");
        HashSet<String> pollerAddresses = new HashSet<>();
        pollerAddresses.add("192.168.1.3");
        String vvMasterAddress = "192.168.1.4";
        String pollerFailoverAddress = "192.168.1.6";
        HashMap<String, String> stringStringHashMap = hashAddressRolesInMap(masterAddress, aggrAddresses,
                pollerAddresses, vvMasterAddress, null, pollerFailoverAddress);
        //test
        assertThat(stringStringHashMap).isNotNull();
        String masterRole = stringStringHashMap.get("192.168.1.1");
        assertThat(masterRole).isEqualTo("VNM");
        String aggrRole = stringStringHashMap.get("192.168.1.2");
        assertThat(aggrRole).isEqualTo("VNA");
        String pollerRole = stringStringHashMap.get("192.168.1.3");
        assertThat(pollerRole).isEqualTo("VNP");
        String vvMasterRole = stringStringHashMap.get("192.168.1.4");
        assertThat(vvMasterRole).isEqualTo("VV");
//        String reportingRole = stringStringHashMap.get("192.168.1.5");
//        assertThat(reportingRole).isEqualTo("VNR");
        String pollerFailoverRole = stringStringHashMap.get("192.168.1.6");
        assertThat(pollerFailoverRole).isEqualTo("VNP-standby");
    }

    @Test
    void addMaster_givePollerFailoverNull_thenWithoutPollerFailover() {
        String masterAddress = "192.168.1.1";
        HashSet<String> aggrAddresses = new HashSet<>();
        aggrAddresses.add("192.168.1.2");
        HashSet<String> pollerAddresses = new HashSet<>();
        pollerAddresses.add("192.168.1.3");
        String vvMasterAddress = "192.168.1.4";
        String reportingAddress = "192.168.1.5";
        HashMap<String, String> stringStringHashMap = hashAddressRolesInMap(masterAddress, aggrAddresses,
                pollerAddresses, vvMasterAddress, reportingAddress, null);
        //test
        assertThat(stringStringHashMap).isNotNull();
        String masterRole = stringStringHashMap.get("192.168.1.1");
        assertThat(masterRole).isEqualTo("VNM");
        String aggrRole = stringStringHashMap.get("192.168.1.2");
        assertThat(aggrRole).isEqualTo("VNA");
        String pollerRole = stringStringHashMap.get("192.168.1.3");
        assertThat(pollerRole).isEqualTo("VNP");
        String vvMasterRole = stringStringHashMap.get("192.168.1.4");
        assertThat(vvMasterRole).isEqualTo("VV");
        String reportingRole = stringStringHashMap.get("192.168.1.5");
        assertThat(reportingRole).isEqualTo("VNR");
    }


    @Test
    void addMaster_giveMasterAsAggerPoller_AggrAsPoler_thenMergedRole() {
        String masterAddress = "192.168.1.1";
        HashSet<String> aggrAddresses = new HashSet<>();
        aggrAddresses.add("192.168.1.1");
        aggrAddresses.add("192.168.1.2");
        HashSet<String> pollerAddresses = new HashSet<>();
        pollerAddresses.add("192.168.1.1");
        pollerAddresses.add("192.168.1.2");
        pollerAddresses.add("192.168.1.3");
        String vvMasterAddress = "192.168.1.4";
        String reportingAddress = "192.168.1.5";
        String pollerFailoverAddress = "192.168.1.6";
        HashMap<String, String> stringStringHashMap = hashAddressRolesInMap(masterAddress, aggrAddresses,
                pollerAddresses, vvMasterAddress, reportingAddress, pollerFailoverAddress);
        //test
        assertThat(stringStringHashMap).isNotNull();
        String masterRole = stringStringHashMap.get("192.168.1.1");
        assertThat(masterRole).isEqualTo("VNMAP");
        String aggrRole = stringStringHashMap.get("192.168.1.2");
        assertThat(aggrRole).isEqualTo("VNAP");
        String pollerRole = stringStringHashMap.get("192.168.1.3");
        assertThat(pollerRole).isEqualTo("VNP");
        String vvMasterRole = stringStringHashMap.get("192.168.1.4");
        assertThat(vvMasterRole).isEqualTo("VV");
        String reportingRole = stringStringHashMap.get("192.168.1.5");
        assertThat(reportingRole).isEqualTo("VNR");
        String pollerFailoverRole = stringStringHashMap.get("192.168.1.6");
        assertThat(pollerFailoverRole).isEqualTo("VNP-standby");
    }

    @Test
    void addMaster_giveMasterVVAsAggerPoller_AggrAsPoler_thenOnlyVNM() {
        String masterAddress = "192.168.1.1";
        HashSet<String> aggrAddresses = new HashSet<>();
        aggrAddresses.add("192.168.1.1");
        aggrAddresses.add("192.168.1.2");
        HashSet<String> pollerAddresses = new HashSet<>();
        pollerAddresses.add("192.168.1.1");
        pollerAddresses.add("192.168.1.2");
        pollerAddresses.add("192.168.1.3");
        String vvMasterAddress = "192.168.1.1";
        String reportingAddress = "192.168.1.5";
        String pollerFailoverAddress = "192.168.1.6";
        HashMap<String, String> stringStringHashMap = hashAddressRolesInMap(masterAddress, aggrAddresses,
                pollerAddresses, vvMasterAddress, reportingAddress, pollerFailoverAddress);
        //test
        assertThat(stringStringHashMap).isNotNull();
        String masterRole = stringStringHashMap.get("192.168.1.1");
        assertThat(masterRole).isEqualTo("VNMAP-VV");
        String aggrRole = stringStringHashMap.get("192.168.1.2");
        assertThat(aggrRole).isEqualTo("VNAP");
        String pollerRole = stringStringHashMap.get("192.168.1.3");
        assertThat(pollerRole).isEqualTo("VNP");
//        String vvMasterRole = stringStringHashMap.get("192.168.1.4");
//        assertThat(vvMasterRole).isEqualTo("VV");
        String reportingRole = stringStringHashMap.get("192.168.1.5");
        assertThat(reportingRole).isEqualTo("VNR");
        String pollerFailoverRole = stringStringHashMap.get("192.168.1.6");
        assertThat(pollerFailoverRole).isEqualTo("VNP-standby");
    }

    @Test
    void addMaster_giveRealCase_thenExpectedValue() {
        String masterAddress = "192.168.94.181";
        HashSet<String> aggrAddresses = new HashSet<>();
        aggrAddresses.add("192.168.94.123");
        HashSet<String> pollerAddresses = new HashSet<>();
        pollerAddresses.add("192.168.94.123");
        pollerAddresses.add("192.168.94.171");
        String vvMasterAddress = "192.168.94.175";
        String reportingAddress = "192.168.94.114";
        String pollerFailoverAddress = "192.168.94.115";
        HashMap<String, String> stringStringHashMap = hashAddressRolesInMap(masterAddress, aggrAddresses,
                pollerAddresses, vvMasterAddress, reportingAddress, pollerFailoverAddress);
        //test
        assertThat(stringStringHashMap).isNotNull();
        String masterRole = stringStringHashMap.get("192.168.94.181");
        assertThat(masterRole).isEqualTo("VNM");
        String aggrRole = stringStringHashMap.get("192.168.94.123");
        assertThat(aggrRole).isEqualTo("VNAP");
        String pollerRole = stringStringHashMap.get("192.168.94.171");
        assertThat(pollerRole).isEqualTo("VNP");
        String vvMasterRole = stringStringHashMap.get("192.168.94.175");
        assertThat(vvMasterRole).isEqualTo("VV");
        String reportingRole = stringStringHashMap.get("192.168.94.114");
        assertThat(reportingRole).isEqualTo("VNR");
        String pollerFailoverRole = stringStringHashMap.get("192.168.94.115");
        assertThat(pollerFailoverRole).isEqualTo("VNP-standby");
    }

    @Test
    void addMaster_giveMasterPooler_thenVNMP() {
        String masterAddress = "192.168.1.1";
        HashSet<String> aggrAddresses = new HashSet<>();
        aggrAddresses.add("192.168.1.2");
        HashSet<String> pollerAddresses = new HashSet<>();
        pollerAddresses.add("192.168.1.1");
        pollerAddresses.add("192.168.1.2");
        pollerAddresses.add("192.168.1.3");
        String vvMasterAddress = "192.168.1.1";
        String reportingAddress = "192.168.1.5";
        String pollerFailoverAddress = "192.168.1.6";
        HashMap<String, String> stringStringHashMap = hashAddressRolesInMap(masterAddress, aggrAddresses,
                pollerAddresses, vvMasterAddress, reportingAddress, pollerFailoverAddress);
        //test
        assertThat(stringStringHashMap).isNotNull();
        String masterRole = stringStringHashMap.get("192.168.1.1");
        assertThat(masterRole).isEqualTo("VNMP-VV");
        String aggrRole = stringStringHashMap.get("192.168.1.2");
        assertThat(aggrRole).isEqualTo("VNAP");
        String pollerRole = stringStringHashMap.get("192.168.1.3");
        assertThat(pollerRole).isEqualTo("VNP");
        String reportingRole = stringStringHashMap.get("192.168.1.5");
        assertThat(reportingRole).isEqualTo("VNR");
        String pollerFailoverRole = stringStringHashMap.get("192.168.1.6");
        assertThat(pollerFailoverRole).isEqualTo("VNP-standby");
    }

    private HashMap<String, String> hashAddressRolesInMap(String master, Set<String> aggrs, Set<String> pollers,
                                                          String vvMaster, String reporting, String pollerFailover) {

        HashMap<String, String> roleMap = new HashMap<>();
        addMaster(roleMap, master);
        addAggrs(roleMap, aggrs);
        addPollers(roleMap, pollers);
        addVvMaster(roleMap, vvMaster);
        addReporting(roleMap, reporting);
        addPollerFailover(roleMap, pollerFailover);
        return roleMap;
    }

    private void addMaster(HashMap<String, String> addressRoleMap, String master) {
        if (master == null || master.isBlank()) {
            return;
        }
        addressRoleMap.put(master, "VNM");
    }

    private void addAggrs(HashMap<String, String> addressRoleMap, Set<String> aggrs) {
        if (aggrs == null) {
            return;
        }
        for (String aggr : aggrs) {
            String role = "VNA";
            if (addressRoleMap.containsKey(aggr)) {
                role = addressRoleMap.get(aggr) + "A";
            }
            addressRoleMap.put(aggr, role);
        }
    }

    private void addPollers(HashMap<String, String> addressRoleMap, Set<String> pollers) {
        if (pollers == null) {
            return;
        }
        for (String poller : pollers) {
            String role = "VNP";
            if (addressRoleMap.containsKey(poller)) {
                role = addressRoleMap.get(poller) + "P";
            }
            addressRoleMap.put(poller, role);
        }
    }

    private void addVvMaster(HashMap<String, String> addressRoleMap, String vvMaster) {
        if (vvMaster == null || vvMaster.isBlank()) {
            return;
        }
        String role = "VV";
        if (addressRoleMap.containsKey(vvMaster)) {
            role = addressRoleMap.get(vvMaster) + "-VV";
        }
        addressRoleMap.put(vvMaster, role);
    }

    private void addReporting(HashMap<String, String> addressRoleMap, String reporting) {
        if (reporting == null || reporting.isBlank()) {
            return;
        }
        String role = "VNR";
        addressRoleMap.put(reporting, role);
    }

    private void addPollerFailover(HashMap<String, String> addressRoleMap, String pollerFailover) {
        if (pollerFailover == null || pollerFailover.isBlank()) {
            return;
        }
        String role = "VNP-standby";
        addressRoleMap.put(pollerFailover, role);
    }
}
