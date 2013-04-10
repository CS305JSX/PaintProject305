
public class test {
	
	public static void main(String[] args){
		test T = new test();
		CounterThread t1 = new CounterThread(T, "one");
		CounterThread t2 = new CounterThread(T, "two");
		
		t1.start();
		t2.start();
	}
	
	public void count(CounterThread t){
		for(int i=0; i<100000; i++)
			System.out.println(t.id + " " + i);
	}
	
}

class CounterThread extends Thread {
	
	String id;
	test T;
	
	public CounterThread(test T, String id){
		this.T = T;
		this.id = id;
	}
	
	public void run(){
		T.count(this);
	}
}