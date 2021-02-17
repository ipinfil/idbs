package ui;

import rdg.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;

/**
 * Used to print info. Used by User Interface class.
 */

public class UIPrinter {
    private static final UIPrinter INSTANCE = new UIPrinter();
    private UIPrinter() {}
    public static UIPrinter getInstance() {return INSTANCE;}

    /**
     * Most of methods are pretty straight forward, they print stuff.
     * Most of methods, that print more rows are paginated.
     */

    public void printAllStudents() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int response = 1;
        System.out.println("--------------------- VYPIS VSETKYCH STUDENTOV --------------------------");
        System.out.printf("%-18s%-18s\n", "MENO", "PRIEZVISKO");

        while(true) {
            List<Account> accs = AccountFinder.getInstance().findAllPaginated(response);
        if (! accs.isEmpty()) {
            for (Account acc : accs) {
                printStudent(acc);
            }

            System.out.println("STRANA: " + response + " PRVKY: " + (response - 1) * 20 + " - " + ((response - 1) * 20 + accs.size()) +" PRE DALSIU STRANU STLACTE ENTER | PRE KONKRETNU STRANU NAPISTE CISLO | PRE KONIEC NAPISTE 0");
        } else {
            System.out.println("STRANA: " + response + " JE PRAZDNA");
            return;
        }

        String responseString = br.readLine();
        if (responseString.isEmpty()) {
            response++;
        } else if (responseString.equals("0")) {
            return;
        } else response = Integer.parseInt(responseString);
        }

    }

    public void printStudent(Account acc) {
        System.out.printf("%-18s%-18s\n", acc.getFirstName(), acc.getLastName());
    }

    public void printStudentCredit() throws IOException, SQLException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Vloz id studenta:");
        int accId = Integer.parseInt(br.readLine());
        Account acc = AccountFinder.getInstance().findById(accId);
        if (acc == null) {
            System.out.println("Takyto student neexistuje");
            return;
        }
        System.out.println("Stav uctu uzivatela " + acc.getFirstName() + " " + acc.getLastName() + ": " + acc.getCredit() + " eur.");
    }

    public void printLast10Operations() throws SQLException, IOException {
        System.out.println("--------------------- VYPIS POSLEDNYCH 10 OPERACII --------------------------");
        for (Operation op : OperationFinder.getInstance().findLastN(10)) {
            System.out.println("ID Konta: " + op.getAccId() + " Hodnota: " + op.getAmount());
        }
        System.out.println("Stlacte ENTER pre pokracovanie.");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        br.readLine();
    }

    public void printAllApplications() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Z akeho roku si prajete zobrazit ziadosti o ubytovanie?");
        Year year = YearFinder.getInstance().findByValue(Integer.parseInt(br.readLine()));
        if (year == null) {
            System.out.println("Takyto rok neexistuje");
            return;
        }
        int response = 1;

        System.out.println("--------------------- VYPIS VSETKYCH ZIADOSTI --------------------------");
        System.out.printf("%-18s%-18s%-18s%-18s\n","ID ZIADOSTI","MENO PRIEZVISKO", "STAV", "ID ZMLUVY");

        while (true) {

            List<Application> apps = ApplicationFinder.getInstance().findByYearPaginated(year.getId(), response);
            if (! apps.isEmpty()) {
                for (Application app : apps) {
                    Account acc = AccountFinder.getInstance().findById(app.getAccId());
                    System.out.printf("%-18s%-18s%-18s%-18s\n", "Ziadost " + app.getId(), " od " + acc.getFirstName() + " " + acc.getLastName(), "so stavom " + app.getStatus(), "na zmluvu " + app.getContractId());
                }
            } else {
                System.out.println("STRANA: " + response + " JE PRAZDNA");
                return;
            }

            System.out.println("STRANA: " + response + " PRVKY: " + (response - 1) * 20 + " - " + ((response - 1) * 20 + apps.size()) +" PRE DALSIU STRANU STLACTE ENTER | PRE KONKRETNU STRANU NAPISTE CISLO | PRE KONIEC NAPISTE 0");
            String responseString = br.readLine();
            if (responseString.isEmpty()) {
                response++;
            } else if (responseString.equals("0")) {
                return;
            } else response = Integer.parseInt(responseString);
        }

    }

    public void printApplication(Application app) throws SQLException {
        System.out.println("\t\tID ZIADOSTI\t\t|\t\tMENO\t\t|\t\tPRIEZVISKO\t\t|\t\tROK\t\t|\t\tSTAV\t\t|\t\tID ZMLUVY\t\t");
        Account acc = AccountFinder.getInstance().findById(app.getAccId());
        Year y = YearFinder.getInstance().findById(app.getYearId());
        System.out.println("Ziadost " + app.getId() + " od " + acc.getFirstName() + " " + acc.getLastName() + " z roku " + y.getValue() + " so stavom " + app.getStatus() + " na zmluvu " + app.getContractId());
    }

    public void printStudentContracts() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Vloz id studenta:");
        int accId = Integer.parseInt(br.readLine());

        System.out.println("--------------------- VYPIS VSETKYCH ZMLUV STUDENTA --------------------------");
        System.out.printf("%-18s%-25s%-18s%-18s%-18s\n","ID ZMLUVY", "STAV", "PLATNA OD", "PLATNA DO", "ID IZBY");
        List<Year> years = YearFinder.getInstance().findAll();
        int response = 1;

        while (true) {
            List<Contract> contracts = ContractFinder.getInstance().findByAccIdPaginated(accId, response);

            if (! contracts.isEmpty()) {
                for (Contract c : contracts) {
                    System.out.printf("%-18s%-25s%-18s%-18s%-18s\n", c.getId(), c.getStatus(), c.getValidSince(), c.getValidUntil(), c.getRoomId());
                }
            } else {
                System.out.println("STRANA: " + response + " JE PRAZDNA");
                return;
            }

            System.out.println("STRANA: " + response + " PRVKY: " + (response - 1) * 20 + " - " + ((response - 1) * 20 + contracts.size()) +" PRE DALSIU STRANU STLACTE ENTER | PRE KONKRETNU STRANU NAPISTE CISLO | PRE KONIEC NAPISTE 0");
            String responseString = br.readLine();

            if (responseString.isEmpty()) {
                response++;
            } else if (responseString.equals("0")) {
                return;
            } else response = Integer.parseInt(responseString);
        }

    }

    public void printAverageOccupancyStatistics() throws SQLException {
        List<AverageDailyRoomOccupation> stats = AverageDailyRoomOccupationFinder.getInstance().findAll();

        System.out.println("\t\tKAPACITA\t\t|\t\tPRIEMERNA DENNA OBSADENOST\t\t");
        for (AverageDailyRoomOccupation avg : stats) {
            System.out.println("\t\t\t" + avg.getCapacity() + "\t\t\t|\t\t\t" + avg.getAvg() + "\t\t");
        }

    }

    public void printMostUnpopularRoomsStatistics() throws IOException, SQLException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Zadajte kolko izieb podla poradia si prajete zobrazit: ");
        int n = Integer.parseInt(br.readLine());

        System.out.println("--------------------- STATISTIKA NEPOPULARITY --------------------------");
        System.out.printf("%-10s%-10s%-18s\n","PORADIE","ID IZBY", "POCET PREUBYTOVANI");
        List<Unpopularity> unpopularities = UnpopularityFinder.getInstance().findN(n);
        for (Unpopularity u : unpopularities) {
            System.out.printf("%-10s%-10s%-18s\n", u.getOrder(), u.getRoom_id(), u.getNumberOfReaccommodations());
        }
        System.out.println("Pre pokracovanie do hlavneho menu stlacte ENTER.");
        br.readLine();

    }

    public void printApplicationsOfStudents() throws SQLException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Vloz id studenta:");
        int accId = Integer.parseInt(br.readLine());

        Account acc = AccountFinder.getInstance().findById(accId);


        System.out.println("--------------------- VYPIS VSETKYCH ZIADOSTI STUDENTA --------------------------");
        System.out.printf("%-18s%-18s%-18s%-18s\n","ID ZIADOSTI","MENO PRIEZVISKO", "STAV", "ID ZMLUVY");
        List<Year> years = YearFinder.getInstance().findAll();
        int response = 1;

        while (true) {
                List<Application> apps = ApplicationFinder.getInstance().findByAccIdPaginated(accId, response);

                if (! apps.isEmpty()) {
                    for (Application app : apps) {
                        Year y = null;
                        for (Year yr : years) {
                            if (yr.getId() == app.getYearId()) {
                                y = yr;
                                break;
                            }
                        }
                        System.out.printf("%-18s%-18s%-18s%-18s\n", app.getId(), acc.getFirstName() + " " + acc.getLastName(), app.getStatus(), app.getContractId());
                    }
                } else {
                    System.out.println("STRANA: " + response + " JE PRAZDNA");
                    return;
                }

            System.out.println("STRANA: " + response + " PRVKY: " + (response - 1) * 20 + " - " + ((response - 1) * 20 + apps.size()) +" PRE DALSIU STRANU STLACTE ENTER | PRE KONKRETNU STRANU NAPISTE CISLO | PRE KONIEC NAPISTE 0");
            String responseString = br.readLine();

            if (responseString.isEmpty()) {
                response++;
            } else if (responseString.equals("0")) {
                return;
            } else response = Integer.parseInt(responseString);
            }
        }

    public void printPointInfoOfStudents() throws SQLException, IOException {

        System.out.println("Z akeho roku chcete vypisat body studentov?");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Year yr = YearFinder.getInstance().findByValue(Integer.parseInt(br.readLine()));


        int response = 1;
        List<PointInfo> points;

        System.out.println("--------------------- VYPIS BODOV STUDENTOV V ROKU " + yr.getValue() + " --------------------------");
        System.out.printf("%-18s%-18s%-18s\n", "POCET BODOV", "MENO", "PRIEZVISKO");

        while (true) {

            points = PointInfoFinder.getInstance().findPaginatedByYear(yr.getId(), response);

            if (!points.isEmpty()) {
                for (PointInfo p : points) {
                    Account acc = AccountFinder.getInstance().findById(p.getAccountId());
                    System.out.printf("%-18s%-18s%-18s\n",p.getAmount(), acc.getFirstName(), acc.getLastName());
                }
            System.out.println("STRANA: " + response + " PRVKY: " + (response - 1) * 20 + " - " + ((response - 1) * 20 + points.size()) +" PRE DALSIU STRANU STLACTE ENTER | PRE KONKRETNU STRANU NAPISTE CISLO | PRE KONIEC NAPISTE 0");
            } else {
                System.out.println("STRANA: " + response + " JE PRAZDNA");
                return;
            }

            String responseString = br.readLine();
            if (responseString.isEmpty()) {
                response++;
            } else if (responseString.equals("0")) {
                return;
            } else response = Integer.parseInt(responseString);

        }

    }

    public void printStudentRoomPriorities() throws IOException, SQLException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Zadajte id ziadosti, ktorej priority pri vybere izieb si prajete vypisat: ");
        int appId = Integer.parseInt(br.readLine());

        int response = 1;
        List<RoomPriority> priorities;

        System.out.println("--------------------- VYPIS PREFERENCII IZIEB ZIADOSTI S ID " + appId + " --------------------------");
        System.out.printf("%-10s%-10s\n", "PORADIE", "ID IZBY");

        while (true) {
            priorities = RoomPriorityFinder.getInstance().findPrioritiesForApplicationPaginated(appId, response);

            if (!priorities.isEmpty()) {
                for (RoomPriority p : priorities) {
                    System.out.printf("%-10s%-10s\n", p.getOrd(), p.getRoomId());
                }
                System.out.println("STRANA: " + response + " PRVKY: " + (response - 1) * 20 + " - " + ((response - 1) * 20 + priorities.size()) +" PRE DALSIU STRANU STLACTE ENTER | PRE KONKRETNU STRANU NAPISTE CISLO | PRE KONIEC NAPISTE 0");
            } else {
                System.out.println("STRANA: " + response + " JE PRAZDNA");
                return;
            }

            String responseString = br.readLine();
            if (responseString.isEmpty()) {
                response++;
            } else if (responseString.equals("0")) {
                return;
            } else response = Integer.parseInt(responseString);

        }
    }

}
