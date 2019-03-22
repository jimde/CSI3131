/* CSI3131 W2019 - Assignment 2
 *
 * Jimmy Deng
 * 8194330
 */

 import java.util.Random;

/***************************************************************************************/
//  Provide code for the methods in the classes Ships and Harbour, and in one place
//  in the main() method. Look for "your code here" comments.


/* the main class of assignment 2, launching the simulation */
public class Assignment2 {
    // Configuration
    final static int DESTINATIONS = 2;
    final static int SHIPS = 4;
    final static int SHIP_SIZE = 2;
    final static int PASSENGERS = 10;
    final static String[] destName = {"Hawaii", "Mauritius"};

    public static void main(String args[]) throws InterruptedException{
        Ship[] sships = new Ship[SHIPS];
        Passenger[] passengers = new Passenger[PASSENGERS];

        // create the Harbour
        Harbour sp = new Harbour();

        /* create ships and passengers*/
        for (int i = 0; i < SHIPS; i++)
            sships[i] = new Ship(sp, i);
        for (int i = 0; i < PASSENGERS; i++)
            passengers[i] = new Passenger(sp, i);

        /* now launch them */
        for (int i = 0; i < SHIPS; i++)
            sships[i].start();
        for (int i = 0; i < PASSENGERS; i++)
            passengers[i].start();

        // let them enjoy for 20 seconds
        try { Thread.sleep(20000);} catch (InterruptedException e) { }

        /* now stop them */
        // note how we are using deferred cancellation
        for (int i = 0; i < SHIPS; i++)
            try {sships[i].interrupt();} catch (Exception e) { }
        for (int i = 0; i < PASSENGERS; i++)
            try {passengers[i].interrupt();} catch (Exception e) { }

        // Wait until everybody else is finished
        // your code here
        for (int i = 0; i < SHIPS; i++)
            try {sships[i].join();} catch (Exception e) { e.printStackTrace(); }
        for (int i = 0; i < PASSENGERS; i++)
            try {passengers[i].join();} catch (Exception e) { e.printStackTrace(); }

        // This should be the last thing done by this program:
        System.out.println("Simulation finished.");
    }
}



/* The class implementing a passenger. */
// This class is completely provided to you, you don't have to change
// anything, just have a look and understand what the passenger wants from
// the Harbour and from the ships
class Passenger extends Thread {
    private boolean enjoy;
    private int id;
    private Harbour sp;

    // constructor
    public Passenger(Harbour sp, int id) {
        this.sp = sp;
        this.id = id;
        enjoy = true;
    }

    // this is the passenger's thread
    public void run() {
        int         stime;
        int         dest;
        Ship        sh;

        while (enjoy) {
            try {
                // Wait and arrive to the port
                stime = (int) (700*Math.random());
                sleep(stime);

                // Choose the destination
                dest = (int) (((double) Assignment2.DESTINATIONS)*Math.random());
                System.out.println("Passenger " + id + " wants to go to " + Assignment2.destName[dest]);

                // come to the harbour and board a ship to my destination
                // (might wait if there is no such ship ready)
                sh = sp.wait4Ship(dest);

                // Should be executed after the ship is on the dock and taking passengers
                System.out.println("Passenger " + id + " has boarded ship " + sh.id + ", destination: " + Assignment2.destName[dest]);

                // wait for launch
                sh.wait4launch();

                // Enjoy the ride
                // Should be executed after the ship has launched.
                System.out.println("Passenger " + id + " enjoying the ride to " + Assignment2.destName[dest] + ": Yeahhh!");

                // wait for arriving
                sh.wait4arriving();

                // Should be executed after the ship has landed
                System.out.println("Passenger " + id + " leaving the ship " + sh.id + " which has " + sh.numSeats + " seats");

                // Leave the ship
                sh.leave();
            } catch (InterruptedException e) {
                enjoy = false; // have been interrupted, probably by the main program, terminate
            }
       }
       System.out.println("Passenger " + id + " has finished its rides.");
    }
}

/* The class simulating an ship */
// Now, here you will have to implement several methods
class Ship extends Thread {
    public int         id;
    private Harbour    sp;
    private boolean enjoy;
    // your code here (other local variables and semaphores)
    public int numSeats;
    private Semaphore passengerSem;
    private boolean docked;

    // constructor
    public Ship(Harbour sp, int id) {
        this.sp = sp;
        this.id = id;
        enjoy = true;

        // your code here (local variable and semaphore initializations)
        passengerSem = new Semaphore(2);
        numSeats = 2;
        docked = false;
    }

