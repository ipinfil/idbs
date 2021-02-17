package ui;

import main.Initializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

/**
 * Prints main and sub-menus. Uses UIHandler for interaction with user and UIPrinter to print all kinds of information.
 */

public class UserInterface {
    private boolean running;
    private final UIHandler uiHandler = UIHandler.getInstance();
    private final UIPrinter uiPrinter = UIPrinter.getInstance();

    /**
     * Starts printing main menu while running is true.
     * @author Alexander Simko - example project from DB2 course
     */
    public void start() throws IOException {
        running = true;

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        while (running) {
            showMainMenu();

            System.out.println();

            String in = input.readLine();

            if (in == null) break;

            executeCommand(in);
        }
    }


    public void showMainMenu() {
        System.out.println("--------------------- HLAVNE MENU ----------------------");
        System.out.println("1 | Praca s kontami studentov"); //vypis zoznam, insert, update, delete, vypis kredit, vlozit kredit
        System.out.println("2 | Praca so ziadostami o ubytovanie"); //print zoznam, insert, delete, update
        System.out.println("3 | Vypis zoznam ubytovacich zmluv"); //print zoznam studentovych zmluv
        System.out.println("4 | Vypis poslednych 10 operacii"); //vypisat poslednych 10 operacii
        System.out.println("5 | Praca s preferenciami studentov pri vybere izieb"); //vypis, insert, update, delete
        System.out.println("6 | Praca s bodmi"); //vypis zoznam bodov v danom roku, nastav body studentovi
        System.out.println("7 | Statistiky");
        System.out.println("8 | Stop");
        System.out.println("9 | Potvrdit zmluvu");
        System.out.println("10 | Stiahnutie mesacnych poplatkov");
        System.out.println("11 | Preubytovanie");
        System.out.println("X | Vytvor + vygeneruj databazu");
    }

    /**
     * Used to shut down the application.
     */
    public void stop() {
        running = false;
    }

