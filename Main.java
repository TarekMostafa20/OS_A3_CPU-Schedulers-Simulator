import java.util.*;

class Process {
    String name;
    String color;
    int arrivalTime;
    int burstTime;
    int original_burstTime;
    int timeQuantum;
    int changeable_timeQuantum;
    int priority;
    int startTime;
    int finishTime;
    int waitingTime;
    int turnaroundTime;
    int AGfactor;
    boolean DonePreemptive;
    boolean executed;
    boolean calculated;

    public Process(String name, String color, int arrivalTime, int burstTime, int priority, int timeQuantum) {
        this.name = name;
        this.color = color;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.original_burstTime = burstTime;
        this.timeQuantum = timeQuantum;
        this.changeable_timeQuantum = timeQuantum;
        this.priority = priority;
        this.startTime = -1;
        this.finishTime = 0;
        this.waitingTime = 0;
        this.turnaroundTime = 0;
        this.DonePreemptive = false;
        this.executed = false;
        this.calculated = false;
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("processes numms: ");
        int numProcesses = scanner.nextInt();

        System.out.print("Round Robin Time Quantum: ");
        int timeQuantum = scanner.nextInt();

        System.out.print("context switching time: ");
        int contextSwitchingTime = scanner.nextInt();

        List<Process> processes = new ArrayList<>();

        for (int i = 0; i < numProcesses; i++) {
            System.out.println("Enter the details of Process " + (i + 1) + ":");
            System.out.print("Name: ");
            String name = scanner.next();

            System.out.print("Color: ");
            String color = scanner.next();

            System.out.print("Arrival Time: ");
            int arrivalTime = scanner.nextInt();

            System.out.print("Burst Time: ");
            int burstTime = scanner.nextInt();

            System.out.print("Priority: ");
            int priority = scanner.nextInt();

            processes.add(new Process(name, color, arrivalTime, burstTime, priority,timeQuantum));
        }
        System.out.print("Please Choose the Algorithm: \n1- Shortest-Job First\n2- Shortest Remaining Time First\n3- Non-preemptive Priority Scheduling\n4- AGScheduler\n----> ");
        int choice = scanner.nextInt();
        if(choice == 1)
        {
            sjfScheduler(new ArrayList<>(processes), contextSwitchingTime);
        }
        else if(choice == 2)
        {
            srtfScheduler(new ArrayList<>(processes), contextSwitchingTime);
        }
        else if(choice == 3)
        {
            priorityScheduler(new ArrayList<>(processes), contextSwitchingTime);
        }
        else if(choice == 4)
        {
            AgScheduler(new ArrayList<>(processes), contextSwitchingTime);
        }
        else
        {
            System.out.println("Wrong Choice");
        }

        scanner.close();
    }

    // Shortest-Job First

    public static void sjfScheduler(List<Process> processes, int contextSwitchingTime) {
        List<Process> sjfOrder = new ArrayList<>();
        List<Process> waitingQueue = new ArrayList<>();
        int currentTime = 0;
        processes.sort(Comparator.comparingInt(p -> p.burstTime));
        while (!processes.isEmpty() || !waitingQueue.isEmpty()) {
            Iterator<Process> processIterator = processes.iterator();
            while (processIterator.hasNext()) {
                Process p = processIterator.next();
                if (p.arrivalTime <= currentTime && !p.executed) {
                    waitingQueue.add(p);
                    processIterator.remove();
                }
            }
            if (!waitingQueue.isEmpty()) {
                waitingQueue.sort(Comparator.comparingInt(p -> p.burstTime));
                Process shortest = waitingQueue.remove(0);
                shortest.startTime = currentTime;
                shortest.finishTime = currentTime + shortest.burstTime ;
                shortest.turnaroundTime = shortest.finishTime - shortest.arrivalTime + contextSwitchingTime;
                shortest.waitingTime = shortest.turnaroundTime - shortest.burstTime - contextSwitchingTime;
                currentTime = shortest.finishTime + contextSwitchingTime;
                shortest.executed = true;
                sjfOrder.add(shortest);
            } else if (!processes.isEmpty()) {
                currentTime++;
            }
        }

        System.out.println("\n" + "Shortest-Job First" + " Execution Order:");
        for (Process p : sjfOrder) {
            System.out.println("Process " + p.name + " (" + p.color + ")" + ":\t" + p.startTime + " ---- " + p.finishTime );
        }

        calculateAverages(sjfOrder);
    }





