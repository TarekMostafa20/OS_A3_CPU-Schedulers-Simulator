import java.util.*;

class Process {
    String name;
    String color;
    int arrivalTime;
    int burstTime;
    int priority;
    int startTime;
    int finishTime;
    int waitingTime;
    int turnaroundTime;
    boolean executed;

    public Process(String name, String color, int arrivalTime, int burstTime, int priority) {
        this.name = name;
        this.color = color;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.startTime = 0;
        this.finishTime = 0;
        this.waitingTime = 0;
        this.turnaroundTime = 0;
        this.executed = false;
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
            System.out.println("Enter تفاصيل ي حبيبي تفاصيل for Process " + (i + 1) + ":");
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

        sjfScheduler(new ArrayList<>(processes), contextSwitchingTime);
        srtfScheduler(new ArrayList<>(processes), contextSwitchingTime);
        priorityScheduler(new ArrayList<>(processes), contextSwitchingTime);

        scanner.close();
    }

    // (1)
    public static void sjfScheduler(List<Process> processes, int contextSwitchingTime) {
        List<Process> sjfOrder = new ArrayList<>();
        List<Process> waitingQueue = new ArrayList<>();
        int currentTime = 0;

        while (!processes.isEmpty() || !waitingQueue.isEmpty()) {
            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && !p.executed) {
                    waitingQueue.add(p);
                }
            }

            if (!waitingQueue.isEmpty()) {
                waitingQueue.sort(Comparator.comparingInt(p -> p.burstTime));
                Process shortest = waitingQueue.remove(0);
                shortest.startTime = currentTime;
                shortest.finishTime = currentTime + shortest.burstTime;
                shortest.turnaroundTime = shortest.finishTime - shortest.arrivalTime;
                shortest.waitingTime = shortest.turnaroundTime - shortest.burstTime;
                currentTime = shortest.finishTime + contextSwitchingTime;
                shortest.executed = true;
                sjfOrder.add(shortest);
            } else {
                currentTime++;
            }
        }

        calculateAverages(sjfOrder, "Shortest-Job First ");
    }

    //(2)
    public static void srtfScheduler(List<Process> processes, int contextSwitchingTime) {
        List<Process> srtfOrder = new ArrayList<>();
        int currentTime = 0;

        while (true) {
            Process shortest = null;
            int shortestTime = Integer.MAX_VALUE;

            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && !p.executed && p.burstTime < shortestTime) {
                    shortest = p;
                    shortestTime = p.burstTime;
                }
            }

            if (shortest == null) {
                boolean flag = true;
                for (Process p : processes) {
                    if (!p.executed) {
                        currentTime++;
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    break;
                }
            } else {
                shortest.burstTime--;
                currentTime++;
                if (shortest.burstTime == 0) {
                    shortest.finishTime = currentTime;
                    shortest.turnaroundTime = shortest.finishTime - shortest.arrivalTime;
                    shortest.waitingTime = shortest.turnaroundTime - shortest.burstTime;
                    shortest.executed = true;
                    srtfOrder.add(shortest);
                }
            }
        }

        calculateAverages(srtfOrder, "Shortest Remaining Time First ");
    }

    // Non-preemptive Priority Scheduling
    public static void priorityScheduler(List<Process> processes, int contextSwitchingTime) {
        List<Process> priorityOrder = new ArrayList<>();
        List<Process> waitingQueue = new ArrayList<>();
        int currentTime = 0;

        while (!processes.isEmpty() || !waitingQueue.isEmpty()) {
            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && !p.executed) {
                    waitingQueue.add(p);
                }
            }

            if (!waitingQueue.isEmpty()) {
                waitingQueue.sort(Comparator.comparingInt(p -> p.priority));
                Process highestPriority = waitingQueue.remove(0);
                highestPriority.startTime = currentTime;
                highestPriority.finishTime = currentTime + highestPriority.burstTime;
                highestPriority.turnaroundTime = highestPriority.finishTime - highestPriority.arrivalTime;
                highestPriority.waitingTime = highestPriority.turnaroundTime - highestPriority.burstTime;
                currentTime = highestPriority.finishTime + contextSwitchingTime;
                highestPriority.executed = true;
                priorityOrder.add(highestPriority);
            } else {
                currentTime++;
            }
        }

        calculateAverages(priorityOrder, "Non-preemptive Priority Scheduling");
    }


    public static void calculateAverages(List<Process> order, String scheduler) {
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;

        System.out.println("\n" + scheduler + " Execution Order:");
        for (Process p : order) {  // Calcu avg wait time and avg turnaround time
            System.out.println("Process " + p.name + " (" + p.color + ")");
            totalWaitingTime += p.waitingTime;
            totalTurnaroundTime += p.turnaroundTime;
        }

        double avgWaitingTime = (double) totalWaitingTime / order.size();
        double avgTurnaroundTime = (double) totalTurnaroundTime / order.size();

        System.out.println("\nAverage Waiting Time: " + avgWaitingTime);
        System.out.println("Average Turnaround Time: " + avgTurnaroundTime);
    }
}
