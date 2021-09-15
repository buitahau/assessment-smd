package net.bigpoint.assessment.gasstation;

/**
 * This class is an implementation of a gas pump.
 * 
 * It is final and should not be modified!
 * 
 * It is not thread-safe! It should only ever be used by one thread.
 * 
 * Especially only one thread at a time may call the pumpGas(double) method!
 * 
 */
public final class GasPump {

	private String id;

	private final GasType gasType;

	private volatile double amount;

	private volatile boolean isWorking = false;

	public GasPump(GasType gasType, double amount) {
		super();
		this.gasType = gasType;
		this.amount = amount;
	}

	public void pumpGas(double amount) {
		this.amount -= amount;
		isWorking = true;
		// simulate that it takes time to pump some gas
		try {
			Thread.sleep((long) (amount * 100));
		} catch (InterruptedException e) {
			// ignored
		}
		isWorking = false;
	}

	public double getRemainingAmount() {
		return amount;
	}

	public GasType getGasType() {
		return gasType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isWorking() {
		return isWorking;
	}

	public void setWorking(boolean working) {
		isWorking = working;
	}
}
