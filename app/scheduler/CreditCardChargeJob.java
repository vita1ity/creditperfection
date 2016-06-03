package scheduler;

import play.Logger;

public class CreditCardChargeJob implements Runnable {
	
    @Override
    public void run() {
        Logger.info("creating the runnable");
        Logger.info("EVERY 60 MInutes ---    " + System.currentTimeMillis());
    }
    
}
