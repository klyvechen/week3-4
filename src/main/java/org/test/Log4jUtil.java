package org.test;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.test.mvvm.MyViewModel;

public class Log4jUtil {
	public static Logger logger;
	static{
		PropertyConfigurator.configure("log4j.properties");
    	logger = Logger.getLogger(MyViewModel.class);
	}
}
