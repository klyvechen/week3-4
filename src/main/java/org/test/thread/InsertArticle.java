package org.test.thread;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zul.Button;
public class InsertArticle implements Runnable {

    
    private Button undo;
    private org.zkoss.zk.ui.event.EventQueue<Event> xSecondEvtQue;
    private org.zkoss.zk.ui.event.EventQueue<Event> insertNewArticleEvtQue;
    private org.zkoss.zk.ui.event.EventQueue<Event> undoInsertEvtQue;
    private EventListener<Event> xSecondEventListener;
    private EventListener<Event> undoInsertEventListener;
    private Desktop desktop; 
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public InsertArticle(Button undo,EventQueue<Event> xSecondEvtQuep, EventQueue<Event> insertNewArticleEvtQuep,EventQueue<Event> undoInsertEvtQuep,Desktop desktopp ) {
        this.undo = undo;
        this.xSecondEvtQue = xSecondEvtQuep;
        this.insertNewArticleEvtQue = insertNewArticleEvtQuep;
        this.undoInsertEvtQue = undoInsertEvtQuep;
        this.desktop = desktopp;
        this.xSecondEventListener = new EventListener(){
        	public void onEvent(Event evt){
        		System.out.println("x second event get");
        		insertNewArticleEvtQue.publish(new Event("insertArticle", null));
    			unsubscribeEvt();    			
        	}
        };
        this.undoInsertEventListener = new EventListener(){
        	public void onEvent(Event evt){
        		System.out.println("undo event get");
    			unsubscribeEvt();
        	}
        };        
    }

    public void run() {
        try {
        	Executions.activate(this.desktop); 
        	this.xSecondEvtQue.subscribe(this.xSecondEventListener);
        	this.undoInsertEvtQue.subscribe(undoInsertEventListener);
        	System.out.println("insert article ok");
            
        } catch (DesktopUnavailableException e) {
            System.err.println("Desktop is no longer available: ");
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void unsubscribeEvt(){
    	insertNewArticleEvtQue.unsubscribe(xSecondEventListener);
    	undoInsertEvtQue.unsubscribe(undoInsertEventListener);
    }
}
