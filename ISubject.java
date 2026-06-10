package pattern.observer;

public interface ISubject {
	void attach(IObserver observer);
    void notifyObservers();

}
