package com.mlab.tesis.java.gpx.data.test;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mlab.tesis.java.gpx.data.BSimpleWayPoint;

public class TestBSimpleWayPoint {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		System.out.print("Testing TestBSimpleWayPoint...");
		
		// Simple constructor
		BSimpleWayPoint wp = new BSimpleWayPoint();
		Assert.assertNotNull(wp);
		Assert.assertEquals(-1l, wp.getTime());
		//System.out.println("\n"+wp.toString());

		// constructor with params
		BSimpleWayPoint wp2 = new BSimpleWayPoint("P1", "Pto de pruebas", 1000l, -3.5, 42.9, 960.0);
		Assert.assertNotNull(wp2);
		Assert.assertEquals("P1", wp2.getName());
		Assert.assertEquals("Pto de pruebas", wp2.getDescription());
		Assert.assertEquals(-3.5, wp2.getLongitude());
		Assert.assertEquals(42.9, wp2.getLatitude());
		Assert.assertEquals(960.0, wp2.getAltitude());
		//System.out.println(wp2.toString());
		
		// constructor from values
		List<Double> values = Arrays.asList(new Double[] {-3.5, 42.9,960.0});
		BSimpleWayPoint wp3 = new BSimpleWayPoint("P1", "Pto de pruebas", 1000l, values);
		Assert.assertNotNull(wp3);
		Assert.assertEquals("P1", wp3.getName());
		Assert.assertEquals("Pto de pruebas", wp3.getDescription());
		Assert.assertEquals(-3.5, wp3.getLongitude());
		Assert.assertEquals(42.9, wp3.getLatitude());
		Assert.assertEquals(960.0, wp3.getAltitude());
		//System.out.println(wp2.toString());
		
		System.out.println("OK");
	}
	
	@Test
	public void testAsCsv() {
		System.out.print("Testing TestBSimpleWayPoint.asCsv()...");
		BSimpleWayPoint wp = new BSimpleWayPoint("P1", "Pto de pruebas", 1000l, -3.5, 42.9, 960.0);
		Assert.assertNotNull(wp);
		//System.out.println(wp.asCsv(true));
		Assert.assertEquals("1970-01-01T00:00:01.01Z,1000,-3.500000,42.900000,960.00",wp.asCsv(false)); 
		Assert.assertEquals("1970-01-01T00:00:01.01Z,1000,-3.500000,42.900000,960.00,459179.76,4749831.30",
			wp.asCsv(true)); 

		System.out.println("OK");
	}

	@Test
	public void testAsGpx() {
		System.out.print("Testing TestBSimpleWayPoint.asGpx()...");
		BSimpleWayPoint wp = new BSimpleWayPoint("P1", "Pto de pruebas", 1000l, -3.5, 42.9, 960.0);
		Assert.assertNotNull(wp);
		//System.out.println(wp.asGpx());
		Assert.assertEquals("<wpt  lat=\"42.900000\" lon=\"-3.500000\"><ele>960.00</ele><time>1970-01-01T00:00:01.01Z</time><name>P1</name><desc>Pto de pruebas</desc></wpt>",
				wp.asGpx()); 

		System.out.println("OK");
	}

	@Test
	public void testClone() {
		System.out.print("Testing TestBSimpleWayPoint.clone()...");
		BSimpleWayPoint wp = new BSimpleWayPoint("P1", "Pto de pruebas", 1000l, -3.5, 42.9, 960.0);
		Assert.assertNotNull(wp);
		BSimpleWayPoint wp3 = wp.clone();
		Assert.assertNotNull(wp3);
		Assert.assertEquals("P1", wp3.getName());
		Assert.assertEquals("Pto de pruebas", wp3.getDescription());
		Assert.assertEquals(-3.5, wp3.getLongitude());
		Assert.assertEquals(42.9, wp3.getLatitude());
		Assert.assertEquals(960.0, wp3.getAltitude());
		System.out.println("OK");
	}

}
