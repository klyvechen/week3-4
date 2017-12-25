package org.test.thread;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;

public class insertAfter5Sec implements Runnable{
	private org.zkoss.zk.ui.event.EventQueue<Event> insertAfter5;
	private static int i = 0;
	Desktop desktop;
	
	public insertAfter5Sec(EventQueue<Event> insertAfter5, Desktop desktop){
		
		this.desktop = desktop;
		this.insertAfter5 = insertAfter5;
	}
	
	public void run() {		
		//que2 = EventQueues.lookup("count to x second", EventQueues.APPLICATION,true);
		try {
			System.out.println("i++ is "+(i++));
			Thread.sleep(1000);			
			System.out.println("insert after 5 to publish");
			insertAfter5.publish(new Event("x second " + String.valueOf(i),null));
			System.out.println("insert after 5 publish ok");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}
