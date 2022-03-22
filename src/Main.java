import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    private static Map<Integer, Long> base = new ConcurrentHashMap<>(); // База номеров для обзвона
    private static Map<Integer, Long> usedNumbers = new ConcurrentHashMap<>(); // База обзвоненых номеров
    private static Worker[] threads; // Массивив потоков
    private static int numOfWorkers; //Количество работников колл-центра
    private static int workerNum; //Текущий работник
    private static int quantOfNumbers; //Предел телефонных номеров в справочнике (точное количество заранее не известно)

    public static void main(String[] args) throws InterruptedException {
        numOfWorkers = 5;
        quantOfNumbers = 1000000;

        Main worker = new Main();
        worker.createBase();
        System.out.println("Размер исходной базы телефонных номеров до начала работы: " + base.size());
        System.out.println("Размер базы, в которую будут помещаться обзвоненные номера до начала работы: " + usedNumbers.size());
        threads = new Worker[numOfWorkers];

        for (workerNum = 0; workerNum < numOfWorkers; workerNum++) {
            threads[workerNum] = new Worker();
            threads[workerNum].start();
            threads[workerNum].join();
        }

        System.out.println();
        System.out.println("Размер исходной базы телефонных номеров после окончания работы: " + base.size());
        System.out.println("Размер базы, в которую будут помещаться обзвоненные номера после окончания работы: " + usedNumbers.size());
        System.out.println();
        System.out.println("Работа выполнена!");
    }

    // Создаём базу оригинальных телефонных номеров для дальнейшей работы колл-центра
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

            int startWork = quantOfNumbers / numOfWorkers * (workerNum + 1) - (quantOfNumbers / numOfWorkers); // начало диапазона для работы потока
            int finishWork = quantOfNumbers / numOfWorkers * (workerNum + 1); // окончание диапазона для работы потока
            for (Integer index = startWork; index < finishWork; index++) {
                Long telNumber = base.get(index);
                try {
                    usedNumbers.put(index, telNumber);
                } catch (NullPointerException e) {
                    // Здесь оставим пустое место, т.к. в последнем потоке, ряда ключей не будет существовать и будут вываливаться исключения
                }
                base.remove(index);
            }
        }
    }
}
