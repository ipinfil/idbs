package ui;

import domainOperations.*;
import exceptions.*;
import rdg.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Used to handle almost all of the user interaction.
 */

public class UIHandler extends UserInterface {
    public static final UIHandler INSTANCE = new UIHandler();
    public static UIHandler getInstance() { return INSTANCE; }
    private UIHandler() {}
    final private UIPrinter ui_printer = UIPrinter.getInstance();

    /**
     * Students CRUD operations.
     */
    public void addStudent() throws IOException, SQLException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        Account acc = new Account();

        System.out.println("Krstne meno:");
        acc.setFirstName(br.readLine());
        System.out.println("Priezvisko:");
        acc.setLastName(br.readLine());
        acc.setCredit(BigDecimal.valueOf(0));

        acc.insert();

        System.out.println("Uspesne ste pridali studenta s id: " + acc.getId());
    }
    public void updateStudent() throws IOException, SQLException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Vlozte id studenta:");
        int accId = Integer.parseInt(br.readLine());
        Account acc = AccountFinder.getInstance().findById(accId);
        if (acc == null) {
            System.out.println("Takyto student neexistuje.");
            return;
        }

        ui_printer.printStudent(acc);

        System.out.println("Vlozte krstne meno:");
        acc.setFirstName(br.readLine());
        System.out.println("Vlozte priezvisko:");
        acc.setLastName(br.readLine());
        acc.update();

        System.out.println("Uspesne ste aktualizovali udaje studenta s id " + acc.getId());


    }
    public void deleteStudent() throws IOException, SQLException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Vloz id studenta:");
        int accId = Integer.parseInt(br.readLine());
        Account acc = AccountFinder.getInstance().findById(accId);
        if (acc == null) {
            System.out.println("Student s takymto ID neexistuje.");
            return;
        }

        acc.delete();

        System.out.println("Uspesne ste vymazali studenta z databazy.");


    }

    /**
     * Applications CRUD operations.
     */
    public void insertApplication() throws IOException, SQLException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        Application app = new Application();

        System.out.println("Zadajte id studenta, pre ktoreho chcete vytvorit ziadost o ubytovanie.");

        int accId = Integer.parseInt(br.readLine());
        Account acc = AccountFinder.getInstance().findById(accId);
        if (acc == null) {
            System.out.println("Takyto student neexistuje.");
            return;
        }
        ui_printer.printStudent(acc);

        Year y = YearFinder.getInstance().findLast();
        app.setYearId(y.getId());
        app.setStatus("Sent");
        app.setContractId(null);
        app.setAccId(acc.getId());

        app.insert();


        System.out.println("Uspesne ste vytvorili ziadost o ubytovanie s id: " + app.getId());
    }
    public void updateApplication() throws IOException, SQLException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Zadajte ID ziadosti, ktoru chcete upravit:");
        Application app = ApplicationFinder.getInstance().findById(Integer.parseInt(br.readLine()));
        if (app == null) {
            System.out.println("Takato ziadost neexistuje.");
        }
        else if (!app.getStatus().equals("Sent")) {
            System.out.println("Ziadost sa neda zmenit.");
        } else {
            System.out.println("Aky stav chcete pridelit tejto ziadosti?");
            String stat = br.readLine();

            app.setStatus(stat);
            try {
                app.update();
            } catch (SQLException e) {
                if (e.getSQLState().equals("23514")) {
                    System.out.println("Nespravna uprava ziadosti");
                }
                return;
            }
            System.out.println("Uspesne ste aktualizovali ziadost " + app.getId());
        }


    }
    public void deleteApplication() throws IOException, SQLException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Vloz id ziadosti:");
        int appId = Integer.parseInt(br.readLine());
        Application app = ApplicationFinder.getInstance().findById(appId);
        if (app == null) {
            System.out.println("Takato ziadost neexistuje");
            return;
        }
        else if (app.getStatus().equals("Sent")) {
            app.delete();
            System.out.println("Uspesne ste vymazali ziadost z databazy.");
        } else {
            System.out.println("Tato ziadost sa neda vymazat.");
        }

    }

    /**
     * Inserts points to a student in a certain year.
     */
    public void insertPointInfo() throws IOException, SQLException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Vloz id studenta:");
        int accId = Integer.parseInt(br.readLine());
        System.out.println("Pre aky rok chcete nastavit body studenta?");
        Year yr = YearFinder.getInstance().findByValue(Integer.parseInt(br.readLine()));

        if (yr == null) {
            System.out.println("Takyto rok neexistuje");
            return;
        }

        PointInfo pi = new PointInfo();
        pi.setAccountId(accId);
        pi.setYearId(yr.getId());
        System.out.println("Kolko bodov chcete dat studentovi v tomto roku?");
        pi.setAmount(Float.parseFloat(br.readLine()));
        try {
            pi.insert();
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                System.out.println("Tento student uz ma zadane body pre tento rok.");
            }

            if (e.getSQLState().equals("23503")) {
                System.out.println("Takyto student neexistuje.");
            }
            return;
        }

        System.out.println("Uspesne ste pridali body studentovi s id " + accId);
    }

    /**
     * Domain operation - deposits credit to a certain student.
     * depositCredit() handles all of the interaction with user.
     * depositCredit(int accId, BigDecimal amount) executes credit deposit by calling
     * CreditDeposit::depositCreditToStudent(int accId, BigDecimal amount)
     * and does all of the exception handling.
     */
    public void depositCredit() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Zadaj id studenta:");
        int accId = Integer.parseInt(br.readLine());
        System.out.println("Zadajte sumu, ktoru chcete vlozit:");
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(br.readLine()));

        depositCredit(accId, amount);

    }
    private void depositCredit(int accId, BigDecimal amount) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            CreditDeposit.getInstance().depositCreditToStudent(accId, amount);
            System.out.println("Uspesne ste vlozili prostriedky na ucet studenta.");
        } catch (SQLException e) {
            if (e.getSQLState().equals("40001")) { //Isolation error.
                System.out.println("Pocas vykonavania transakcie sa manipulovalo s datami, ktore ste chceli upravit. Zopakovat transakciu? a/n");
                if (br.readLine().equals("a")) { //Executes operation again.
                    depositCredit(accId, amount);
                }
            }
        } catch (StudentNotFoundException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Room Priority CRUD operations.
     */
    public void insertRoomPriority() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Zadajte ziadost, pre ktoru vytvarate prioritu: ");
        int appId = Integer.parseInt(br.readLine());

        System.out.println("Zadajte poradie izby podla preferencii: ");
        int order = Integer.parseInt(br.readLine());

        System.out.println("Zadajte ID izby, ktoru chcete zvolit: ");
        int roomId = Integer.parseInt(br.readLine());

        RoomPriority rp = new RoomPriority();
        rp.setOrd(order);
        rp.setApplicationId(appId);
        rp.setRoomId(roomId);
        try {
            rp.insert();
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                System.out.println("Uz existuje zaznam preferencie izby pre tuto ziadost s tymto poradim.");
                return;
            }

            if (e.getSQLState().equals("23514")) {
                System.out.println("Poradie izby musi byt > 0.");
                return;
            }
            e.printStackTrace();
        }
        System.out.println("Uspesne ste pridali prioritu izby s id " + rp.getId());

    }
    public void deleteRoomPriority() throws IOException, SQLException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Zadajte ID zaznamu priority izby, ktore si prajete vymazat: ");
        int rpId = Integer.parseInt(br.readLine());

        RoomPriority rp = RoomPriorityFinder.getInstance().findById(rpId);
        if (rp == null) {
            System.out.println("Neexistuje takyto zaznam v preferenciach izieb.");
            return;
        }
        rp.delete();
        System.out.println("Uspesne ste vymazali prioritu izby.");

    }
    public void updateRoomPriority() throws IOException, SQLException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Zadajte ID zaznamu priority izby, ktore si prajete upravit: ");
        int rpId = Integer.parseInt(br.readLine());

        RoomPriority rp = RoomPriorityFinder.getInstance().findById(rpId);
        if (rp == null) {
            System.out.println("Takyto zaznam neexistuje");
            return;
        }
        System.out.println("Ake poradie si prajete jej zvolit?");
        int order = Integer.parseInt(br.readLine());
        rp.setOrd(order);
        try {
            rp.update();
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                System.out.println("Nemozete mat dvakrat rovnake poradie preferencie izby.");
            }

            if (e.getSQLState().equals("23514")) {
                System.out.println("Poradie izby musi byt > 0.");
                return;
            }

            return;
        }
        System.out.println("Uspesne ste upravili prioritu izby.");
    }

    /**
     * Domain operation - assignes rooms to all of the students who applied for accommodation
     * and have >= points in this year.
     * assignRooms() handles all of the interaction with user.
     * assignRooms(int poinThreshold) executes room assignment by calling
     * RoomAssignment::initiate(int pointThreshold)
     * Prints number of deleted contracts (room has been assigned, but contract has not been signed),
     * number of new contracts (generates contracts for all of those who applied),
     * number of contracts, to which room has been assigned.
     */
    public void assignRooms() throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Zadajte pocet minimalny pocet bodov, pre ktory sa pridelia body v tomto ubytovacom kole.");
        int pointThreshold = Integer.parseInt(br.readLine());

        assignRooms(pointThreshold);
    }
    private void assignRooms(int pointThreshold) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            List<Integer> assignedInfo;
            assignedInfo = RoomAssignment.getInstance().initiate(pointThreshold);
            System.out.println("Pocet zrusenych nepodpisanych zmluv: " + assignedInfo.get(0) + " Pocet pridelenych izieb: " + assignedInfo.get(1));
        } catch (SQLException e) {
            if (e.getSQLState().equals("40001")) { //Isolation exception.
                System.out.println("Pocas vykonavania operacie sa manipulovalo s datami. Zopakovat operaciu? a/n");
                if (br.readLine().equals("a")) assignRooms(pointThreshold); //executes operation again
            }
        } catch (NotEnoughRoomsException e) {
            System.out.println("Nedostatok izieb, izby neboli pridelene vsetkym uchadzacom.");
        }
    }

    /**
     *Domain operation - contract sign. If contract has room asssigned and has not been signed yet,
     * contract is signed and credit is withdrawn from the tenant's account.
     * accommodate() handles all of the interaction with user.
     * accommodate(int contractId) executes accommodation by calling
     * Accommodate::signContract(int contractId) and does all exception handling.
     */
    public void accommodate() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Zadajte cislo zmluvy: ");
        int contractId = Integer.parseInt(br.readLine());

        accommodate(contractId);

    }
    private void accommodate(int contractId) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            Accommodate.getInstance().signContract(contractId);
            System.out.println("Uspesne ste podpisali zmluvu a bola vam stiahnuta prva platba z uctu.");

        } catch (SQLException e) {
            if (e.getSQLState().equals("40001")) { //Isolation exception.
                System.out.println("Pocas vykonavania operacie sa manipulovalo s datami. Zopakovat operaciu? a/n");
                if (br.readLine().equals("a")) accommodate(contractId); //Executes operation again.
            }
        } catch (ContractNotFoundException e) {
            System.out.println("Takato zmluvva neexistuje.");
        } catch (WrongContractFormatException e) { //Contract does not have status NULL.
            System.out.println("Tuto zmluvu nemozete podpisat."); //So it has already been signed or denied.
        } catch (NotEnoughCreditException e) {
            System.out.println("Nedostatok kreditu na ucte.");
        }
    }

    /**
     *Domain operation - monthly payment withdrawal. Withdraws monthly payments
     * from all of the current tennants.
     *
     */
    public void withdrawMonthlyPayment() throws IOException {
        System.out.println("Zacinam stahovanie poplatkov.");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        List<Integer> result;

        try {
            result = MonthlyPayment.getInstance().pay();
            System.out.println("Uspesnych platieb: " + result.get(0) + " Neuspesnych platieb (zablokovanych kard): " + result.get(1) + " Celkovy zisk: " + result.get(2));
        } catch (SQLException e) {
            if (e.getSQLState().equals("40001")) { //Isolation exception.
                System.out.println("Pocas vykonavania operacie sa manipulovalo s datami. Zopakovat operaciu? a/n");
                if (br.readLine().equals("a")) withdrawMonthlyPayment(); //Executes operation again.
            }
        }
    }

    /**
     *Domain operation - reaccommodation. If account has valid contract, he can apply to
     * be reaccommodate to another free room.
     * reaccommodate() handles all of the interaction with user.
     * reaccommodate(int accId, int roomId) executes reaccommodation by calling
     * Reaccommodate::initialize(int accId, int roomId) and does all of the exception handling.
     * Prints how much has student paid/received because of reaccommodation.
     */
    public void reaccommodate() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Zadajte ID studenta, ktory sa chce preubytovat: ");
        int accId = Integer.parseInt(br.readLine());
        System.out.println("Zadajte ID izby, na ktoru sa chce student preubytovat: ");
        int roomId = Integer.parseInt(br.readLine());
        reaccommodate(accId, roomId);
    }
    private void reaccommodate(int accId, int roomId) throws IOException {
        BigDecimal result = BigDecimal.valueOf(0);
        try {
            result = Reaccommodate.getInstance().initialize(accId, roomId);
        } catch (StudentNotFoundException e) {
            System.out.println("Takyto student neexistuje.");
            return;
        } catch (ReaccomodationNotPossibleException e) { //new room is the same as the previous one
            System.out.println(e.getMessage());
        } catch (StudentNotAccommodatedException e) {
            System.out.println("Tento student nema ziadnu aktivnu platnu ubytovaciu zmluvu alebo platna zmluva este nezacala platit.");
            return;
        } catch (RoomNotFreeException e) {
            System.out.println("Zvolena izba nie je dostupna na preubytovanie.");
            return;
        } catch (SQLException e) {
            if (e.getSQLState().equals("40001")) { //Isolation exception.
                System.out.println("Pocas preubytuvavania sa manipulovalo s datami. Zopakovat operaciu? a/n");
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                if (br.readLine().equals("a")) { //executes operation again
                    reaccommodate(accId, roomId);
                }
            } else {
                e.printStackTrace();
            }

            return;
        } catch (NotEnoughCreditException e) {
            System.out.println("Student nema dostatok kreditu na preubytovanie, kedze izba, do ktorej sa stahuje ma vyssi mesacny poplatok.");
            return;
        } catch (RoomNotFoundException e) {
            System.out.println("Izba s takouto ID neexistuje.");
            return;
        }
        System.out.println("Preubytovanie sa podarilo. " + (result.intValue() > 0 ? " Nova izba bola drahsia ako predchadzajuca, preto sa " +
                "stiahla z uctu studenta suma: " + result.toString() : "Nova izba mala rovnaku cenu alebo bola lacnejsia, na ucet sa pridalo " +
                result.toString()));
    }
}
