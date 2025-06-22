package com.mycompany.javaproject;
import com.mycompany.javaproject.BordersPanel;
import com.mycompany.javaproject.ImpactDataManager;
import com.mycompany.javaproject.HumanitarianPanel;
import com.mycompany.javaproject.HealthcarePanel;
import com.mycompany.javaproject.OverviewPanel;
import com.mycompany.javaproject.EducationPanel;
import com.mycompany.javaproject.AnalysisPanel;
import javax.swing.*;
import java.awt.*;


public class JavaProject extends JFrame {

    private ImpactDataManager dataManager; // Your data management class

    // Panels for each tab
    private OverviewPanel overviewPanel;
    private HumanitarianPanel humanitarianPanel;
    private HealthcarePanel healthcarePanel;
    private EducationPanel educationPanel;
    private BordersPanel bordersPanel;
    private AnalysisPanel analysisPanel;

    public JavaProject() {
        this.dataManager = new ImpactDataManager(); // Initialize your data manager
        // TODO: Populate dataManager with some initial/test data if needed for development
        // Example: dataManager.addRegionDataEntry(new RegionData("05/2024", "Gaza"));


        setTitle("Palestine War Impact Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         
        setSize(1000, 700); // Initial size, can be adjusted
        setLocationRelativeTo(null); // Center the window

        initComponents();

        // Consider setting a more formal Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error setting Look and Feel: " + e.getMessage());
        }
    }

    private void initComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Initialize panels (passing dataManager to them)
        overviewPanel = new OverviewPanel(dataManager);
       humanitarianPanel = new HumanitarianPanel(dataManager);
        healthcarePanel = new HealthcarePanel(dataManager);
        educationPanel = new EducationPanel(dataManager);
       bordersPanel = new BordersPanel(dataManager);
        analysisPanel = new AnalysisPanel(dataManager);

        // Add tabs
        tabbedPane.addTab("Overview & Regions", UIManager.getIcon("Tree.homeIcon"), overviewPanel, "Manage regional data and siege status");
        tabbedPane.addTab("Humanitarian Stats", UIManager.getIcon("Tree.leafIcon"), humanitarianPanel, "Manage Martyrs, Wounded, and Prisoners");
        tabbedPane.addTab("Healthcare Impact", UIManager.getIcon("OptionPane.informationIcon"), healthcarePanel, "Manage healthcare infrastructure and patient data"); // Example icon
        tabbedPane.addTab("Educational Impact", UIManager.getIcon("FileView.hardDriveIcon"), educationPanel, "Manage educational infrastructure and student data"); // Example icon
        tabbedPane.addTab("Border Status", UIManager.getIcon("FileView.computerIcon"), bordersPanel, "View and manage border crossing statuses"); // Example icon
        tabbedPane.addTab("Analysis & Reports", UIManager.getIcon("FileChooser.detailsViewIcon"), analysisPanel, "View analytical reports"); // Example icon

        // Example: Setting a more formal font for tabs (optional)
        Font tabFont = new Font("Segoe UI", Font.PLAIN, 14);
        tabbedPane.setFont(tabFont);

        add(tabbedPane, BorderLayout.CENTER);

        // Optional: Add a Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        // Add more menus (e.g., Help) if needed
        setJMenuBar(menuBar);
    }

   // In JavaProject.java, at the beginning of the main method
// In JavaProject.java
public static void main(String[] args) {
    // --- Splash Screen Logic ---
    JWindow splashWindow = new JWindow(); // أنشئ النافذة مرة واحدة
    JLabel splashLabel; // عرّف splashLabel هنا ليكون متاحًا في كلا حالتي if/else

    java.net.URL imageUrl = JavaProject.class.getResource("/logoo.png"); // حاول تحميل الصورة

    if (imageUrl != null) {
        System.out.println("Splash image found at: " + imageUrl); // DEBUG: للتأكد من مسار الصورة
        ImageIcon splashIcon = new ImageIcon(imageUrl);
        splashLabel = new JLabel(splashIcon); // استخدم الصورة إذا وُجدت
        // يمكنك هنا ضبط حجم splashLabel ليتناسب مع الصورة إذا أردت
        // splashLabel.setPreferredSize(new Dimension(splashIcon.getIconWidth(), splashIcon.getIconHeight()));
    } else {
        System.err.println("Error: Splash image 'logo.png' not found in resources! Displaying text instead.");
        // إذا لم يتم العثور على الصورة، استخدم نصًا بديلاً
        splashLabel = new JLabel("Palestine War Impact Tracker", SwingConstants.CENTER);
        splashLabel.setFont(new Font("Serif", Font.BOLD, 28));
        splashLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        splashLabel.setOpaque(true);
        splashLabel.setBackground(Color.WHITE);
    }

    
    
    // الآن، قم بتهيئة وإظهار splashWindow بالمحتوى المناسب (صورة أو نص)
    // تحديد حجم شاشة البداية وموقعها
    // إذا كنتِ تستخدمين صورة، قد ترغبين في جعل الحجم يعتمد على حجم الصورة
    int splashWidth = 1080; // يمكنك تعديل هذا
    int splashHeight =733; // يمكنك تعديل هذا
    // إذا كان splashLabel يحتوي على صورة، يمكنك استخدام حجمها:
    // if (imageUrl != null) {
    //     splashWidth = splashLabel.getPreferredSize().width > 0 ? splashLabel.getPreferredSize().width : 450;
    //     splashHeight = splashLabel.getPreferredSize().height > 0 ? splashLabel.getPreferredSize().height : 250;
    // }

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    splashWindow.setBounds(
            (screenSize.width - splashWidth) / 2,
            (screenSize.height - splashHeight) / 2,
            splashWidth, splashHeight
    );

    splashWindow.getContentPane().add(splashLabel); // أضف JLabel (سواء كان صورة أو نصًا)
    splashWindow.setVisible(true);

    // إيقاف مؤقت لبضع ثوانٍ
    try {
        Thread.sleep(3000); // 3 ثوانٍ
    } catch (InterruptedException e) {
        // إذا حدث خطأ أثناء Thread.sleep (نادر جدًا هنا)
        e.printStackTrace(); 
    }

    // إخفاء شاشة البداية وتحرير مواردها
    splashWindow.setVisible(false);
    splashWindow.dispose();
    // --- End Splash Screen Logic ---

    // الكود الحالي لتشغيل الواجهة الرئيسية
    SwingUtilities.invokeLater(() -> {
        JavaProject app = new JavaProject();
        app.setVisible(true);
    });
}
}