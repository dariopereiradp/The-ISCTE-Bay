package pt.iul.ista.pcd.user;

public class ThreadPool {

	private final Worker[] threads;
	private final BlockingQueue<Runnable> fila;

	public ThreadPool(int numThreads) {
		threads = new Worker[numThreads];
		fila = new BlockingQueue<Runnable>();
		for (int i = 0; i < numThreads; i++) {
			threads[i] = new Worker();
			threads[i].start();
		}
	}

	public void submit(Runnable task) {
		synchronized (fila) {
			try {
				fila.offer(task);
				fila.notify();
			} catch (InterruptedException e) {
				System.out.println("Ocorreu um erro na espera: - " + e.getMessage());
			}
		}

	}

	private class Worker extends Thread {

		@Override
		public void run() {
			Runnable task;

			while (true) {
				task = fila.poll();
				try {
					task.run();
				} catch (RuntimeException e) {
					System.out.println("Thread pool foi interrompida: - " + e.getMessage());
				}

			}
		}

	}
}
