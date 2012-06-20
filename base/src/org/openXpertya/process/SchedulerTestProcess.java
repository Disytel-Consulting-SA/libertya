package org.openXpertya.process;

public class SchedulerTestProcess extends SvrProcess {

	@Override
	protected String doIt() throws Exception {
		System.out.println("DoIt...");
		return "OK";
	}

	@Override
	protected void prepare() {
		System.out.println("Prepare...");
	}

}
