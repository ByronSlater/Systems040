import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Test extends JFrame implements ActionListener{
  private static final long serialVersionUID = 1L;
  public Container contentPane = getContentPane();
  public JPanel AdminPanel = new JPanel(new FlowLayout());
  public JPanel StudentInfoPanel = new JPanel(new FlowLayout());
  public JPanel RegistrarPanel = new JPanel(new FlowLayout());
  public JPanel TeacherPanel = new JPanel(new FlowLayout());
  public JPanel StudentPanel = new JPanel(new FlowLayout());
  public JPanel DegreePanel = new JPanel(new FlowLayout());
  public JPanel DepartmentPanel = new JPanel(new FlowLayout());
  public JPanel LoginPanel = new JPanel(new FlowLayout());
  public JPanel ModulePanel = new JPanel(new FlowLayout());
  public JPanel AddModulePanel = new JPanel();
  public JPanel LinkModulePanel = new JPanel();
  public JPanel AddDepartmentPanel = new JPanel();
  public JPanel AddDegreePanel = new JPanel();
  public JPanel AddStudentPanel = new JPanel();
  public JPanel AddUserPanel = new JPanel();
  public JPanel UsersPanel = new JPanel(new FlowLayout());
  public JPanel RegStudentsPanel = new JPanel(new FlowLayout());
  public JPanel IndividualStudentPanel = new JPanel();
  public JPanel TeacherStudentsPanel = new JPanel(new FlowLayout());
  public JPanel StudentGradesPanel = new JPanel();
  public JPanel AdminStudentPanel = new JPanel();
  public JTextField username = new JTextField("Username");
  public JPasswordField password = new JPasswordField("Password");
  public JTextField departmentFullname = new JTextField("");
  public JTextField departmentCode = new JTextField("");
  public JTextField moduleFullname = new JTextField("");
  public JTextField moduleCode = new JTextField("");
  public JTextField moduleCredits = new JTextField("");
  public JTextField moduleTimePeriod = new JTextField("");
  public JCheckBox moduleCore = new JCheckBox("");
  public JTextField moduleDepartment = new JTextField("");
  public JComboBox timeList = new JComboBox();
  public JTextField degreeFullname = new JTextField("");
  public JTextField degreeCode = new JTextField("");
  public JCheckBox degreeYearIndustry = new JCheckBox();
  public JTextField studentTitle = new JTextField("");
  public JTextField studentFirstname =  new JTextField("");
  public JTextField studentLastname = new JTextField("");
  public JTextField studentRegnum = new JTextField("");
  public JTextField studentEmail = new JTextField("");
  public JTextField studentTutor = new JTextField("");
  public JComboBox optionalModules = new JComboBox();
  public JTextField userUsername = new JTextField("");
  public JTextField userPassword = new JTextField("");
  public JComboBox userType = new JComboBox();
  public JTextField adminTitle = new JTextField("");
  public JTextField adminFirstname =  new JTextField("");
  public JTextField adminLastname = new JTextField("");
  public JTextField adminRegnum = new JTextField("");
  public JTextField adminEmail = new JTextField("");
  public JTextField adminTutor = new JTextField("");
  public JComboBox exam = new JComboBox();
  public JTextField grade = new JTextField("");
  public JTextField resit = new JTextField("");

  public Test() throws HeadlessException {

    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension screenSize = toolkit.getScreenSize();

    contentPane.setLayout(new FlowLayout());

    setSize(screenSize.width/2, screenSize.height/2);
    setLocation(screenSize.width/4, screenSize.height/4);

    setDefaultCloseOperation(EXIT_ON_CLOSE);

    Dimension input = new Dimension(100, 24);

    LoginPanel.add(username);
    LoginPanel.add(password);
    JButton login = new JButton ("Login");
    login.addActionListener(this);
    LoginPanel.add(login);

    contentPane.add(LoginPanel);

    JButton userlist = new JButton("Users");
    userlist.addActionListener(this);
    AdminPanel.add(userlist);
    JButton degreelist = new JButton("Degrees");
    degreelist.addActionListener(this);
    AdminPanel.add(degreelist);
    JButton deplist = new JButton("Departments");
    deplist.addActionListener(this);
    AdminPanel.add(deplist);
    JButton modulelist = new JButton("Modules");
    modulelist.addActionListener(this);
    AdminPanel.add(modulelist);
    JButton linkModule = new JButton("Link Module");
    linkModule.addActionListener(this);
    AdminPanel.add(linkModule);
    JButton adminLogout = new JButton("Logout");
    adminLogout.addActionListener(this);
    AdminPanel.add(adminLogout);

    JButton registrarLogout = new JButton("Logout");
    registrarLogout.addActionListener(this);
    RegistrarPanel.add(registrarLogout);

    //TeacherPanel.add(studentlist);
    JButton allStudents = new JButton("All Students");
    allStudents.addActionListener(this);
    TeacherPanel.add(allStudents);
    JButton teacherLogout = new JButton("Logout");
    teacherLogout.addActionListener(this);
    TeacherPanel.add(teacherLogout);

    JButton studentLogout = new JButton("Logout");
    studentLogout.addActionListener(this);
    StudentPanel.add(studentLogout);

    String[] departmentColumns = {"Full Name",
                        "Code"};
    Object[][] departmentData = { {"Computer Science", "COM"},
        {"Business School", "BUS"}, {"Psychology", "PSY"}, {"Modern Languages", "LAN"}
    };
    JTable department = new JTable(departmentData, departmentColumns);
    DepartmentPanel.add(new JScrollPane(department));
    JButton addDepartment = new JButton("Add Department");
    addDepartment.addActionListener(this);
    DepartmentPanel.add(addDepartment);
    JButton dBack = new JButton("Back");
    dBack.addActionListener(this);
    DepartmentPanel.add(dBack);

    String[] degreeColumns = {"Full Name", "Code", "Lead Department", "Other Departments", "Year in Industry"};
    Object[][] degreeData = {{"MSc in Business Administation", "BUSU01", "BUS", "", "No"},
      {"MEng Software Engineering", "COMU01", "COM","", "Yes"},
      {"BSc Information Systems", "COMU02", "COM", "BUS, LAN", "No"},
      {"MPsy Cognitive Science", "PSYU01", "PSY", "COM", "No"}};
    JTable degree = new JTable(degreeData, degreeColumns);
    DegreePanel.add(new JScrollPane(degree));
    JButton addDegree = new JButton("Add Degree");
    addDegree.addActionListener(this);
    DegreePanel.add(addDegree);
    JButton deBack = new JButton("Back");
    deBack.addActionListener(this);
    DegreePanel.add(deBack);

    AddDegreePanel.setLayout(new BoxLayout(AddDegreePanel, BoxLayout.Y_AXIS));
    JTextField deName = new JTextField("Name: ");
    deName.setEditable(false);
    AddDegreePanel.add(deName);
    degreeFullname.setPreferredSize(input);
    AddDegreePanel.add(degreeFullname);
    JTextField deCode = new JTextField("Code: ");
    deCode.setEditable(false);
    AddDegreePanel.add(deCode);
    degreeCode.setPreferredSize(input);
    AddDegreePanel.add(degreeCode);
    JTextField yearIndustry = new JTextField("Year in Industry: ");
    yearIndustry.setEditable(false);
    AddDegreePanel.add(yearIndustry);
    degreeYearIndustry.setPreferredSize(input);
    AddDegreePanel.add(degreeYearIndustry);
    JButton deAdd = new JButton("Add New Degree");
    deAdd.addActionListener(this);
    AddDegreePanel.add(deAdd);

    AddDepartmentPanel.setLayout(new BoxLayout(AddDepartmentPanel, BoxLayout.Y_AXIS));
    JTextField dName = new JTextField("Name: ");
    dName.setEditable(false);
    AddDepartmentPanel.add(dName);
    departmentFullname.setPreferredSize(input);
    AddDepartmentPanel.add(departmentFullname);
    JTextField dCode = new JTextField("Code: ");
    dCode.setEditable(false);
    AddDepartmentPanel.add(dCode);
    departmentCode.setPreferredSize(input);
    AddDepartmentPanel.add(departmentCode);
    JButton dAdd = new JButton("Add New Department");
    dAdd.addActionListener(this);
    AddDepartmentPanel.add(dAdd);

    String[] moduleColumns = {"Full Name", "Code", "Credits", "Core"};
    Object[][] moduleData = {{"Java Programming", "COM1003", "20", "Yes"},
    {"Machines and Intelligence", "COM1005", "20", "Yes"}};
    JTable module = new JTable(moduleData, moduleColumns);
    ModulePanel.add(new JScrollPane(module));
    JButton mBack = new JButton("Back");
    mBack.addActionListener(this);
    ModulePanel.add(mBack);
    JButton addModule = new JButton("Add Module");
    addModule.addActionListener(this);
    ModulePanel.add(addModule);

    AddModulePanel.setLayout(new BoxLayout(AddModulePanel, BoxLayout.Y_AXIS));
    JTextField mName = new JTextField("Name: ");
    mName.setEditable(false);
    AddModulePanel.add(mName);
    moduleFullname.setPreferredSize(input);
    AddModulePanel.add(moduleFullname);
    JTextField mCode = new JTextField("Code: ");
    mCode.setEditable(false);
    AddModulePanel.add(mCode);
    moduleCode.setPreferredSize(input);
    AddModulePanel.add(moduleCode);
    JTextField mCredits = new JTextField("Credits: ");
    mCredits.setEditable(false);
    AddModulePanel.add(mCredits);
    moduleCredits.setPreferredSize(input);
    AddModulePanel.add(moduleCredits);
    JTextField mTimePeriod = new JTextField("Time Period: ");
    mTimePeriod.setEditable(false);
    AddModulePanel.add(mTimePeriod);
    moduleTimePeriod.setPreferredSize(input);
    timeList.addItem("1");
    timeList.addItem("2");
    AddModulePanel.add(timeList);
    JButton mAdd = new JButton ("Add New Module");
    AddModulePanel.add(mAdd);
    mAdd.addActionListener(this);

    LinkModulePanel.setLayout(new BoxLayout(LinkModulePanel, BoxLayout.Y_AXIS));
    JTextField mCore = new JTextField("Core: ");
    mCore.setEditable(false);
    LinkModulePanel.add(mCore);
    LinkModulePanel.add(moduleCore);
    JTextField mDepartment = new JTextField("Department: ");
    mDepartment.setEditable(false);
    LinkModulePanel.add(mDepartment);
    moduleDepartment.setPreferredSize(input);
    LinkModulePanel.add(moduleDepartment);
    JButton linkModuleAdd = new JButton ("Add Module Link");
    linkModuleAdd.addActionListener(this);
    LinkModulePanel.add(linkModuleAdd);

    String[] studentColumns = {"Title", "First Name", "Surname", "Reg Number",
    "Email Address", "Personal Tutor"};
    Object[][] studentData = {{"Miss", "Madeleine", "Austen", "123456789",
    "MAusten1@sheffield.ac.uk", "Steve Maddock"}};
    JTable user = new JTable(studentData, studentColumns);
    StudentInfoPanel.add(new JScrollPane(user));
    JButton sBack = new JButton("Back");
    sBack.addActionListener(this);
    StudentInfoPanel.add(sBack);
    JButton addUser = new JButton("Add User");
    addUser.addActionListener(this);
    StudentInfoPanel.add(addUser);
    JButton adminStudent = new JButton("Admin: Add Student");
    adminStudent.addActionListener(this);
    StudentInfoPanel.add(adminStudent);

    AddUserPanel.setLayout(new BoxLayout(AddUserPanel, BoxLayout.Y_AXIS));
    JTextField uUsername = new JTextField("Username: ");
    uUsername.setEditable(false);
    AddUserPanel.add(uUsername);
    userUsername.setPreferredSize(input);
    AddUserPanel.add(userUsername);
    JTextField uPassword = new JTextField("Password: ");
    uPassword.setEditable(false);
    AddUserPanel.add(uPassword);
    userPassword.setPreferredSize(input);
    AddUserPanel.add(userPassword);
    JTextField uType = new JTextField("User Type: ");
    uType.setEditable(false);
    AddUserPanel.add(uType);
    userType.addItem("Admin");
    userType.addItem("Registrar");
    userType.addItem("Teacher");
    AddUserPanel.add(userType);
    JButton uAdd = new JButton("Add New User");
    uAdd.addActionListener(this);
    AddUserPanel.add(uAdd);

    AdminStudentPanel.setLayout(new BoxLayout(AdminStudentPanel, BoxLayout.Y_AXIS));
    JTextField aTitle = new JTextField("Title: ");
    aTitle.setEditable(false);
    AdminStudentPanel.add(aTitle);
    adminTitle.setPreferredSize(input);
    AdminStudentPanel.add(adminTitle);
    JTextField aFirstname = new JTextField("First Name: ");
    aFirstname.setEditable(false);
    AdminStudentPanel.add(aFirstname);
    adminFirstname.setPreferredSize(input);
    AdminStudentPanel.add(adminFirstname);
    JTextField aLastname = new JTextField("Last Name: ");
    aLastname.setEditable(false);
    AdminStudentPanel.add(aLastname);
    adminLastname.setPreferredSize(input);
    AdminStudentPanel.add(adminLastname);
    JTextField aRegnum = new JTextField("Registration Number: ");
    aRegnum.setEditable(false);
    AdminStudentPanel.add(aRegnum);
    adminRegnum.setPreferredSize(input);
    AdminStudentPanel.add(adminRegnum);
    JTextField aEmail = new JTextField("Email Address: ");
    aEmail.setEditable(false);
    AdminStudentPanel.add(aEmail);
    adminEmail.setPreferredSize(input);
    AdminStudentPanel.add(adminEmail);
    JTextField aTutor = new JTextField("Tutor: ");
    aTutor.setEditable(false);
    AdminStudentPanel.add(aTutor);
    adminTutor.setPreferredSize(input);
    AdminStudentPanel.add(adminTutor);
    JButton aAdd = new JButton("Admin: Add New Student");
    aAdd.addActionListener(this);
    AdminStudentPanel.add(aAdd);

    AddStudentPanel.setLayout(new BoxLayout(AddStudentPanel, BoxLayout.Y_AXIS));
    JTextField sTitle = new JTextField("Title: ");
    sTitle.setEditable(false);
    AddStudentPanel.add(sTitle);
    studentTitle.setPreferredSize(input);
    AddStudentPanel.add(studentTitle);
    JTextField sFirstname = new JTextField("First Name: ");
    sFirstname.setEditable(false);
    AddStudentPanel.add(sFirstname);
    studentFirstname.setPreferredSize(input);
    AddStudentPanel.add(studentFirstname);
    JTextField sLastname = new JTextField("Last Name: ");
    sLastname.setEditable(false);
    AddStudentPanel.add(sLastname);
    studentLastname.setPreferredSize(input);
    AddStudentPanel.add(studentLastname);
    JTextField sRegnum = new JTextField("Registration Number: ");
    sRegnum.setEditable(false);
    AddStudentPanel.add(sRegnum);
    studentRegnum.setPreferredSize(input);
    AddStudentPanel.add(studentRegnum);
    JTextField sEmail = new JTextField("Email Address: ");
    sEmail.setEditable(false);
    AddStudentPanel.add(sEmail);
    studentEmail.setPreferredSize(input);
    AddStudentPanel.add(studentEmail);
    JTextField sTutor = new JTextField("Tutor: ");
    sTutor.setEditable(false);
    AddStudentPanel.add(sTutor);
    studentTutor.setPreferredSize(input);
    AddStudentPanel.add(studentTutor);
    JButton sAdd = new JButton("Add New Student");
    sAdd.addActionListener(this);
    AddStudentPanel.add(sAdd);

    JButton regStudents = new JButton("View Students");
    regStudents.addActionListener(this);
    RegistrarPanel.add(regStudents);

    JTable studentReg = new JTable(studentData, studentColumns);
    RegStudentsPanel.add(new JScrollPane(studentReg));
    JButton addStudent = new JButton("Add Student");
    addStudent.addActionListener(this);
    RegStudentsPanel.add(addStudent);
    JButton studentModule = new JButton("View Individual Student");
    studentModule.addActionListener(this);
    RegStudentsPanel.add(studentModule);
    JButton regBack = new JButton("Registrar Back");
    regBack.addActionListener(this);
    RegStudentsPanel.add(regBack);

    IndividualStudentPanel.setLayout(new BoxLayout(IndividualStudentPanel, BoxLayout.Y_AXIS));
    JTextField oModules = new JTextField("Optional Modules: ");
    oModules.setEditable(false);
    IndividualStudentPanel.add(oModules);
    optionalModules.addItem("Java Programming");
    optionalModules.addItem("Functional Programming");
    IndividualStudentPanel.add(optionalModules);
    JButton regEdits = new JButton("Make Changes");
    regEdits.addActionListener(this);
    IndividualStudentPanel.add(regEdits);

    JTable student = new JTable(studentData, studentColumns);
    TeacherStudentsPanel.add(new JScrollPane(student));
    JButton teacherStudent = new JButton("Edit Student Grades");
    teacherStudent.addActionListener(this);
    TeacherStudentsPanel.add(teacherStudent);

    StudentGradesPanel.setLayout(new BoxLayout(StudentGradesPanel, BoxLayout.Y_AXIS));
    JTextField sExam = new JTextField("Exam: ");
    sExam.setEditable(false);
    StudentGradesPanel.add(sExam);
    exam.addItem("COM1005 exam 1");
    exam.addItem("BUS1002 exam 1");
    StudentGradesPanel.add(exam);
    JTextField sGrade = new JTextField("Initial Grade: ");
    sGrade.setEditable(false);
    StudentGradesPanel.add(sGrade);
    grade.setPreferredSize(input);
    StudentGradesPanel.add(grade);
    JTextField sResit = new JTextField("Resit Grade: ");
    sResit.setEditable(false);
    StudentGradesPanel.add(sResit);
    resit.setPreferredSize(input);
    StudentGradesPanel.add(resit);
    JButton updateGrades = new JButton("Update Grades");
    updateGrades.addActionListener(this);
    StudentGradesPanel.add(updateGrades);

    setVisible(true);
  }

  public void changePanel(JPanel p) {
    contentPane.removeAll();
    contentPane.add(p);
    revalidate();
    repaint();
  }

  public void actionPerformed(ActionEvent e) {
    String str = e.getActionCommand();
    if(str.equals("Login")) {
      if(username.getText().equals("Admin")) {
        changePanel(AdminPanel);
      }
      else if(username.getText().equals("Registrar")){
        changePanel(RegistrarPanel);
      }
      else if(username.getText().equals("Teacher")){
        changePanel(TeacherPanel);
      }
      else if(username.getText().equals("Student")){
        changePanel(StudentPanel);
      }
    }
    if(str.equals("Users")) {
      changePanel(StudentInfoPanel);
    }
    if(str.equals("Degrees")) {
      changePanel(DegreePanel);
    }
    if(str.equals("Departments")){
      changePanel(DepartmentPanel);
    }
    if(str.equals("Modules")){
      changePanel(ModulePanel);
    }
    if(str.equals("Students")){
      changePanel(StudentInfoPanel);
    }
    if(str.equals("Back")){
      changePanel(AdminPanel);
    }
    if(str.equals("Add Module")){
      changePanel(AddModulePanel);
    }
    if(str.equals("Add New Module")){
      changePanel(ModulePanel);
    }
    if(str.equals("Link Module")){
      changePanel(LinkModulePanel);
    }
    if(str.equals("Add Module Link")){
      changePanel(ModulePanel);
    }
    if(str.equals("Add Department")){
      changePanel(AddDepartmentPanel);
    }
    if(str.equals("Add New Department")){
      changePanel(DepartmentPanel);
    }
    if(str.equals("Add Degree")){
      changePanel(AddDegreePanel);
    }
    if(str.equals("Add New Degree")){
      changePanel(DegreePanel);
    }
    if(str.equals("Add Student")){
      changePanel(AddStudentPanel);
    }
    if(str.equals("Add New Student")){
      changePanel(RegStudentsPanel);
    }
    if(str.equals("Add User")){
      changePanel(AddUserPanel);
    }
    if(str.equals("Add New User")){
      changePanel(StudentInfoPanel);
    }
    if(str.equals("Admin: Add Student")){
      changePanel(AdminStudentPanel);
    }
    if(str.equals("Admin: Add New Student")){
      changePanel(StudentInfoPanel);
    }
    if(str.equals("View Students")){
      changePanel(RegStudentsPanel);
    }
    if(str.equals("Registrar Back")){
      changePanel(RegistrarPanel);
    }
    if(str.equals("View Individual Student")){
      changePanel(IndividualStudentPanel);
    }
    if(str.equals("Make Changes")){
      changePanel(RegStudentsPanel);
    }
    if(str.equals("All Students")){
      changePanel(TeacherStudentsPanel);
    }
    if(str.equals("Edit Student Grades")){
      changePanel(StudentGradesPanel);
    }
    if(str.equals("Update Grades")){
      changePanel(TeacherStudentsPanel);
    }
    if(str.equals("Logout")){
      changePanel(LoginPanel);
    }
  }

  public static void main(String[] args) {
    new Test();
  }
}
