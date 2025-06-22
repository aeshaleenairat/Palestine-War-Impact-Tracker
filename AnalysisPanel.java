
package com.mycompany.javaproject; // Or your actual package

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
// JFreeChart imports
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.CategoryPlot; // For customizing plot
import org.jfree.chart.renderer.category.BarRenderer; // For customizing bars

public class AnalysisPanel extends JPanel {
    private ImpactDataManager dataManager;
    private JTextArea analysisResultsArea;
    private JButton mostAffectedButton, mwPStatsButton, sortedPrisonersButton, allBorderStatusButton, fullReportButton; // Renamed one button
    private JButton showMartyrsChartButton; // زر جديد للرسم البياني

    private JPanel chartDisplayPanel;
    
    public AnalysisPanel(ImpactDataManager dataManager) {
        this.dataManager = dataManager;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 5, 5)); // Vertical layout for buttons
        // Consider a FlowLayout if you prefer horizontal: new FlowLayout(FlowLayout.CENTER)

        fullReportButton = new JButton("Generate Full Analysis Report");
        mostAffectedButton = new JButton("Show Most Affected Region");
        mwPStatsButton = new JButton("Show Most/Least M/W/P Stats"); // Shortened name
        sortedPrisonersButton = new JButton("Show Sorted Prisoners List");
        allBorderStatusButton = new JButton("Show All Border Statuses");
showMartyrsChartButton = new JButton("Show Martyrs per Region Chart");

        buttonPanel.add(fullReportButton);
        buttonPanel.add(mostAffectedButton);
        buttonPanel.add(mwPStatsButton);
        buttonPanel.add(sortedPrisonersButton);
        buttonPanel.add(allBorderStatusButton);
         buttonPanel.add(showMartyrsChartButton);

            // --- لوحة عرض النتائج (يمكن أن تكون JSplitPane لتقسيم بين النص والرسم البياني) ---
        JSplitPane resultsSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        resultsSplitPane.setResizeWeight(0.6); // إعطاء مساحة أكبر للرسم البياني مبدئيًا
         
        analysisResultsArea = new JTextArea(20, 70); // Increased size
        analysisResultsArea.setEditable(false);
        analysisResultsArea.setFont(new Font("Monospaced", Font.PLAIN, 13)); // Good for text reports
        JScrollPane textScrollPane = new JScrollPane(analysisResultsArea);

        resultsSplitPane.setTopComponent(textScrollPane); // التقارير النصية في الأعلى

        chartDisplayPanel = new JPanel(new BorderLayout()); // لوحة مخصصة للرسم البياني
        chartDisplayPanel.setBorder(BorderFactory.createTitledBorder("Chart Display"));
        JLabel noChartLabel = new JLabel("Click 'Show Martyrs per Region Chart' to generate.", SwingConstants.CENTER);
        chartDisplayPanel.add(noChartLabel, BorderLayout.CENTER); // رسالة أولية
        resultsSplitPane.setBottomComponent(chartDisplayPanel); // الرسم البياني في الأسفل

        
        add(buttonPanel, BorderLayout.WEST); // Buttons on the west for a different layout
          add(resultsSplitPane, BorderLayout.CENTER);

        // Add Action Listeners
        fullReportButton.addActionListener(e -> displayFullReport());
        mostAffectedButton.addActionListener(e -> displayMostAffected());
        mwPStatsButton.addActionListener(e -> displayMWPStats());
        sortedPrisonersButton.addActionListener(e -> displaySortedPrisoners());
        allBorderStatusButton.addActionListener(e -> displayAllBorders());
          showMartyrsChartButton.addActionListener(e -> displayMartyrsPerRegionChart());
    }
        
    


