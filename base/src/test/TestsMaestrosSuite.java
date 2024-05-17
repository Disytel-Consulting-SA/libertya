package test;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import test.model.AD_UserTest;
import test.model.MProductPriceTest;
import test.model.MProductTest;

@Suite
@SelectClasses({ 
	AD_UserTest.class,
	MProductTest.class,
	MProductPriceTest.class})
public class TestsMaestrosSuite {

	
}