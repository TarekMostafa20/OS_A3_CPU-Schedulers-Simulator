import java.util.*;

class Process {
    String name;
    String color;
    int arrivalTime;
    int burstTime;
    int original_burstTime;
    int priority;
    int startTime;
    int finishTime;
    int waitingTime;
    int turnaroundTime;
    boolean executed;
    boolean calculated;

    public Process(String name, String color, int arrivalTime, int burstTime, int priority) {
        this.name = name;
        this.color = color;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.original_burstTime = burstTime;
        this.priority = priority;
        this.startTime = -1;
        this.finishTime = 0;
        this.waitingTime = 0;
        this.turnaroundTime = 0;
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

            processes.add(new Process(name, color, arrivalTime, burstTime, priority));
        }

        //sjfScheduler(new ArrayList<>(processes), contextSwitchingTime); DONE
        //srtfScheduler(new ArrayList<>(processes), contextSwitchingTime);DONE
        //priorityScheduler(new ArrayList<>(processes), contextSwitchingTime);DONE

        scanner.close();
    }

    // (1)

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
                shortest.finishTime = currentTime + shortest.burstTime;
                shortest.turnaroundTime = shortest.finishTime - shortest.arrivalTime + contextSwitchingTime;
                shortest.waitingTime = shortest.turnaroundTime - shortest.burstTime;
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

        calculateAverages(sjfOrder, "Shortest-Job First ");
    }






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

        System.out.println("Execution order:");
        for (String event : executionOrder) {
            System.out.println(event);
        }
    
        calculateAverages(srtfOrder, "Shortest Remaining Time First ");
       
    }

    // (3)
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

        System.out.println("\n" + "Shortest-Job First" + " Execution Order:");
        for (Process p : priorityOrder) {
            System.out.println("Process " + p.name + " (" + p.color + ")" + ":\t" + p.startTime + " ---- " + p.finishTime );
        }

        calculateAverages(priorityOrder, "Non-preemptive Priority Scheduling");
    }

    // Calcu avg wait time and avg turnaround time
    public static void calculateAverages(List<Process> order, String scheduler) {
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;
        int counter = 0;
        System.out.println("\n" + scheduler + " Execution Order:");
        for (Process p : order) {
            System.out.println("Process " + p.name + " (" + p.color + ")" + ":\t" + p.startTime + " ---- " + p.finishTime );
            if(!p.calculated){
                counter++;
                p.calculated = true;
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