    // Shortest Remaining Time First
    public static void srtfScheduler(List<Process> processes, int contextSwitchingTime) {
        int currentTime = 0;
        int remainingTime = Integer.MAX_VALUE;
        int idx = -1;
        Process currentProcess = null;
        List<Process> srtfOrder = new ArrayList<>(); 
        List<String> executionOrder = new ArrayList<>();
        processes.sort(Comparator.comparingInt(p -> p.burstTime));
        boolean flag = false;
        while (true) {
            for (int i = 0; i < processes.size(); i++) {
                Process p = processes.get(i);
                if (p.arrivalTime <= currentTime && !p.executed && p.burstTime < remainingTime) {
                     if (currentProcess != null && currentProcess != p) {
                         executionOrder.add("Process " + currentProcess.name + " interrupted at time " + currentTime);
                     }
                    flag = true;
                    idx = i;
                    remainingTime = p.burstTime;
                    currentProcess = p;
                    executionOrder.add("Process " + p.name + " started at time " + currentTime);
                }
            }
            if(flag){
                Process pro = processes.get(idx);
                srtfOrder.add(pro);
                if (pro.startTime == -1) {
                    pro.startTime = currentTime;
                }
                flag = false;
            }
            

            if (currentProcess == null) {
                boolean allExecuted = true;
                for (Process p : processes) {
                    if (!p.executed) {
                        currentTime++;
                        allExecuted = false;
                        break;
                    }
                }
                if (allExecuted) {
                    break;
                }
            } else {
                currentProcess.burstTime--;
                remainingTime--;
                currentTime++;
                if (currentProcess.burstTime == 0) {
                    currentProcess.executed = true;
                    currentProcess.finishTime = currentTime;
                    currentProcess.turnaroundTime = currentProcess.finishTime - currentProcess.arrivalTime;
                    currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.original_burstTime;
                       executionOrder.add("Process " + currentProcess.name + " ended at time " + currentTime);
                    currentProcess = null;
                    remainingTime = Integer.MAX_VALUE;
                }
            }
        }

        System.out.println("Shortest Remaining Time First Execution order:");
        for (String event : executionOrder) {
            System.out.println(event);
        }
    
        calculateAverages(srtfOrder);
       
    }

    // Non-preemptive Priority Scheduling
    public static void priorityScheduler(List<Process> processes, int contextSwitchingTime) {
        List<Process> priorityOrder = new ArrayList<>();
        List<Process> waitingQueue = new ArrayList<>();
        int currentTime = 0;
        processes.sort(Comparator.comparingInt(p -> p.priority));
        while (!processes.isEmpty() || !waitingQueue.isEmpty()) {
            Iterator<Process> processIterator = processes.iterator();
            while (processIterator.hasNext()) {
                Process p = processIterator.next();
                if (p.arrivalTime <= currentTime && !p.executed) {
                    waitingQueue.add(p);
                    processIterator.remove();
                }
            }
            if (!waitingQueue.isEmpty()) {
                waitingQueue.sort(Comparator.comparingInt(p -> p.priority));
                Process shortest = waitingQueue.remove(0);
                shortest.startTime = currentTime;
                shortest.finishTime = currentTime + shortest.burstTime;
                shortest.turnaroundTime = shortest.finishTime - shortest.arrivalTime;
                shortest.waitingTime = shortest.turnaroundTime - shortest.burstTime;
                currentTime = shortest.finishTime;
                shortest.executed = true;
                priorityOrder.add(shortest);
            } else if (!processes.isEmpty()) {
                currentTime++;
            }
        }


        System.out.println("\n" + "Non-preemptive Priority Scheduling" + " Execution Order:");
        for (Process p : priorityOrder) {
            System.out.println("Process " + p.name + " (" + p.color + ")" + ":\t" + p.startTime + " ---- " + p.finishTime );
        }

        calculateAverages(priorityOrder);
    }