    /**
     * Prints sub-menus or calls UIHandler/UIPrinter to execute commands.
     * @param cmd - command to execute (number / X)
     */
    public void executeCommand(String cmd) {
        try {
            switch (cmd) {
                case "1":
                    studentAccountMenu();
                    break;
                case "2":
                    applicationMenu();
                    break;
                case "3":
                    uiPrinter.printStudentContracts();
                    break;
                case "4":
                    uiPrinter.printLast10Operations();
                    break;
                case "5":
                    roomPriorityMenu();
                    break;
                case "6":
                    pointInfoMenu();
                    break;
                case "7":
                    statisticsMenu();
                    break;
                case "8":
                    stop();
                    break;
                case "X":
                    initialize();
                    break;
                case "9":
                    uiHandler.accommodate();
                    break;
                case "10":
                    uiHandler.withdrawMonthlyPayment();
                    break;
                case "11":
                    uiHandler.reaccommodate();
                    break;
                default:
                    System.out.println("Takato moznost neexistuje.");
                    break;
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Used to create database and generate data from main menu.
     */
    private void initialize() {
        try {
            Initializer.createDatabase();
            Initializer.generateDatabase();

            System.out.println("Databaza bola uspesne vytvorena a data boli vygenerovane;");
        } catch (SQLException | IOException e) {
            System.out.println("Databazu sa nepodarilo vytvorit.");
        }
    }

    /**
     * Prints menu that offers options to work with accounts and handles further action by calling UIPrinter/UIHandler methods to execute tasks.
     */
    private void studentAccountMenu() throws IOException, SQLException {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        while (running) {
            System.out.println();
            System.out.println("--------------------- STUDENTSKE KONTA ----------------------");
            System.out.println("1 | Vypis zoznam studentov");
            System.out.println("2 | Pridaj studenta");
            System.out.println("3 | Aktualizuj studenta");
            System.out.println("4 | Odstran studenta");
            System.out.println("5 | Vypis kredit");
            System.out.println("6 | Vlozit kredit");
            System.out.println("7 | Hlavne menu");
            System.out.println("8 | Stop");

            System.out.println();

            String in = input.readLine();

            if (in == null) break;

            switch (in) {
                case "1":   uiPrinter.printAllStudents(); break;
                case "2":   uiHandler.addStudent(); break;
                case "3":   uiHandler.updateStudent(); break;
                case "4":   uiHandler.deleteStudent(); break;
                case "5":   uiPrinter.printStudentCredit(); break;
                case "6":   uiHandler.depositCredit(); break;
                case "7":   start(); break;
                case "8":   stop(); break;

                default:    System.out.println("Takato moznost neexistuje."); break;
            }
        }
    }

    /**
     * Prints menu that offers options to work with applications and handles further action by calling UIPrinter/UIHandler methods to execute tasks.
     */
    private void applicationMenu() throws IOException, SQLException {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        while (running) {
            System.out.println();
            System.out.println("--------------------- ZIADOSTI O UBYTOVANIE ----------------------");
            System.out.println("1 | Vypis zoznam ziadosti");
            System.out.println("2 | Vytvor ziadost");
            System.out.println("3 | Uprav ziadost");
            System.out.println("4 | Odstran ziadost");
            System.out.println("5 | Vyhladaj vsetky ziadosti studenta");
            System.out.println("6 | Pridelenie volnych izieb");
            System.out.println("7 | Hlavne menu");
            System.out.println("8 | Stop");

            System.out.println();

            String in = input.readLine();

            if (in == null) break;

            switch (in) {
                case "1":   uiPrinter.printAllApplications(); break;
                case "2":   uiHandler.insertApplication(); break;
                case "3":   uiHandler.updateApplication(); break;
                case "4":   uiHandler.deleteApplication(); break;
                case "5":   uiPrinter.printApplicationsOfStudents(); break;
                case "6":   uiHandler.assignRooms(); break;
                case "7":   start(); break;
                case "8":   stop(); break;

                default:    System.out.println("Takato moznost neexistuje."); break;
            }
        }
    }

    /**
     * Used to print statistics/reports.
     */
    private void statisticsMenu() throws IOException, SQLException {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        while (running) {
            System.out.println();
            System.out.println("--------------------- STATISTIKY ----------------------");
            System.out.println("1 | Priemerna denna obsadenost");
            System.out.println("2 | Najnepopularnejsie izby");
            System.out.println("3 | Hlavne menu");
            System.out.println("4 | Stop");
            System.out.println();

            String in = input.readLine();

            switch (in) {
                case "1":   uiPrinter.printAverageOccupancyStatistics(); break;
                case "2":   uiPrinter.printMostUnpopularRoomsStatistics(); break;
                case "3":   start(); break;
                case "4":   stop(); break;
                default:    System.out.println("Takato moznost neexistuje."); break;
            }

        }
    }

    /**
     * Prints menu that offers options to work with points (PointInfo class) and handles further action by calling UIPrinter/UIHandler methods to execute tasks.
     */
    private void pointInfoMenu() throws IOException, SQLException {

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        while (running) {
            System.out.println();
            System.out.println("--------------------- PRACA S BODMI ----------------------");
            System.out.println("1 | Nastavit studentovi body");
            System.out.println("2 | Vypis bodov studentov");
            System.out.println("3 | Hlavne menu");
            System.out.println("4 | Stop");
            System.out.println();

            String in = input.readLine();

            switch (in) {
                case "1":   uiHandler.insertPointInfo(); break;
                case "2":   uiPrinter.printPointInfoOfStudents(); break;
                case "3":   start(); break;
                case "4":   stop(); break;
                default:    System.out.println("Takato moznost neexistuje."); break;
            }

        }
    }
    /**
     * Prints menu that offers options to work with room_priorities and handles further action by calling UIPrinter/UIHandler methods to execute tasks.
     */
    private void roomPriorityMenu() throws IOException, SQLException {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        while (running) {
            System.out.println();
            System.out.println("--------------------- PRACA S PREFERENCIAMI IZIEB----------------------");
            System.out.println("1 | Vypíš preferované izby ziadosti");
            System.out.println("2 | Pridaj preferovanú izbu pre ziadost");
            System.out.println("3 | Odstráň preferovanú izbu");
            System.out.println("4 | Uprav preferovanú izbu (jej priorita)");
            System.out.println("5 | Hlavne menu");
            System.out.println("6 | Stop");
            System.out.println();

            String in = input.readLine();

            switch (in) {
                case "1":
                    uiPrinter.printStudentRoomPriorities();
                    break;
                case "2":
                    uiHandler.insertRoomPriority();
                    break;
                case "3":
                    uiHandler.deleteRoomPriority();
                    break;
                case "4":
                    uiHandler.updateRoomPriority();
                    break;
                case "5":
                    start();
                    break;
                case "6":
                    stop();
                    break;
                default:
                    System.out.println("Takato moznost neexistuje.");
                    break;
            }
        }
    }

}
