import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuarantineSystem {
    public static class DashBoard {
    	HashMap<String, Person> People;
        List<Integer> patientNums;
        List<Integer> infectNums;
        List<Double> infectAvgNums;
        List<Integer> vacNums;
        List<Integer> vacInfectNums;

        public DashBoard(HashMap<String, Person> p_People) {
            this.People = p_People;
            this.patientNums = new ArrayList<>(8);
            this.infectNums = new ArrayList<>(8);
            this.infectAvgNums = new ArrayList<>(8);
            this.vacNums = new ArrayList<>(8);
            this.vacInfectNums = new ArrayList<>(8);
        }

        public void runDashBoard() {
            for (int i = 0; i < 8; i++) {
                this.patientNums.add(0);
                this.infectNums.add(0);
                this.vacNums.add(0);
                this.vacInfectNums.add(0);
                this.infectAvgNums.add(0.00);
            }

            /*
             * TODO: Collect the statistics based on People
             *  Add the data in the lists, such as patientNums, infectNums, etc.
             */
            for (Map.Entry<String, Person> temp : People.entrySet()) {
            	int i = ((temp.getValue().getAge() / 10 > 7) ? 7 : temp.getValue().getAge() / 10);
            	if (temp.getValue().getIsVac()) {
            		vacNums.set(i, vacNums.get(i) + 1);}
            	if (temp.getValue().getInfectCnt()>0) {
            		patientNums.set(i, patientNums.get(i) + 1);
            		infectNums.set(i, patientNums.get(i) + temp.getValue().getInfectCnt());
            		if (temp.getValue().getIsVac()) {
            			vacInfectNums.set(i, vacInfectNums.get(i) + 1);}
            	}	
            }
            for (int i = 0; i < 8; i++) 
            	if (patientNums.get(i)!=0)
            		infectAvgNums.set(i, infectAvgNums.get(i) + (Double.valueOf(infectNums.get(i))/Double.valueOf(patientNums.get(i))) );
        }
    }


    private HashMap<String, Person> People;
    private HashMap<String, Patient> Patients;

    private List<Record> Records;
    private HashMap<String, Hospital> Hospitals;
    private int newHospitalNum;

    private DashBoard dashBoard;

    public QuarantineSystem() throws IOException {
        importPeople();
        importHospital();
        importRecords();
        dashBoard = null;
        Patients = new HashMap<>();;
    }

    public void startQuarantine() throws IOException {
        /*
         * Task 1: Saving Patients
         */
        System.out.println("Task 1: Saving Patients");
        /*
         * TODO: Process each record
         */
        String res;
        int dis;
        int mindis;
        boolean full;
        String newID;
        int IDcounter = 1;
        for (Record rec : Records) {
        	res = null;
        	dis = 0;
        	mindis = 2147483647;
        	if (rec.getStatus()== Status.Confirmed) {
	        	for (Map.Entry<String, Hospital> hos : Hospitals.entrySet()) {
	        		full = false;
	        		dis = hos.getValue().getLoc().getDisSquare((People.get(rec.getIDCardNo()).getLoc()));
	        		switch(rec.getSymptomLevel()) {
	    				case Critical:
	    					full = ((hos.getValue().getCapacity().CriticalCapacity==0) ? true : false);
	    					break;
	    				case Moderate:
	    					full = ((hos.getValue().getCapacity().ModerateCapacity==0) ? true : false);
	    					break;
	    				case Mild:
	    					full = ((hos.getValue().getCapacity().MildCapacity==0) ? true : false);
	    					break;
	    				default:
	    					break;
	            	}
	        		if ((!full) && (dis<mindis)) {
	        			mindis = dis;
	        			res = hos.getValue().HospitalID;
	        		}
	            }
	        	if (res == null) {
	        		newID = "H-New-" + String.valueOf(IDcounter);
	        		Capacity cap = new Capacity(5,10,20);
	        		Hospital temp = new Hospital(newID,People.get(rec.getIDCardNo()).getLoc(),cap);
	        		Hospitals.put(newID, temp);
	        		rec.setHospitalID(newID);
	        		IDcounter++;
	        	} else {
	        		rec.setHospitalID(res);
	        	}
	        	saveSinglePatient(rec);
        	} else {
        		rec.setHospitalID(Patients.get(rec.getIDCardNo()).getHospitalID());
        		releaseSinglePatient(rec);
        	}
        }
        
        exportRecordTreatment();

        /*
         * Task 2: Displaying Statistics
         */
        System.out.println("Task 2: Displaying Statistics");
        dashBoard = new DashBoard(this.People);
        dashBoard.runDashBoard();
        exportDashBoard();
    }

    /*
     * Save a single patient when the status of the record is Confirmed
     */
    public void saveSinglePatient(Record record) {
        //TODO
    	People.get(record.getIDCardNo()).setInfectCnt(People.get(record.getIDCardNo()).getInfectCnt()+1);
    	Patient p = new Patient(People.get(record.getIDCardNo()),record.getSymptomLevel());
    	p.setHospitalID(record.getHospitalID());
    	Patients.put(record.getIDCardNo(), p);
    	Hospitals.get(record.getHospitalID()).addPatient(p);
    }

    /*
     * Release a single patient when the status of the record is Recovered
     */
    public void releaseSinglePatient(Record record) {
        //TODO
    	Hospitals.get(record.getHospitalID()).releasePatient(Patients.get(record.getIDCardNo()));
    	Patients.remove(record.getIDCardNo(), Patients.get(record.getIDCardNo()));
    }

    /*
     * Import the information of the people in the area from Person.txt
     * The data is finally stored in the attribute People
     * You do not need to change the method.
     */
    public void importPeople() throws IOException {
        this.People = new HashMap<String, Person>();
        File filename = new File("data/Person.txt");
        InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);
        String line = br.readLine();
        int lineNum = 0;

        while (line != null) {
            lineNum++;
            if (lineNum > 1) {
                String[] records = line.split("        ");
                assert (records.length == 6);
                String pIDCardNo = records[0];
                System.out.println(pIDCardNo);
                int XLoc = Integer.parseInt(records[1]);
                int YLoc = Integer.parseInt(records[2]);
                Location pLoc = new Location(XLoc, YLoc);
                assert (records[3].equals("Male") || records[3].equals("Female"));
                String pGender = records[3];
                int pAge = Integer.parseInt(records[4]);
                assert (records[5].equals("Yes") || records[5].equals("No"));
                boolean pIsVac = (records[5].equals("Yes"));
                Person p = new Person(pIDCardNo, pLoc, pGender, pAge, pIsVac);
                this.People.put(pIDCardNo, p);
            }
            line = br.readLine();
        }
    }

    /*
     * Import the information of the records
     * The data is finally stored in the attribute Records
     * You do not need to change the method.
     */
    public void importRecords() throws IOException {
        this.Records = new ArrayList<>();

        File filename = new File("data/Record.txt");
        InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);
        String line = br.readLine();
        int lineNum = 0;

        while (line != null) {
            lineNum++;
            if (lineNum > 1) {
                String[] records = line.split("        ");
                assert(records.length == 3);
                String pIDCardNo = records[0];
                System.out.println(pIDCardNo);
                assert(records[1].equals("Critical") || records[1].equals("Moderate") || records[1].equals("Mild"));
                assert(records[2].equals("Confirmed") || records[2].equals("Recovered"));
                Record r = new Record(pIDCardNo, records[1], records[2]);
                Records.add(r);
            }
            line = br.readLine();
        }
    }

    /*
     * Import the information of the hospitals
     * The data is finally stored in the attribute Hospitals
     * You do not need to change the method.
     */
    public void importHospital() throws IOException {
        this.Hospitals = new HashMap<>();
        this.newHospitalNum = 0;

        File filename = new File("data/Hospital.txt");
        InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);
        String line = br.readLine();
        int lineNum = 0;

        while (line != null) {
            lineNum++;
            if (lineNum > 1) {
                String[] records = line.split("        ");
                assert(records.length == 6);
                String pHospitalID = records[0];
                System.out.println(pHospitalID);
                int XLoc = Integer.parseInt(records[1]);
                int YLoc = Integer.parseInt(records[2]);
                Location pLoc = new Location(XLoc, YLoc);
                int pCritialCapacity = Integer.parseInt(records[3]);
                int pModerateCapacity = Integer.parseInt(records[4]);
                int pMildCapacity = Integer.parseInt(records[5]);
                Capacity cap = new Capacity(pCritialCapacity, pModerateCapacity, pMildCapacity);
                Hospital hospital = new Hospital(pHospitalID, pLoc, cap);
                this.Hospitals.put(pHospitalID, hospital);
            }
            line = br.readLine();
        }
    }

    /*
     * Export the information of the records
     * The data is finally dumped into RecordTreatment.txt
     * DO NOT change the functionality of the method
     * Otherwise, you may generate wrong results in Task 1
     */
    public void exportRecordTreatment() throws IOException {
        File filename = new File("output/RecordTreatment.txt");
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filename));
        BufferedWriter bw = new BufferedWriter(writer);
        bw.write("IDCardNo        SymptomLevel        Status        HospitalID\n");
        for (Record record : Records) {
            //Invoke the toString method of Record.
            bw.write(record.toString() + "\n");
        }
        bw.close();
    }

    /*
     * Export the information of the dashboard
     * The data is finally dumped into Statistics.txt
     * DO NOT change the functionality of the method
     * Otherwise, you may generate wrong results in Task 2
     */
    public void exportDashBoard() throws IOException {
        File filename = new File("output/Statistics.txt");
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filename));
        BufferedWriter bw = new BufferedWriter(writer);

        bw.write("AgeRange        patientNums        infectAvgNums        vacNums        vacInfectNums\n");

        for (int i = 0; i < 8; i++) {
            String ageRageStr = "";
            switch (i) {
                case 0:
                    ageRageStr = "(0, 10)";
                    break;
                case 7:
                    ageRageStr = "[70, infinite)";
                    break;
                default:
                    ageRageStr = "[" + String.valueOf(i) + "0, " + String.valueOf(i + 1) + "0)";
                    break;
            }
            String patientNumStr = String.valueOf(dashBoard.patientNums.get(i));
            String infectAvgNumsStr = String.valueOf(dashBoard.infectAvgNums.get(i));
            String vacNumsStr = String.valueOf(dashBoard.vacNums.get(i));
            String vacInfectNumsStr = String.valueOf(dashBoard.vacInfectNums.get(i));

            bw.write(ageRageStr + "        " + patientNumStr + "        " + infectAvgNumsStr + "        " + vacNumsStr + "        " + vacInfectNumsStr + "\n");
        }

        bw.close();
    }

    /* The entry of the project */
    public static void main(String[] args) throws IOException {
        QuarantineSystem system = new QuarantineSystem();
        long startTime = System.nanoTime();
        System.out.println("Start Quarantine System");
        system.startQuarantine();
        System.out.println("Quarantine Finished");
        long endTime = System.nanoTime();
        double duration = Double.valueOf(endTime - startTime)/1000000/1000;
        System.out.println("Execution Time: "+duration+" seconds");
    }
}