    // AGScheduler
    public static void AgScheduler(List<Process> processes, int contextSwitchingTime) {
        int currentTime = 0;
        int AGfactor = Integer.MAX_VALUE;
        int idx = -1;
        Process currentProcess = null;
        List<Process> AgScheduler = new ArrayList<>(); 
        List<String> executionOrder = new ArrayList<>();
        processes.sort(Comparator.comparingInt(p -> p.AGfactor));
        boolean flag = false;
        for (int i = 0; i < processes.size(); i++) {
                Process p = processes.get(i);
                int randomNumber = getRandomNumber(0, 20);
                if(randomNumber < 10)
                {
                    p.AGfactor = randomNumber + p.burstTime + p.arrivalTime;
                }
                else if(randomNumber > 10)
                {
                    p.AGfactor = 10 + p.burstTime + p.arrivalTime;
                }
                else
                {
                    p.AGfactor = p.priority + p.burstTime + p.arrivalTime;
                }
            }
        while (true) {
            for (int i = 0; i < processes.size(); i++) {
                Process p = processes.get(i);
                if (p.arrivalTime <= currentTime && !p.executed && p.AGfactor < AGfactor) {
                    if (currentProcess != null && currentProcess != p) {
                        executionOrder.add("Process " + currentProcess.name + " interrupted at time " + currentTime);
                    }
                    if(currentProcess != null && currentProcess.changeable_timeQuantum == 0 && !currentProcess.executed)
                    {
                        int mean = calcMean(processes,currentTime);
                        currentProcess.timeQuantum += mean;
                        currentProcess.changeable_timeQuantum = currentProcess.timeQuantum;
                        currentProcess.DonePreemptive = false;
                    }
                    else if(currentProcess != null && currentProcess.changeable_timeQuantum != 0 && !currentProcess.executed)
                    {
                        currentProcess.timeQuantum += currentProcess.changeable_timeQuantum;
                        currentProcess.changeable_timeQuantum = currentProcess.timeQuantum;
                        currentProcess.DonePreemptive = false;
                    }
                    flag = true;
                    idx = i;
                    AGfactor = p.AGfactor;
                    currentProcess = p;
                    executionOrder.add("Process " + p.name + " started at time " + currentTime);
                }
            }
            if(flag){
                Process pro = processes.get(idx);
                AgScheduler.add(pro);
                if (pro.startTime == -1) {
                    pro.startTime = currentTime;
                }
                flag = false;
            }
            else if(currentProcess != null &&!flag && currentProcess.changeable_timeQuantum == 0 && !currentProcess.executed)
            {
                boolean flag2 = false;
                boolean checkArrivals = false;
                for (int i = 0; i < processes.size(); i++) {
                    Process p = processes.get(i);
                    if(p != currentProcess && p.arrivalTime <= currentTime && !p.executed)
                    {
                        checkArrivals = true;
                        break;
                    }
                }

                if(checkArrivals)
                {
                    AGfactor = Integer.MAX_VALUE;
                    for (int i = 0; i < processes.size(); i++) {
                        Process p = processes.get(i);
                        if(p != currentProcess && p.AGfactor < AGfactor)
                        {
                            if (currentProcess != null && currentProcess != p) {
                                executionOrder.add("Process " + currentProcess.name + " interrupted at time " + currentTime);
                            }
                            flag2 = true;
                            idx = i;
                            AGfactor = p.AGfactor;
                            currentProcess = p;
                            executionOrder.add("Process " + p.name + " started at time " + currentTime);
                        }
                    }
                }
                else
                {
                    if(currentProcess.changeable_timeQuantum == 0 && !currentProcess.executed)
                    {
                        int mean = calcMean(processes,currentTime);
                        currentProcess.timeQuantum += mean;
                        currentProcess.changeable_timeQuantum = currentProcess.timeQuantum;
                        currentProcess.DonePreemptive = false;
                    }
                }
                
                if(flag2)
                {
                    Process pro = processes.get(idx);
                    AgScheduler.add(pro);
                    if (pro.startTime == -1) {
                        pro.startTime = currentTime;
                    }
                    flag2 = false;
                }

            }

            if (currentProcess == null) {
                boolean allExecuted = true;
                for (Process p : processes) {
                    if (!p.executed) {
                        currentTime++;
                        allExecuted = false;
                        break;
                    }
                }
                if (allExecuted) {
                    break;
                }
            } else {
                int halfQuantum = (int) Math.ceil(currentProcess.timeQuantum / 2);
                if(!currentProcess.DonePreemptive)
                {
                    currentProcess.burstTime-=halfQuantum;
                    currentTime+=halfQuantum;
                    currentProcess.changeable_timeQuantum-=halfQuantum;
                    currentProcess.DonePreemptive = true;
                }
                else
                {
                    currentProcess.changeable_timeQuantum--;
                    currentProcess.burstTime--;
                    currentTime++;
                }
                if (currentProcess.burstTime <= 0) {
                    currentProcess.changeable_timeQuantum = 0;
                    currentProcess.burstTime = 0;
                    currentProcess.executed = true;
                    currentProcess.finishTime = currentTime;
                    currentProcess.turnaroundTime = currentProcess.finishTime - currentProcess.arrivalTime;
                    currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.original_burstTime;
                    executionOrder.add("Process " + currentProcess.name + " ended at time " + currentTime);
                    currentProcess = null;
                    AGfactor = Integer.MAX_VALUE;
                }
            }
        }

        System.out.println("AgScheduler Execution order:");
        for (String event : executionOrder) {
            System.out.println(event);
        }
    
        calculateAverages(AgScheduler);
       
    }

    public static int getRandomNumber(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public static int calcMean(List<Process> processes, int currentTime)
    {
        int mean = 0;
        int counter = 0;
        for (Process p : processes) {
            if(p.arrivalTime <= currentTime)
            {
                mean += p.timeQuantum;
                counter++;
            }
        }
        mean = (int) Math.ceil(0.1*(mean/counter)) ;
        return mean;
    }

    // Calcu avg wait time and avg turnaround time
    public static void calculateAverages(List<Process> order) {
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;
        int counter = 0;
        
        for (Process p : order) {

            if(!p.calculated){
                counter++;
                p.calculated = true;
                System.out.println("\n"+p.name +" ("+p.color+")" +" Turnaround Time: "+p.turnaroundTime);
                System.out.println("\n"+p.name +" ("+p.color+")" +" Waiting Time: "+p.waitingTime);
                totalWaitingTime += p.waitingTime;
                totalTurnaroundTime += p.turnaroundTime;
            }
        }

        double avgWaitingTime = (double) totalWaitingTime / counter;
        double avgTurnaroundTime = (double) totalTurnaroundTime / counter;

        System.out.println("\nAverage Waiting Time: " + avgWaitingTime);
        System.out.println("Average Turnaround Time: " + avgTurnaroundTime);
    }
}
