/**
 * Copyright (c) 2014 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.digi.xbee.api.io;

import static org.junit.Assert.*;

import org.junit.Test;

import com.digi.xbee.api.exceptions.OperationNotSupportedException;

public class IOSampleTest {

	// Constants.
	private static final byte[] INVALID_IO_DATA = new byte[]{0x00, 0x01, 0x02, 0x03};
	
	/*
	 * DIGITAL I/Os CONFIGURATION
	 * --------------------------------------------------
	 * DIO0, DIO4 and DIO9 are configured as digital I/O.
	 * 
	 * ADCs CONFIGURATION
	 * --------------------------------------------------
	 * DIO1 and DIO3 are configured as ADC. Power Supply voltage is provided.
	 * 
	 * DIGITAL I/Os VALUES
	 * --------------------------------------------------
	 * DIO0 is in HIGH state.
	 * DIO4 is in LOW state.
	 * DIO9 is in HIGH state.
	 * 
	 * ADCs VALUES
	 * --------------------------------------------------
	 * DIO1 value is 0x020C (524)
	 * DIO3 value is 0x00FA (250)
	 * Power Supply value is 0x04E2 (1250)
	 */
	private static final byte[] IO_DATA_ONLY_DIGITAL = new byte[]{0x01, 0x02, 0x11, 0x00, 0x02, 0x01};
	private static final byte[] IO_DATA_ONLY_ANALOG = new byte[]{0x01, 0x00, 0x00, (byte)0x8A, 0x02, 0x0C, 0x00, (byte)0xFA, 0x04, (byte)0xE2};
	private static final byte[] IO_DATA_MIXED = new byte[]{0x01, 0x02, 0x11, (byte)0x8A, 0x02, 0x01, 0x02, 0x0C, 0x00, (byte)0xFA, 0x04, (byte)0xE2};
	
	private static final int DIO1_ANALOG_VALUE = 524;
	private static final int DIO3_ANALOG_VALUE = 250;
	private static final int POWER_SUPPLY_VALUE = 1250;
	
	private static final int DIGITAL_MASK = 529; // 0x0211
	private static final int ANALOG_MASK = 138; // 0x8A
	
	/**
	 * Verify that the IOSample object is not correctly instantiated when the IO data byte 
	 * array is null.
	 */
	@Test(expected=NullPointerException.class)
	public void testCreateIOSampleWithNullData() {
		// Instantiate an IOSample object with null IO data.
		new IOSample(null);
	}
	
	/**
	 * Verify that the IOSample object is not correctly instantiated when the IO data byte 
	 * array is not valid.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateIOSampleWithInvalidData() {
		// Instantiate an IOSample object with invalid IO data.
		new IOSample(INVALID_IO_DATA);
	}
	
	/**
	 * Verify that digital values are successfully parsed and stored in the sample 
	 * when it only contains digital information.
	 */
	@Test
	public void testGetDigitalValuesFromDigitalData() {
		// Create an IO sample with the digital data.
		IOSample ioSample = new IOSample(IO_DATA_ONLY_DIGITAL);
		
		// Verify that the digital mask is correct.
		assertEquals(DIGITAL_MASK, ioSample.getDigitalMask());
		
		// Verify that the IO sample has digital values.
		assertTrue(ioSample.hasDigitalValues());
		
		// Try to get the values of all the DIO lines, only DIO0, DIO4 and 
		// DIO9 should have digital values, verify them.
		for (IOLine ioLine:IOLine.values()) {
			if (ioLine == IOLine.DIO0_AD0) {
				assertTrue(ioSample.hasDigitalValue(ioLine));
				assertEquals(IOValue.HIGH, ioSample.getDigitalValue(ioLine));
				assertEquals(IOValue.HIGH, ioSample.getDigitalValues().get(ioLine));
			} else if (ioLine == IOLine.DIO4_AD4) {
				assertTrue(ioSample.hasDigitalValue(ioLine));
				assertEquals(IOValue.LOW, ioSample.getDigitalValue(ioLine));
				assertEquals(IOValue.LOW, ioSample.getDigitalValues().get(ioLine));
			} else if (ioLine == IOLine.DIO9) {
				assertTrue(ioSample.hasDigitalValue(ioLine));
				assertEquals(IOValue.HIGH, ioSample.getDigitalValue(ioLine));
				assertEquals(IOValue.HIGH, ioSample.getDigitalValues().get(ioLine));
			} else {
				assertFalse(ioSample.hasDigitalValue(ioLine));
				try {
					ioSample.getDigitalValue(ioLine);
					fail("Digital value shouldn't have been retrieved.");
				} catch (Exception e) {
					assertEquals(IllegalArgumentException.class, e.getClass());
				}
				assertNull(ioSample.getDigitalValues().get(ioLine));
			}
		}
	}
	
	/**
	 * Verify that no analog values are retrieved from the sample when it 
	 * only contains digital information.
	 */
	@Test
	public void testGetAnalogValuesFromDigitalData() {
		// Create an IO sample with the digital data.
		IOSample ioSample = new IOSample(IO_DATA_ONLY_DIGITAL);
		
		// Verify that the analog mask is 0.
		assertEquals(0, ioSample.getAnalogMask());
		
		// Verify that the IO sample does not have analog values.
		assertFalse(ioSample.hasAnalogValues());
		
		// Verify that there is not any analog value.
		for (IOLine ioLine:IOLine.values()) {
			assertFalse(ioSample.hasAnalogValue(ioLine));
			try {
				ioSample.getAnalogValue(ioLine);
				fail("Analog value shouldn't have been retrieved.");
			} catch (Exception e) {
				assertEquals(IllegalArgumentException.class, e.getClass());
			}
			assertNull(ioSample.getAnalogValues().get(ioLine));
		}
	}
	
	/**
	 * Verify that power supply value not retrieved from the sample when it 
	 * only contains digital information.
	 * 
	 * @throws OperationNotSupportedException 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testGetPowerSupplyValueFromDigitalData() throws OperationNotSupportedException {
		// Create an IO sample with the analog data.
		IOSample ioSample = new IOSample(IO_DATA_ONLY_DIGITAL);
		
		// Verify that the IO sample does not have power supply value.
		assertFalse(ioSample.hasPowerSupplyValue());
			
		ioSample.getPowerSupplyValue();
	}
	
	/**
	 * Verify that no digital values are retrieved from the sample when it 
	 * only contains analog and power supply information.
	 */
	@Test
	public void testGetDigitalValuesFromAnalogData() {
		// Create an IO sample with the analog data.
		IOSample ioSample = new IOSample(IO_DATA_ONLY_ANALOG);
		
		// Verify that the digital mask is 0.
		assertEquals(0, ioSample.getDigitalMask());
		
		// Verify that the IO sample does not have digital values.
		assertFalse(ioSample.hasDigitalValues());
		
		// Verify that there is not any digital value.
		for (IOLine ioLine:IOLine.values()) {
			assertFalse(ioSample.hasDigitalValue(ioLine));
			try {
				ioSample.getDigitalValue(ioLine);
				fail("Digital value shouldn't have been retrieved.");
			} catch (Exception e) {
				assertEquals(IllegalArgumentException.class, e.getClass());
			}
			assertNull(ioSample.getDigitalValues().get(ioLine));
		}
	}
	
	/**
	 * Verify that analog values are successfully parsed and stored in the sample 
	 * when it only contains analog and power supply information.
	 */
	@Test
	public void testGetAnalogValuesFromAnalogData() {
		// Create an IO sample with the analog data.
		IOSample ioSample = new IOSample(IO_DATA_ONLY_ANALOG);
		
		// Verify that the analog mask is correct.
		assertEquals(ANALOG_MASK, ioSample.getAnalogMask());
		
		// Verify that the IO sample has analog values.
		assertTrue(ioSample.hasAnalogValues());
		
		// Try to get the values of all the DIO lines, only DIO1 and DIO3 lines 
		// should have analog values, verify them.
		for (IOLine ioLine:IOLine.values()) {
			if (ioLine == IOLine.DIO1_AD1) {
				assertTrue(ioSample.hasAnalogValue(ioLine));
				assertEquals(DIO1_ANALOG_VALUE, ioSample.getAnalogValue(ioLine));
				assertEquals(DIO1_ANALOG_VALUE, (int)ioSample.getAnalogValues().get(ioLine));
			} else if (ioLine == IOLine.DIO3_AD3) {
				assertTrue(ioSample.hasAnalogValue(ioLine));
				assertEquals(DIO3_ANALOG_VALUE, ioSample.getAnalogValue(ioLine));
				assertEquals(DIO3_ANALOG_VALUE, (int)ioSample.getAnalogValues().get(ioLine));
			} else {
				assertFalse(ioSample.hasAnalogValue(ioLine));
				try {
					ioSample.getAnalogValue(ioLine);
					fail("Analog value shouldn't have been retrieved.");
				} catch (Exception e) {
					assertEquals(IllegalArgumentException.class, e.getClass());
				}
				assertNull(ioSample.getAnalogValues().get(ioLine));
			}
		}
	}
	
	/**
	 * Verify that power supply value is successfully parsed and stored in the sample 
	 * when it only contains analog and power supply information.
	 * 
	 * @throws OperationNotSupportedException 
	 */
	@Test
	public void testGetPowerSupplyValueFromAnalogData() throws OperationNotSupportedException {
		// Create an IO sample with the analog data.
		IOSample ioSample = new IOSample(IO_DATA_ONLY_ANALOG);
		
		// Verify that the IO sample has power supply value.
		assertTrue(ioSample.hasPowerSupplyValue());
			
		assertEquals(POWER_SUPPLY_VALUE, ioSample.getPowerSupplyValue());
	}
	
	/**
	 * Verify that digital values are successfully parsed and stored in the sample 
	 * when it contains digital, analog and power supply information.
	 */
	@Test
	public void testGetDigitalValuesFromMixedData() {
		// Create an IO sample with mixed (digital + analog) data.
		IOSample ioSample = new IOSample(IO_DATA_MIXED);
		
		// Verify that the digital mask is correct.
		assertEquals(DIGITAL_MASK, ioSample.getDigitalMask());
		
		// Verify that the IO sample has digital values.
		assertTrue(ioSample.hasDigitalValues());
		
		// Try to get the values of all the DIO lines, only DIO0, DIO4 and 
		// DIO9 should have digital values, verify them.
		for (IOLine ioLine:IOLine.values()) {
			if (ioLine == IOLine.DIO0_AD0) {
				assertTrue(ioSample.hasDigitalValue(ioLine));
				assertEquals(IOValue.HIGH, ioSample.getDigitalValue(ioLine));
				assertEquals(IOValue.HIGH, ioSample.getDigitalValues().get(ioLine));
			} else if (ioLine == IOLine.DIO4_AD4) {
				assertTrue(ioSample.hasDigitalValue(ioLine));
				assertEquals(IOValue.LOW, ioSample.getDigitalValue(ioLine));
				assertEquals(IOValue.LOW, ioSample.getDigitalValues().get(ioLine));
			} else if (ioLine == IOLine.DIO9) {
				assertTrue(ioSample.hasDigitalValue(ioLine));
				assertEquals(IOValue.HIGH, ioSample.getDigitalValue(ioLine));
				assertEquals(IOValue.HIGH, ioSample.getDigitalValues().get(ioLine));
			} else {
				assertFalse(ioSample.hasDigitalValue(ioLine));
				try {
					ioSample.getDigitalValue(ioLine);
					fail("Digital value shouldn't have been retrieved.");
				} catch (Exception e) {
					assertEquals(IllegalArgumentException.class, e.getClass());
				}
				assertNull(ioSample.getDigitalValues().get(ioLine));
			}
		}
	}
	
	/**
	 * Verify that analog values are successfully parsed and stored in the sample 
	 * when it contains digital, analog and power supply information.
	 */
	@Test
	public void testGetAnalogValuesFromMixedData() {
		// Create an IO sample with mixed (digital + analog) data.
		IOSample ioSample = new IOSample(IO_DATA_MIXED);
		
		// Verify that the analog mask is correct.
		assertEquals(ANALOG_MASK, ioSample.getAnalogMask());
		
		// Verify that the IO sample has analog values.
		assertTrue(ioSample.hasAnalogValues());
		
		// Try to get the values of all the DIO lines, only DIO1 and DIO3 lines 
		// should have analog values, verify them.
		for (IOLine ioLine:IOLine.values()) {
			if (ioLine == IOLine.DIO1_AD1) {
				assertTrue(ioSample.hasAnalogValue(ioLine));
				assertEquals(DIO1_ANALOG_VALUE, ioSample.getAnalogValue(ioLine));
				assertEquals(DIO1_ANALOG_VALUE, (int)ioSample.getAnalogValues().get(ioLine));
			} else if (ioLine == IOLine.DIO3_AD3) {
				assertTrue(ioSample.hasAnalogValue(ioLine));
				assertEquals(DIO3_ANALOG_VALUE, ioSample.getAnalogValue(ioLine));
				assertEquals(DIO3_ANALOG_VALUE, (int)ioSample.getAnalogValues().get(ioLine));
			} else {
				assertFalse(ioSample.hasAnalogValue(ioLine));
				try {
					ioSample.getAnalogValue(ioLine);
					fail("Analog value shouldn't have been retrieved.");
				} catch (Exception e) {
					assertEquals(IllegalArgumentException.class, e.getClass());
				}
				assertNull(ioSample.getAnalogValues().get(ioLine));
			}
		}
	}
	
	/**
	 * Verify that power supply value is successfully parsed and stored in the sample 
	 * when it contains digital, analog and power supply information.
	 * 
	 * @throws OperationNotSupportedException 
	 */
	@Test
	public void testGetPowerSupplyValueFromMixedData() throws OperationNotSupportedException {
		// Create an IO sample with mixed (digital + analog) data.
		IOSample ioSample = new IOSample(IO_DATA_MIXED);
		
		// Verify that the IO sample has power supply value.
		assertTrue(ioSample.hasPowerSupplyValue());
			
		assertEquals(POWER_SUPPLY_VALUE, ioSample.getPowerSupplyValue());
	}
}
