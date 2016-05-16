package demo;

import java.util.Random;

public class CallBackDemo {
	public static void main(String[] args) {
		CallBackDemo cb=new CallBackDemo();
		cb.runTest();
	}

	private void runTest() {
		OtherA otherA=new OtherA();
		for (int i = 0; i < 100; i++) {
			otherA.add(i,new IcallBack() {
				@Override
				public void callBack(Object... object) {
					System.out.println("--"+object[0]);
					
				}
			});
		}
		
	}
}

class OtherA{
	public void add(int i, IcallBack icallBack) {
		System.out.println(i);
		icallBack.callBack(i+"A");
		try {
			Thread.sleep(new Random().nextInt(3000));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}