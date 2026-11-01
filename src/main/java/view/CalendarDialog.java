package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class CalendarDialog extends javax.swing.JDialog {

    private static final String[] MONTHS = {
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    };
    private static final String[] WEEKDAYS = {"Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"};

    private static final Color ACCENT = new Color(21, 94, 239);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color TEXT = new Color(51, 65, 85);
    private static final Color GRID_LINE = new Color(226, 232, 240);

    private final JButton[] dayButtons = new JButton[42];
    private LocalDate selected;
    private YearMonth viewMonth;

    public CalendarDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        start(parse(null));
    }

    public CalendarDialog(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
        start(parse(null));
    }

    private CalendarDialog(Window owner, boolean modal, String initial) {
        super(owner, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
        initComponents();
        start(parse(initial));
    }

    public static String showDialog(Component parent, String initial) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        CalendarDialog dialog;
        if (owner instanceof java.awt.Frame) {
            dialog = new CalendarDialog(owner, true, initial);
        } else if (owner instanceof java.awt.Dialog) {
            dialog = new CalendarDialog(owner, true, initial);
        } else {
            dialog = new CalendarDialog((Window) null, true, initial);
        }
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        return dialog.selected == null ? null : dialog.selected.toString();
    }

    public static LocalDate showDateDialog(Component parent, String initial) {
        String value = showDialog(parent, initial);
        return value == null ? null : LocalDate.parse(value);
    }

    private void start(LocalDate base) {
        if (base == null) base = LocalDate.now();
        selected = base;
        viewMonth = YearMonth.from(base);
        applyStyles();
        buildDays();
        setSize(352, 404);
    }

    private void applyStyles() {
        getContentPane().setBackground(Color.WHITE);
        pnlNavigation.setOpaque(true);
        pnlWeekdays.setOpaque(true);
        pnlDays.setOpaque(true);
        pnlFooter.setOpaque(true);
        pnlWeekdays.setLayout(new GridLayout(1, WEEKDAYS.length, 4, 0));
        pnlDays.setLayout(new GridLayout(6, 7, 4, 4));

        JLabel[] weekdayLabels = {lblSen, lblSel, lblRab, lblKam, lblJum, lblSab, lblMin};
        for (JLabel label : weekdayLabels) {
            label.setFont(label.getFont().deriveFont(Font.BOLD, 11f));
            label.setForeground(MUTED);
            label.setHorizontalAlignment(JLabel.CENTER);
        }

        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 14f));

        for (JButton button : new JButton[]{btnPrev, btnNext, btnToday, btnCancel}) {
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        cmbMonth.setModel(new javax.swing.DefaultComboBoxModel<>(MONTHS));
        cmbMonth.setSelectedIndex(viewMonth.getMonthValue() - 1);

        int currentYear = LocalDate.now().getYear();
        for (int year = currentYear - 20; year <= currentYear + 10; year++) {
            cmbYear.addItem(year);
        }
        cmbYear.setSelectedItem(viewMonth.getYear());

        btnPrev.addActionListener(e -> moveMonth(-1));
        btnNext.addActionListener(e -> moveMonth(1));
        cmbMonth.addActionListener(e -> {
            int index = cmbMonth.getSelectedIndex();
            if (index >= 0 && index + 1 != viewMonth.getMonthValue()) {
                viewMonth = YearMonth.of(viewMonth.getYear(), index + 1);
                buildDays();
            }
        });
        cmbYear.addActionListener(e -> {
            Object item = cmbYear.getSelectedItem();
            if (item instanceof Integer) {
                int year = (Integer) item;
                if (year != viewMonth.getYear()) {
                    viewMonth = YearMonth.of(year, viewMonth.getMonthValue());
                    buildDays();
                }
            }
        });
        btnToday.addActionListener(e -> choose(LocalDate.now()));
        btnCancel.addActionListener(e -> {
            selected = null;
            dispose();
        });

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
        getRootPane().getActionMap().put("cancel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selected = null;
                dispose();
            }
        });
    }

    private static LocalDate parse(String value) {
        if (value == null) return null;
        String text = value.trim();
        if (text.isEmpty()) return null;
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public LocalDate getSelected() {
        return selected;
    }

    private void choose(LocalDate date) {
        selected = date;
        dispose();
    }

    private void moveMonth(int delta) {
        viewMonth = viewMonth.plusMonths(delta);
        cmbMonth.setSelectedIndex(viewMonth.getMonthValue() - 1);
        cmbYear.setSelectedItem(viewMonth.getYear());
        buildDays();
    }

    private void buildDays() {
        pnlDays.removeAll();
        int offset = viewMonth.atDay(1).getDayOfWeek().getValue() - 1;

        for (int index = 0; index < dayButtons.length; index++) {
            if (dayButtons[index] == null) {
                JButton button = new JButton();
                button.setFocusable(false);
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                button.setMargin(new Insets(2, 2, 2, 2));
                button.addActionListener(e -> {
                    Object value = button.getClientProperty("tanggal");
                    if (value instanceof LocalDate) choose((LocalDate) value);
                });
                dayButtons[index] = button;
            }

            JButton button = dayButtons[index];
            int dayOfMonth = index - offset + 1;
            if (dayOfMonth < 1 || dayOfMonth > viewMonth.lengthOfMonth()) {
                button.setText("");
                button.setEnabled(false);
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setContentAreaFilled(false);
                pnlDays.add(button);
                continue;
            }

            LocalDate date = viewMonth.atDay(dayOfMonth);
            boolean isSelected = date.equals(selected);
            boolean isToday = date.equals(LocalDate.now());
            button.setEnabled(true);
            button.setContentAreaFilled(true);
            button.setOpaque(true);
            button.setText(String.valueOf(dayOfMonth));
            button.putClientProperty("tanggal", date);
            button.setFont(button.getFont().deriveFont(isSelected || isToday ? Font.BOLD : Font.PLAIN));
            button.setForeground(isSelected ? Color.WHITE : (isToday ? ACCENT : TEXT));
            button.setBackground(isSelected ? ACCENT : Color.WHITE);
            button.setBorder(BorderFactory.createLineBorder(isSelected ? ACCENT : GRID_LINE));
            pnlDays.add(button);
        }

        pnlDays.revalidate();
        pnlDays.repaint();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        pnlNavigation = new javax.swing.JPanel();
        btnPrev = new javax.swing.JButton();
        cmbMonth = new javax.swing.JComboBox();
        cmbYear = new javax.swing.JComboBox();
        btnNext = new javax.swing.JButton();
        pnlWeekdays = new javax.swing.JPanel();
        lblSen = new javax.swing.JLabel();
        lblSel = new javax.swing.JLabel();
        lblRab = new javax.swing.JLabel();
        lblKam = new javax.swing.JLabel();
        lblJum = new javax.swing.JLabel();
        lblSab = new javax.swing.JLabel();
        lblMin = new javax.swing.JLabel();
        pnlDays = new javax.swing.JPanel();
        pnlFooter = new javax.swing.JPanel();
        btnToday = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Pilih Tanggal");
        setResizable(false);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTitle.setText("Pilih Tanggal");

        pnlNavigation.setBackground(new java.awt.Color(255, 255, 255));

        btnPrev.setText("<");
        btnPrev.setFocusable(false);

        btnNext.setText(">");
        btnNext.setFocusable(false);

        javax.swing.GroupLayout pnlNavigationLayout = new javax.swing.GroupLayout(pnlNavigation);
        pnlNavigation.setLayout(pnlNavigationLayout);
        pnlNavigationLayout.setHorizontalGroup(
            pnlNavigationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNavigationLayout.createSequentialGroup()
                .addComponent(btnPrev)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnNext)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlNavigationLayout.setVerticalGroup(
            pnlNavigationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNavigationLayout.createSequentialGroup()
                .addGroup(pnlNavigationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPrev)
                    .addComponent(cmbMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNext))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlWeekdays.setBackground(new java.awt.Color(255, 255, 255));

        lblSen.setText("Sen");

        lblSel.setText("Sel");

        lblRab.setText("Rab");

        lblKam.setText("Kam");

        lblJum.setText("Jum");

        lblSab.setText("Sab");

        lblMin.setText("Min");

        javax.swing.GroupLayout pnlWeekdaysLayout = new javax.swing.GroupLayout(pnlWeekdays);
        pnlWeekdays.setLayout(pnlWeekdaysLayout);
        pnlWeekdaysLayout.setHorizontalGroup(
            pnlWeekdaysLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlWeekdaysLayout.createSequentialGroup()
                .addComponent(lblSen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblRab)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblKam)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblJum)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSab)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblMin)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlWeekdaysLayout.setVerticalGroup(
            pnlWeekdaysLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlWeekdaysLayout.createSequentialGroup()
                .addGroup(pnlWeekdaysLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSen)
                    .addComponent(lblSel)
                    .addComponent(lblRab)
                    .addComponent(lblKam)
                    .addComponent(lblJum)
                    .addComponent(lblSab)
                    .addComponent(lblMin))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlDays.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout pnlDaysLayout = new javax.swing.GroupLayout(pnlDays);
        pnlDays.setLayout(pnlDaysLayout);
        pnlDaysLayout.setHorizontalGroup(
            pnlDaysLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 261, Short.MAX_VALUE)
        );
        pnlDaysLayout.setVerticalGroup(
            pnlDaysLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 210, Short.MAX_VALUE)
        );

        pnlFooter.setBackground(new java.awt.Color(255, 255, 255));

        btnToday.setText("Hari Ini");
        btnToday.setFocusable(false);

        btnCancel.setText("Batal");
        btnCancel.setFocusable(false);

        javax.swing.GroupLayout pnlFooterLayout = new javax.swing.GroupLayout(pnlFooter);
        pnlFooter.setLayout(pnlFooterLayout);
        pnlFooterLayout.setHorizontalGroup(
            pnlFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFooterLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnToday)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancel))
        );
        pnlFooterLayout.setVerticalGroup(
            pnlFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFooterLayout.createSequentialGroup()
                .addGroup(pnlFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnToday)
                    .addComponent(btnCancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitle)
                    .addComponent(pnlNavigation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlWeekdays, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlFooter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlDays, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlNavigation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlWeekdays, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlDays, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlFooter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrev;
    private javax.swing.JButton btnToday;
    private javax.swing.JComboBox cmbMonth;
    private javax.swing.JComboBox cmbYear;
    private javax.swing.JLabel lblJum;
    private javax.swing.JLabel lblKam;
    private javax.swing.JLabel lblMin;
    private javax.swing.JLabel lblRab;
    private javax.swing.JLabel lblSab;
    private javax.swing.JLabel lblSel;
    private javax.swing.JLabel lblSen;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlDays;
    private javax.swing.JPanel pnlFooter;
    private javax.swing.JPanel pnlNavigation;
    private javax.swing.JPanel pnlWeekdays;
    // End of variables declaration//GEN-END:variables
}