    // the ship thread executes this
    public void run() {
        int     stime;
        int     dest;

        while (enjoy) {
            try {
                // Wait until there an empty arriving dock, then arrive
                dest = sp.wait4arriving(this);
                docked = true;
                sleep(10);

                System.out.println("ship " + id + " arriving on dock " + dest);

                // Tell the passengers that we have arrived

                // Wait until all passengers leave
                while (numSeats < 2) {
                    sleep(1);
                }
                sp.boarding(dest);

                System.out.println("ship " + id + " boarding to " + Assignment2.destName[dest] + " now! With " + numSeats + " seats");

                // the passengers can start to board now

                // Wait until full of passengers
                while (numSeats > 0) {
                    sleep(1);
                }

                // 4, 3, 2, 1, Start!
                System.out.println("ship " + id + " Departs towards " + Assignment2.destName[dest] + "!");

                // tell the passengers we have launched, so they can enjoy now ;-)
                sp.launch(dest);
                docked = false;

                // Sail in water
                stime = 500 + (int)(1500*Math.random());
                sleep(stime);
            } catch (InterruptedException e) {
                enjoy = false; // have been interrupted, probably by the main program, terminate
            }
        }
        System.out.println("ship " + id + " has finished its rides.");
    }


    // service functions to passengers
    // called by the passengers leaving the ship
    public void leave()  throws InterruptedException  {
        // your code here
        passengerSem.signalSem();
        numSeats += 1;
    }

    // called by the passengers sitting in the ship, to wait
    // until the launch
    public void wait4launch()  throws InterruptedException {
        // your code here

        // wait until ship is fully loaded
        while (numSeats > 0) {
            sleep(1);
        }
        sleep(2);
    }

    // called by the bored passengers sitting in the ship, to wait
    // until arriving
    public void wait4arriving()  throws InterruptedException {
        // your code here
        while (!docked) {
            sleep(1);
        }
    }

    public void passengerBoarding() throws InterruptedException {
        passengerSem.waitSem();
        numSeats -= 1;
    }
}


/* The class implementing the Harbour. */
/* This might be convenient place to put lots of the synchronization code into */
class Harbour {
    Ship[] docks; // what is sitting on a given dock
    // your code here (other local variables and semaphores)
    private Semaphore dockSem;

    // constructor
    public Harbour() {
        docks = new Ship[Assignment2.DESTINATIONS];


        // docks[] is an array containing the ships sitting on corresponding docks
        // Value null means the dock is empty
        for(int i = 0; i < Assignment2.DESTINATIONS; i++){
            docks[i] = null;
        }

        // your code here (local variable and semaphore initializations)
        dockSem = new Semaphore(2);
    }

    // called by a passenger wanting to go to the given destination
    // returns the ship he/she boarded
    // Careful here, as the dock might be empty at this moment
    public Ship wait4Ship(int dest) throws InterruptedException {
        // your code here

        while (true) {
            try {
                docks[dest].passengerBoarding();
                return docks[dest];
            }
            catch (IndexOutOfBoundsException e) {}
            catch (NullPointerException e) {}
            // catch (InterruptedException e) {
            //     System.out.println("ASDF");
            //     // throw new InterruptedException();
            //     return null;
            // }
        }



    }

    // called by an ship to tell the harbour that it is accepting passengers now to destination dest
    public void boarding(int dest) throws InterruptedException {
        // your code here


    }

    // Called by an ship returning from a trip
    // Returns the number of the empty dock where to land (might wait
    // until there is an empty dock).
    // Try to rotate the docks so that no destination is starved
    public int wait4arriving(Ship sh)  throws InterruptedException  {
        // your code here

        // get semaphore if available, else wait
        dockSem.waitSem();

        // choose random dock to attempt to dock
        int dockToAttempt = (int)(((double) Assignment2.DESTINATIONS)*Math.random());

        if (docks[dockToAttempt] == null) {
            docks[dockToAttempt] = sh;
            return dockToAttempt;
        } else if (docks[1-dockToAttempt] == null) {
            docks[1-dockToAttempt] = sh;
            return 1-dockToAttempt;
        } else {
            return -1;
        }
    }

    // called by an ship when it Departs, to inform the
    // harbour that the dock has been emptied
    public void launch(int dest) throws InterruptedException {
        // your code here
        docks[dest] = null;
        dockSem.signalSem();


    }
}
