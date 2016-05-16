package demo;

public class ObejctDemo {
	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
			new Thread(new TestRunnable(i)).start();
		}
	}
}

class ObjectA{
	private static ObjectA oa=null; 
	private int a=0;
	public void setA(int i){
		a=i;
	};
	public int getA(){
		return a;
	}
	public static  ObjectA getObjectA(){
		if(oa==null){
			synchronized (Object.class) {
				if(oa==null){
					oa=new ObjectA();
				}
			}
		}
		return oa;
	}
}
class TestRunnable implements Runnable{

	private int ii=0;
	public TestRunnable(int i) {
		ii=i;
	}

	@Override
	public void run() {
		ObjectA oa=ObjectA.getObjectA();
		oa.setA(ii);
		System.out.println(ii+" --- "+oa.getA()+"   "+(ii==oa.getA()));
	}
	
}
