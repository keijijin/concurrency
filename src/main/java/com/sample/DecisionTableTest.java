package com.sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * This is a sample class to launch a rule.
 */
public class DecisionTableTest {
    public static final void main(String[] args) throws FileNotFoundException, InterruptedException {
    	ExecutorService es = Executors.newWorkStealingPool();

		PrintWriter writer = new PrintWriter("test.txt");

		List<Integer> numList = new ArrayList<Integer>();
		for (int i = 0; i < 100; i++) {
			numList.add(new Integer(i));
		}
		Iterator<Integer> itor = numList.iterator();
		while(itor.hasNext()) {
			int n = itor.next();
   			es.execute(() -> ExecRule(writer, n, Thread.currentThread()));   			
		}
		es.shutdown();
		es.awaitTermination(1, TimeUnit.MINUTES);

		writer.close();
		System.out.println("Done!!");
    }
    
    public static void ExecRule(PrintWriter writer, int i, Thread thread) {
    	writer.println("executor:" + i + ", thread-id:" + thread.getName());
    	System.out.println("executor:" + i + ", thread-id:" + thread.getName());
    	
    	//ExecRule();
    	try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    	
    public static void ExecRule() {
        try {
            // load up the knowledge base
	        KieServices ks = KieServices.Factory.get();
    	    KieContainer kContainer = ks.getKieClasspathContainer();
        	KieSession kSession = kContainer.newKieSession("ksession-dtables");

            // go !
            Message message = new Message();
            message.setMessage("Hello World");
            message.setStatus(Message.HELLO);
            kSession.insert(message);
            kSession.fireAllRules();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static class Message {

        public static final int HELLO = 0;
        public static final int GOODBYE = 1;

        private String message;

        private int status;

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getStatus() {
            return this.status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

    }

}
