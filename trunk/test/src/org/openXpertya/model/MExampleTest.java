package org.openXpertya.model;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openXpertya.util.Env;

public class MExampleTest {
	
    @BeforeClass
    public static void start() {
        // before all tests        
    }
	
	@Before
	public void preTest() {
		// before a single test
	}

	@After
	public void postTest() {
		// after a single test
	}

    @AfterClass
    public static void end() {
    	// after all tests
    }
	
    /** ------------------ TESTS ----------------- */
    
	@Test
	public void testAddition() {
		assertEquals("Testing addition1", 2, 1+1);
	}

	@Test
	public void subtraction() {
		assertEquals("Testing subtraction1", 1, 1-1);
	}
	
}