private void displayMartyrsPerRegionChart() {
        if (dataManager == null) {
            chartDisplayPanel.removeAll();
            chartDisplayPanel.add(new JLabel("Error: DataManager not initialized.", SwingConstants.CENTER));
            chartDisplayPanel.revalidate();
            chartDisplayPanel.repaint();
            return;
        }

        Map<String, Integer> data = dataManager.getTotalMartyrsPerRegionForChart();
System.out.println("Data for Martyrs Chart: " + data);
        if (data == null || data.isEmpty()) {
            chartDisplayPanel.removeAll();
            chartDisplayPanel.add(new JLabel("No data available to generate martyrs chart.", SwingConstants.CENTER));
            chartDisplayPanel.revalidate();
            chartDisplayPanel.repaint();
            return;
        }


// 1. إنشاء الـ Dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String seriesLabel = "Total Martyrs"; // يمكن أن يكون لديك عدة سلاسل إذا أردت مقارنة (مثلاً شهداء وجرحى)

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            dataset.addValue(entry.getValue(), seriesLabel, entry.getKey()); // القيمة, اسم السلسلة, اسم الفئة (المنطقة)
        }

        // 2. إنشاء كائن الرسم البياني
        JFreeChart barChart = ChartFactory.createBarChart(
                "Total Martyrs per Region", // عنوان الرسم البياني
                "Region",                   // تسمية المحور السيني (الفئات)
                "Number of Martyrs",        // تسمية المحور الصادي (القيم)
                dataset,                    // البيانات
                PlotOrientation.VERTICAL,   // اتجاه الرسم البياني
                true,                       // هل يتم عرض وسيلة الإيضاح (Legend)؟
                true,                       // هل يتم توليد تلميحات (Tooltips)؟
                false                       // هل يتم توليد روابط URL (URLs)؟
        );

        // 3. تخصيص مظهر الرسم البياني (اختياري ولكن مُحسن)
        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.lightGray); // لون خلفية منطقة الرسم
        plot.setDomainGridlinePaint(Color.white);  // لون خطوط الشبكة الرأسية
        plot.setRangeGridlinePaint(Color.white);   // لون خطوط الشبكة الأفقية

        // تغيير ألوان الأعمدة (إذا أردت)
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(79, 129, 189)); // لون أزرق للسلسلة الأولى (Total Martyrs)
        // renderer.setDrawBarOutline(false); // لإزالة الحدود حول الأعمدة
        // renderer.setItemMargin(0.05); // تقليل المسافة بين الأعمدة في نفس الفئة (إذا كان لديك عدة سلاسل)


        // 4. عرض الرسم البياني في ChartPanel
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(400, 300)); // حجم مبدئي للوحة الرسم البياني

        // تحديث chartDisplayPanel
        chartDisplayPanel.removeAll(); // إزالة أي شيء قديم
        chartDisplayPanel.add(chartPanel, BorderLayout.CENTER);
        chartDisplayPanel.revalidate(); // لإعادة حساب التخطيط
        chartDisplayPanel.repaint();    // لإعادة الرسم
        
        analysisResultsArea.setText("Martyrs per region chart generated.\nScroll down to view the chart if it's not fully visible.");
        analysisResultsArea.setCaretPosition(0);
    }


    private void displayReport(String reportTitle, String reportContent) {
        analysisResultsArea.setText("--- " + reportTitle + " ---\n\n" + reportContent);
        analysisResultsArea.setCaretPosition(0); // تمرير لأعلى منطقة النص
    }

    private void displayFullReport() {
        if (dataManager != null) {
            // ميثود analyzeImpact() في ImpactDataManager يجب أن تستدعي الميثودات الأخرى وتجمع تقريرًا شاملاً
            displayReport("Full Analysis Report", dataManager.analyzeImpact());
        } else {
            analysisResultsArea.setText("Error: DataManager not initialized.");
        }
    }

    private void displayMostAffected() {
        if (dataManager != null) {
            displayReport("Most Affected Region", dataManager.getMostAffectedRegionReport());
        } else {
            analysisResultsArea.setText("Error: DataManager not initialized.");
        }
    }

    private void displayMWPStats() {
        if (dataManager != null) {
            displayReport("Most/Least Martyrs, Wounded, Prisoners Statistics", dataManager.getMostAndLeastMartyrsWoundedPrisonersReport());
        } else {
            analysisResultsArea.setText("Error: DataManager not initialized.");
        }
    }

    
    private void displaySortedPrisoners() {
        if (dataManager != null) {
            displayReport("Prisoners Sorted by Duration", dataManager.getSortedPrisonersByDurationReport());
        } else {
            analysisResultsArea.setText("Error: DataManager not initialized.");
        }
    }

    private void displayAllBorders() {
        if (dataManager != null) {
            displayReport("All Recorded Border Statuses", dataManager.getAllBorderStatusesReport());
        } else {
            analysisResultsArea.setText("Error: DataManager not initialized.");
        }
    }
}