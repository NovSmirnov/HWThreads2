import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    private static Map<Integer, Long> base = new ConcurrentHashMap<>(); // ���� ������� ��� �������
    private static Map<Integer, Long> usedNumbers = new ConcurrentHashMap<>(); // ���� ���������� �������
    private static Worker[] threads; // �������� �������
    private static int numOfWorkers; //���������� ���������� ����-������
    private static int workerNum; //������� ��������
    private static int quantOfNumbers; //������ ���������� ������� � ����������� (������ ���������� ������� �� ��������)

    public static void main(String[] args) throws InterruptedException {
        numOfWorkers = 5;
        quantOfNumbers = 1000000;

        Main worker = new Main();
        worker.createBase();
        System.out.println("������ �������� ���� ���������� ������� �� ������ ������: " + base.size());
        System.out.println("������ ����, � ������� ����� ���������� ����������� ������ �� ������ ������: " + usedNumbers.size());
        threads = new Worker[numOfWorkers];

        for (workerNum = 0; workerNum < numOfWorkers; workerNum++) {
            threads[workerNum] = new Worker();
            threads[workerNum].start();
            threads[workerNum].join();
        }

        System.out.println();
        System.out.println("������ �������� ���� ���������� ������� ����� ��������� ������: " + base.size());
        System.out.println("������ ����, � ������� ����� ���������� ����������� ������ ����� ��������� ������: " + usedNumbers.size());
        System.out.println();
        System.out.println("������ ���������!");
    }

    // ������ ���� ������������ ���������� ������� ��� ���������� ������ ����-������
    public void createBase() throws InterruptedException {
        Set<Long> telephoneNumbers = new HashSet<>(quantOfNumbers);
        long number;
        for (int i = 0; i < quantOfNumbers; i++) {
            number = (long) (Math.random() * 10000000000L);
            telephoneNumbers.add(number);
        }
        Integer index = 0;
        for (long num : telephoneNumbers) {
            base.put(index, num);
            index++;
        }
    }

    static class Worker extends Thread{

        @Override
        public void run() {

            int startWork = quantOfNumbers / numOfWorkers * (workerNum + 1) - (quantOfNumbers / numOfWorkers); // ������ ��������� ��� ������ ������
            int finishWork = quantOfNumbers / numOfWorkers * (workerNum + 1); // ��������� ��������� ��� ������ ������
            for (Integer index = startWork; index < finishWork; index++) {
                Long telNumber = base.get(index);
                try {
                    usedNumbers.put(index, telNumber);
                } catch (NullPointerException e) {
                    // ����� ������� ������ �����, �.�. � ��������� ������, ���� ������ �� ����� ������������ � ����� ������������ ����������
                }
                base.remove(index);
            }
        }
    }
}
