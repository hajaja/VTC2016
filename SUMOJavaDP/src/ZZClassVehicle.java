import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.math3.util.MultidimensionalCounter.Iterator;


public class ZZClassVehicle implements Comparable<ZZClassVehicle>{
	private int t;
	private int number;
	public ZZClassVehicle(int number, int t) {
		this.number = number;
		this.t = t;
	}
	@Override
	public int compareTo(ZZClassVehicle o) {
		// TODO Auto-generated method stub
		return this.t - o.t;
	}
	
	public String toString() {
		String ret = "";
		ret += "number=" + number + "\t" + "t=" + t;
		return ret;
	}
	
	public static void main(String[] args) {
		ZZClassVehicle vehicle1 = new ZZClassVehicle(1, 1);
		ZZClassVehicle vehicle2 = new ZZClassVehicle(2, 2);
		ZZClassVehicle vehicle3 = new ZZClassVehicle(3, 1);
		PriorityQueue<ZZClassVehicle> vehicles = new PriorityQueue<ZZClassVehicle>();
		vehicles.add(vehicle1);
		vehicles.add(vehicle2);
		vehicles.add(vehicle3);
		
		// PriorityQueue, allows elements with the same key
		System.out.println("PriorityQueue");
		// iteration has no sorted order
		System.out.println("using iterator, no order");
		java.util.Iterator<ZZClassVehicle> iter = vehicles.iterator();
		while (iter.hasNext()) {
			System.out.println(iter.next());
		}
		// poll has sorted order
		System.out.println("using poll, with order");
		while (vehicles.isEmpty() == false) {
			System.out.println(vehicles.poll());
		}
		
		// TreeSet, no order
		System.out.println("TreeSet");
		Set<ZZClassVehicle> set = new TreeSet<ZZClassVehicle>();
		set.add(vehicle1);
		set.add(vehicle2);
		set.add(vehicle3);
		java.util.Iterator<ZZClassVehicle> iterTreeSet = set.iterator();
		while (iterTreeSet.hasNext()) {
			System.out.println(iterTreeSet.next());
		}
		
		// HashSet, no order
		System.out.println("HashSet");
		set = new HashSet<ZZClassVehicle>();
		set.add(vehicle1);
		set.add(vehicle2);
		set.add(vehicle3);
		java.util.Iterator<ZZClassVehicle> iterHashSet = set.iterator();
		while (iterHashSet.hasNext()) {
			System.out.println(iterHashSet.next());
		}
	}
}
