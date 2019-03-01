package pt.iul.ista.pcd.user;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class BlockingQueue<T> {
	private Queue<T> queue = new LinkedList<T>();
	private int capacidade = 0;
	private boolean temLimite;

	public BlockingQueue() {
		temLimite = false;
	}
	
	public BlockingQueue(ArrayList<T> list){
		queue.addAll(list);
	}

	public BlockingQueue(int capacidade) {
		if (capacidade < 1)
			throw new IllegalArgumentException("Capacidade deve ser um número positivo!");
		else {
			temLimite = true;
			this.capacidade = capacidade;
		}
	}

	public int size() {
		return queue.size();
	}

	public synchronized void offer(T obj) throws InterruptedException {
		while (queue.size() == capacidade && temLimite) {
			System.out.println("Offer à espera");
			wait();
		}
		notifyAll();
		queue.offer(obj);
	}

	public synchronized T poll() {
		while (queue.size() == 0) {
			System.out.println("Poll à espera");
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.println("Ocorreu um erro na espera: - " + e.getMessage());
			}
		}
		if (temLimite)
			notifyAll();
		return queue.poll();
	}

	public void clear() {
		queue.clear();

	}

	public boolean isEmpty() {
		return queue.size() == 0;
	}
}