package view;

import component.DateTextField;
import controller.InputValidator;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.BlokLahan;
import model.DataStore;
import model.Panen;

public class PetugasFrame extends javax.swing.JFrame {

    private String selectedPanenId = null;
    private final DecimalFormat df = new DecimalFormat("#,##0.00");

    public PetugasFrame() {
        initComponents();
        applyBranding();
        setLocationRelativeTo(null);
        initCustomData();
    }

    private void applyBranding() {
        java.awt.Image appIcon = util.Images.load("logo.png");
        if (appIcon != null) setIconImage(appIcon);
        pnlHeader.setBorder(new component.ImageBorder("header-bg.png", new java.awt.Color(4, 47, 46), 3));
        javax.swing.ImageIcon brandIcon = util.Images.icon("logo.png", 30);
        if (brandIcon != null) {
            lblLogoHeader.setIcon(brandIcon);
            lblLogoHeader.setIconTextGap(10);
        }
        lblLogoHeader.setForeground(java.awt.Color.WHITE);
        lblUserInfo.setForeground(new java.awt.Color(198, 226, 219));
    }

    private void initCustomData() {
        cmbFilterKomoditas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Semua", "Sawit", "Karet"}));
        cmbModeRekap.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Rentang Tanggal"}));
        tblPanen.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPanenMouseClicked(evt);
            }
        });
        loadBlokComboBox();
        refreshPanenTable();
        refreshRekapTable();
        txtTanggalPanen.setText(LocalDate.now().toString());
        LocalDate today = LocalDate.now();
        txtTglAwal.setText(today.withDayOfMonth(1).toString());
        txtTglAkhir.setText(today.toString());
    }

    private void loadBlokComboBox() {
        String selected = cmbBlokLahan.getSelectedItem() == null ? null : cmbBlokLahan.getSelectedItem().toString();
        String commodity = rbSawit.isSelected() ? "Sawit" : "Karet";
        cmbBlokLahan.removeAllItems();
        List<BlokLahan> bloks = DataStore.getInstance().getBlokList();
        for (BlokLahan b : bloks) {
            if (commodity.equalsIgnoreCase(b.getKomoditas())) cmbBlokLahan.addItem(b.toString());
        }
        if (selected != null) cmbBlokLahan.setSelectedItem(selected);
    }

    private Panen buildPanen(String id) {
        String commodity = rbSawit.isSelected() ? "Sawit" : "Karet";
        String block = cmbBlokLahan.getSelectedItem() == null ? "" : cmbBlokLahan.getSelectedItem().toString();
        String input1 = txtInput1.getText().trim();
        String input2 = txtInput2.getText().trim();
        String rejectText = txtTidakLayak.getText().trim();
        String date = txtTanggalPanen.getText().trim();
        if (block.isEmpty() || !InputValidator.isNonNegativeNumber(input1) || !InputValidator.isNonNegativeNumber(input2)
                || (!rejectText.isEmpty() && !InputValidator.isNonNegativeNumber(rejectText))
                || !InputValidator.isValidDate(date)) {
            JOptionPane.showMessageDialog(this, "Pilih blok, isi angka yang valid, dan gunakan tanggal YYYY-MM-DD.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        double first = Double.parseDouble(input1);
        double second = Double.parseDouble(input2);
        double reject = rejectText.isEmpty() ? 0 : Double.parseDouble(rejectText);
        double total = "Sawit".equals(commodity) ? first * 12.0 + second : first * second / 100.0;
        if (total <= 0 || reject > total) {
            JOptionPane.showMessageDialog(this, "Total panen harus lebih dari nol dan hasil tidak layak tidak boleh melebihi total panen.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        String quality = reject / total <= 0.05 ? "Baik" : "Cukup";
        return new Panen(id, date, block, commodity, first, second, total, reject, quality);
    }

    public void refreshPanenTable() {
        DefaultTableModel model = (DefaultTableModel) tblPanen.getModel();
        model.setRowCount(0);
        List<Panen> list = DataStore.getInstance().getPanenList();
        for (Panen p : list) {
            model.addRow(new Object[]{
                p.getIdPanen(),
                p.getTanggal(),
                p.getBlokLahan(),
                p.getKomoditas(),
                df.format(p.getTotalPanen()),
                p.getKualitas()
            });
        }
    }

    public void refreshRekapTable() {
        DefaultTableModel model = (DefaultTableModel) tblRekap.getModel();
        model.setRowCount(0);
        List<Panen> list = DataStore.getInstance().getPanenList();
        double grandTotal = 0;
        for (Panen p : list) {
            grandTotal += p.getTotalPanen();
            model.addRow(new Object[]{
                p.getIdPanen(),
                p.getTanggal(),
                p.getBlokLahan(),
                p.getKomoditas(),
                df.format(p.getTotalPanen()),
                df.format(p.getTidakLayak()),
                p.getKualitas()
            });
        }
        lblTotalRekap.setText("Total Hasil Panen: " + df.format(grandTotal) + " Kg");
    }

    private void resetPanenForm() {
        selectedPanenId = null;
        rbSawit.setSelected(true);
        updateKomoditasLabels();
        loadBlokComboBox();
        if (cmbBlokLahan.getItemCount() > 0) cmbBlokLahan.setSelectedIndex(0);
        txtInput1.setText("");
        txtInput2.setText("");
        txtTidakLayak.setText("");
        txtTanggalPanen.setText(LocalDate.now().toString());
        tblPanen.clearSelection();
    }

    private void updateKomoditasLabels() {
        if (rbSawit.isSelected()) {
            lblInput1.setText("Jumlah Tandan (Janjang):");
            lblInput2.setText("Jumlah Brondolan (Kg):");
        } else {
            lblInput1.setText("Volume Lateks (Liter):");
            lblInput2.setText("Kadar Karet Kering (%):");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGroupKomoditas = new javax.swing.ButtonGroup();
        pnlHeader = new javax.swing.JPanel();
        lblLogoHeader = new javax.swing.JLabel();
        lblUserInfo = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        tabMain = new javax.swing.JTabbedPane();
        pnlTabPanen = new javax.swing.JPanel();
        splitPanen = new javax.swing.JSplitPane();
        pnlFormPanen = new javax.swing.JPanel();
        lblKomoditas = new javax.swing.JLabel();
        rbSawit = new javax.swing.JRadioButton();
        rbKaret = new javax.swing.JRadioButton();
        lblBlok = new javax.swing.JLabel();
        cmbBlokLahan = new javax.swing.JComboBox<>();
        lblInput1 = new javax.swing.JLabel();
        txtInput1 = new javax.swing.JTextField();
        lblInput2 = new javax.swing.JLabel();
        txtInput2 = new javax.swing.JTextField();
        lblTidakLayak = new javax.swing.JLabel();
        txtTidakLayak = new javax.swing.JTextField();
        lblTanggal = new javax.swing.JLabel();
        txtTanggalPanen = new javax.swing.JTextField();
        btnSimpan = new javax.swing.JButton();
        btnPerbarui = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        pnlTabelPanen = new javax.swing.JPanel();
        lblDaftarPanen = new javax.swing.JLabel();
        scrollPanen = new javax.swing.JScrollPane();
        tblPanen = new javax.swing.JTable();
        pnlTabRekap = new javax.swing.JPanel();
        splitRekap = new javax.swing.JSplitPane();
        pnlFilterRekap = new javax.swing.JPanel();
        lblModeRekap = new javax.swing.JLabel();
        cmbModeRekap = new javax.swing.JComboBox<>();
        lblFilterKomoditas = new javax.swing.JLabel();
        cmbFilterKomoditas = new javax.swing.JComboBox<>();
        lblTglAwal = new javax.swing.JLabel();
        txtTglAwal = new javax.swing.JTextField();
        lblTglAkhir = new javax.swing.JLabel();
        txtTglAkhir = new javax.swing.JTextField();
        btnTampilkanRekap = new javax.swing.JButton();
        btnResetRekap = new javax.swing.JButton();
        pnlTabelRekap = new javax.swing.JPanel();
        lblDaftarRekap = new javax.swing.JLabel();
        scrollRekap = new javax.swing.JScrollPane();
        tblRekap = new javax.swing.JTable();
        lblTotalRekap = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CanopyONE - Menu Pegawai Lapangan");
        setPreferredSize(new java.awt.Dimension(1200, 750));

        pnlHeader.setBackground(new java.awt.Color(255, 255, 255));
        pnlHeader.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(229, 231, 235)));

        lblLogoHeader.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblLogoHeader.setForeground(new java.awt.Color(21, 94, 239));
        lblLogoHeader.setText("CanopyONE Panel Petugas");

        lblUserInfo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblUserInfo.setForeground(new java.awt.Color(75, 85, 99));
        lblUserInfo.setText("Petugas: Lapangan Sawit & Karet");

        btnLogout.setBackground(new java.awt.Color(239, 68, 68));
        btnLogout.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout.setText("Logout");
        btnLogout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlHeaderLayout = new javax.swing.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLogoHeader)
                    .addComponent(lblUserInfo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlHeaderLayout.createSequentialGroup()
                        .addComponent(lblLogoHeader)
                        .addGap(2, 2, 2)
                        .addComponent(lblUserInfo)))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        tabMain.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N

        splitPanen.setDividerLocation(420);
        splitPanen.setDividerSize(6);

        pnlFormPanen.setBackground(new java.awt.Color(255, 255, 255));
        pnlFormPanen.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblKomoditas.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblKomoditas.setText("Komoditas Panen:");

        btnGroupKomoditas.add(rbSawit);
        rbSawit.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        rbSawit.setSelected(true);
        rbSawit.setText("Kelapa Sawit");
        rbSawit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbSawitActionPerformed(evt);
            }
        });

        btnGroupKomoditas.add(rbKaret);
        rbKaret.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        rbKaret.setText("Karet");
        rbKaret.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbKaretActionPerformed(evt);
            }
        });

        lblBlok.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblBlok.setText("Pilih Blok Lahan:");

        cmbBlokLahan.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        lblInput1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblInput1.setText("Jumlah Tandan (Janjang):");

        txtInput1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        lblInput2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblInput2.setText("Jumlah Brondolan (Kg):");

        txtInput2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        lblTidakLayak.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTidakLayak.setText("Hasil Panen Tidak Layak (Kg):");

        txtTidakLayak.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        lblTanggal.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTanggal.setText("Tanggal Panen (YYYY-MM-DD):");

        txtTanggalPanen.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        btnSimpan.setBackground(new java.awt.Color(21, 94, 239));
        btnSimpan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSimpan.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpan.setText("Simpan Panen");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        btnPerbarui.setBackground(new java.awt.Color(245, 158, 11));
        btnPerbarui.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnPerbarui.setForeground(new java.awt.Color(255, 255, 255));
        btnPerbarui.setText("Perbarui");
        btnPerbarui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPerbaruiActionPerformed(evt);
            }
        });

        btnHapus.setBackground(new java.awt.Color(239, 68, 68));
        btnHapus.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnHapus.setForeground(new java.awt.Color(255, 255, 255));
        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnReset.setBackground(new java.awt.Color(107, 114, 128));
        btnReset.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnReset.setForeground(new java.awt.Color(255, 255, 255));
        btnReset.setText("Reset");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlFormPanenLayout = new javax.swing.GroupLayout(pnlFormPanen);
        pnlFormPanen.setLayout(pnlFormPanenLayout);
        pnlFormPanenLayout.setHorizontalGroup(
            pnlFormPanenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormPanenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFormPanenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbBlokLahan, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtInput1)
                    .addComponent(txtInput2)
                    .addComponent(txtTidakLayak)
                    .addComponent(txtTanggalPanen)
                    .addGroup(pnlFormPanenLayout.createSequentialGroup()
                        .addGroup(pnlFormPanenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblKomoditas)
                            .addGroup(pnlFormPanenLayout.createSequentialGroup()
                                .addComponent(rbSawit)
                                .addGap(18, 18, 18)
                                .addComponent(rbKaret))
                            .addComponent(lblBlok)
                            .addComponent(lblInput1)
                            .addComponent(lblInput2)
                            .addComponent(lblTidakLayak)
                            .addComponent(lblTanggal))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlFormPanenLayout.createSequentialGroup()
                        .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPerbarui, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 69, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlFormPanenLayout.setVerticalGroup(
            pnlFormPanenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormPanenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblKomoditas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormPanenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbSawit)
                    .addComponent(rbKaret))
                .addGap(12, 12, 12)
                .addComponent(lblBlok)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbBlokLahan, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblInput1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtInput1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblInput2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtInput2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTidakLayak)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTidakLayak, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTanggal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTanggalPanen, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addGroup(pnlFormPanenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPerbarui, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(87, Short.MAX_VALUE))
        );

        splitPanen.setLeftComponent(pnlFormPanen);

        pnlTabelPanen.setBackground(new java.awt.Color(255, 255, 255));
        pnlTabelPanen.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblDaftarPanen.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblDaftarPanen.setText("Daftar Hasil Panen:");

        tblPanen.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblPanen.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Panen", "Tanggal", "Blok Lahan", "Komoditas", "Total Panen (Kg)", "Kualitas"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPanen.setRowHeight(26);
        scrollPanen.setViewportView(tblPanen);

        javax.swing.GroupLayout pnlTabelPanenLayout = new javax.swing.GroupLayout(pnlTabelPanen);
        pnlTabelPanen.setLayout(pnlTabelPanenLayout);
        pnlTabelPanenLayout.setHorizontalGroup(
            pnlTabelPanenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTabelPanenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTabelPanenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPanen, javax.swing.GroupLayout.DEFAULT_SIZE, 712, Short.MAX_VALUE)
                    .addGroup(pnlTabelPanenLayout.createSequentialGroup()
                        .addComponent(lblDaftarPanen)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlTabelPanenLayout.setVerticalGroup(
            pnlTabelPanenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTabelPanenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDaftarPanen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollPanen, javax.swing.GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE)
                .addContainerGap())
        );

        splitPanen.setRightComponent(pnlTabelPanen);

        javax.swing.GroupLayout pnlTabPanenLayout = new javax.swing.GroupLayout(pnlTabPanen);
        pnlTabPanen.setLayout(pnlTabPanenLayout);
        pnlTabPanenLayout.setHorizontalGroup(
            pnlTabPanenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPanen)
        );
        pnlTabPanenLayout.setVerticalGroup(
            pnlTabPanenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPanen)
        );

        tabMain.addTab("1. Kelola Data Panen", pnlTabPanen);

        splitRekap.setDividerLocation(380);
        splitRekap.setDividerSize(6);

        pnlFilterRekap.setBackground(new java.awt.Color(255, 255, 255));
        pnlFilterRekap.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblModeRekap.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblModeRekap.setText("Mode Rekapitulasi:");

        cmbModeRekap.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        lblFilterKomoditas.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblFilterKomoditas.setText("Filter Komoditas:");

        cmbFilterKomoditas.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        lblTglAwal.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTglAwal.setText("Tanggal Awal (YYYY-MM-DD):");

        txtTglAwal.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtTglAwal.setText("2026-10-01");

        lblTglAkhir.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTglAkhir.setText("Tanggal Akhir (YYYY-MM-DD):");

        txtTglAkhir.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtTglAkhir.setText("2026-10-31");

        btnTampilkanRekap.setBackground(new java.awt.Color(21, 94, 239));
        btnTampilkanRekap.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnTampilkanRekap.setForeground(new java.awt.Color(255, 255, 255));
        btnTampilkanRekap.setText("Tampilkan Rekapitulasi");
        btnTampilkanRekap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTampilkanRekapActionPerformed(evt);
            }
        });

        btnResetRekap.setBackground(new java.awt.Color(107, 114, 128));
        btnResetRekap.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnResetRekap.setForeground(new java.awt.Color(255, 255, 255));
        btnResetRekap.setText("Tampilkan Semua Data");
        btnResetRekap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetRekapActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlFilterRekapLayout = new javax.swing.GroupLayout(pnlFilterRekap);
        pnlFilterRekap.setLayout(pnlFilterRekapLayout);
        pnlFilterRekapLayout.setHorizontalGroup(
            pnlFilterRekapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilterRekapLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFilterRekapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbModeRekap, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbFilterKomoditas, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTglAwal)
                    .addComponent(txtTglAkhir)
                    .addComponent(btnTampilkanRekap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnResetRekap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlFilterRekapLayout.createSequentialGroup()
                        .addGroup(pnlFilterRekapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblModeRekap)
                            .addComponent(lblFilterKomoditas)
                            .addComponent(lblTglAwal)
                            .addComponent(lblTglAkhir))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlFilterRekapLayout.setVerticalGroup(
            pnlFilterRekapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilterRekapLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblModeRekap)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbModeRekap, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblFilterKomoditas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbFilterKomoditas, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblTglAwal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTglAwal, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblTglAkhir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTglAkhir, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(btnTampilkanRekap, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnResetRekap, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(128, Short.MAX_VALUE))
        );

        splitRekap.setLeftComponent(pnlFilterRekap);

        pnlTabelRekap.setBackground(new java.awt.Color(255, 255, 255));
        pnlTabelRekap.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblDaftarRekap.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblDaftarRekap.setText("Hasil Rekapitulasi Data Panen:");

        tblRekap.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblRekap.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Panen", "Tanggal", "Blok Lahan", "Komoditas", "Total (Kg)", "Tidak Layak (Kg)", "Kualitas"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblRekap.setRowHeight(26);
        scrollRekap.setViewportView(tblRekap);

        lblTotalRekap.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblTotalRekap.setForeground(new java.awt.Color(21, 94, 239));
        lblTotalRekap.setText("Total Hasil Panen: 0.00 Kg");

        javax.swing.GroupLayout pnlTabelRekapLayout = new javax.swing.GroupLayout(pnlTabelRekap);
        pnlTabelRekap.setLayout(pnlTabelRekapLayout);
        pnlTabelRekapLayout.setHorizontalGroup(
            pnlTabelRekapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTabelRekapLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTabelRekapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollRekap, javax.swing.GroupLayout.DEFAULT_SIZE, 752, Short.MAX_VALUE)
                    .addGroup(pnlTabelRekapLayout.createSequentialGroup()
                        .addGroup(pnlTabelRekapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblDaftarRekap)
                            .addComponent(lblTotalRekap))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlTabelRekapLayout.setVerticalGroup(
            pnlTabelRekapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTabelRekapLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDaftarRekap)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollRekap, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(lblTotalRekap)
                .addContainerGap())
        );

        splitRekap.setRightComponent(pnlTabelRekap);

        javax.swing.GroupLayout pnlTabRekapLayout = new javax.swing.GroupLayout(pnlTabRekap);
        pnlTabRekap.setLayout(pnlTabRekapLayout);
        pnlTabRekapLayout.setHorizontalGroup(
            pnlTabRekapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitRekap)
        );
        pnlTabRekapLayout.setVerticalGroup(
            pnlTabRekapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitRekap)
        );

        tabMain.addTab("2. Rekapitulasi Hasil Panen", pnlTabRekap);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tabMain)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabMain))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        new LoginFrame().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void rbSawitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbSawitActionPerformed
        updateKomoditasLabels();
        loadBlokComboBox();
    }//GEN-LAST:event_rbSawitActionPerformed

    private void rbKaretActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbKaretActionPerformed
        updateKomoditasLabels();
        loadBlokComboBox();
    }//GEN-LAST:event_rbKaretActionPerformed

    private void tblPanenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPanenMouseClicked
        int selectedRow = tblPanen.getSelectedRow();
        if (selectedRow != -1) {
            selectedPanenId = tblPanen.getValueAt(selectedRow, 0).toString();
            txtTanggalPanen.setText(tblPanen.getValueAt(selectedRow, 1).toString());
            String blok = tblPanen.getValueAt(selectedRow, 2).toString();
            String komoditas = tblPanen.getValueAt(selectedRow, 3).toString();
            if ("Sawit".equalsIgnoreCase(komoditas)) {
                rbSawit.setSelected(true);
            } else {
                rbKaret.setSelected(true);
            }
            updateKomoditasLabels();
            loadBlokComboBox();
            cmbBlokLahan.setSelectedItem(blok);

            for (Panen p : DataStore.getInstance().getPanenList()) {
                if (p.getIdPanen().equalsIgnoreCase(selectedPanenId)) {
                    txtInput1.setText(String.valueOf(p.getInput1()));
                    txtInput2.setText(String.valueOf(p.getInput2()));
                    txtTidakLayak.setText(String.valueOf(p.getTidakLayak()));
                    break;
                }
            }
        }
    }//GEN-LAST:event_tblPanenMouseClicked

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        String newId = DataStore.getInstance().generatePanenId();
        Panen panen = buildPanen(newId);
        if (panen == null) return;
        DataStore.getInstance().addPanen(panen);
        JOptionPane.showMessageDialog(this, "Data panen berhasil disimpan dengan ID: " + newId);
        refreshPanenTable();
        refreshRekapTable();
        resetPanenForm();
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnPerbaruiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPerbaruiActionPerformed
        if (selectedPanenId == null) {
            JOptionPane.showMessageDialog(this, "Pilih data panen pada tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Panen panen = buildPanen(selectedPanenId);
        if (panen == null) return;
        if (!DataStore.getInstance().updatePanen(panen)) return;
        JOptionPane.showMessageDialog(this, "Data panen berhasil diperbarui!");
        refreshPanenTable();
        refreshRekapTable();
        resetPanenForm();
    }//GEN-LAST:event_btnPerbaruiActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        if (selectedPanenId == null) {
            JOptionPane.showMessageDialog(this, "Pilih data panen pada tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin menghapus data yang dipilih?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            DataStore.getInstance().deletePanen(selectedPanenId);
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
            refreshPanenTable();
            refreshRekapTable();
            resetPanenForm();
        }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        resetPanenForm();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnTampilkanRekapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTampilkanRekapActionPerformed
        String filterKom = cmbFilterKomoditas.getSelectedItem() == null ? "Semua" : cmbFilterKomoditas.getSelectedItem().toString();
        String tglAwal = txtTglAwal.getText().trim();
        String tglAkhir = txtTglAkhir.getText().trim();
        if (!InputValidator.isValidOptionalDateRange(tglAwal, tglAkhir)) {
            JOptionPane.showMessageDialog(this, "Tanggal rekap harus valid dan tanggal awal tidak boleh melewati tanggal akhir.", "Rentang Tanggal Tidak Valid", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tblRekap.getModel();
        model.setRowCount(0);
        List<Panen> list = DataStore.getInstance().getPanenList();
        double grandTotal = 0;
        int count = 0;

        for (Panen p : list) {
            boolean matchKom = "Semua".equalsIgnoreCase(filterKom) || p.getKomoditas().equalsIgnoreCase(filterKom);
            boolean matchTgl = true;
            if (!tglAwal.isEmpty() && p.getTanggal().compareTo(tglAwal) < 0) matchTgl = false;
            if (!tglAkhir.isEmpty() && p.getTanggal().compareTo(tglAkhir) > 0) matchTgl = false;

            if (matchKom && matchTgl) {
                grandTotal += p.getTotalPanen();
                count++;
                model.addRow(new Object[]{
                    p.getIdPanen(),
                    p.getTanggal(),
                    p.getBlokLahan(),
                    p.getKomoditas(),
                    df.format(p.getTotalPanen()),
                    df.format(p.getTidakLayak()),
                    p.getKualitas()
                });
            }
        }

        lblTotalRekap.setText("Total Hasil Panen: " + df.format(grandTotal) + " Kg (" + count + " data)");

        if (count == 0) {
            JOptionPane.showMessageDialog(this,
                "Data panen pada kriteria/periode ini belum tersedia.",
                "Informasi",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnTampilkanRekapActionPerformed

    private void btnResetRekapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetRekapActionPerformed
        cmbFilterKomoditas.setSelectedIndex(0);
        txtTglAwal.setText("");
        txtTglAkhir.setText("");
        refreshRekapTable();
    }//GEN-LAST:event_btnResetRekapActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup btnGroupKomoditas;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnPerbarui;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnResetRekap;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnTampilkanRekap;
    private javax.swing.JComboBox<String> cmbBlokLahan;
    private javax.swing.JComboBox<String> cmbFilterKomoditas;
    private javax.swing.JComboBox<String> cmbModeRekap;
    private javax.swing.JLabel lblBlok;
    private javax.swing.JLabel lblDaftarPanen;
    private javax.swing.JLabel lblDaftarRekap;
    private javax.swing.JLabel lblFilterKomoditas;
    private javax.swing.JLabel lblInput1;
    private javax.swing.JLabel lblInput2;
    private javax.swing.JLabel lblKomoditas;
    private javax.swing.JLabel lblLogoHeader;
    private javax.swing.JLabel lblModeRekap;
    private javax.swing.JLabel lblTanggal;
    private javax.swing.JLabel lblTglAkhir;
    private javax.swing.JLabel lblTglAwal;
    private javax.swing.JLabel lblTidakLayak;
    private javax.swing.JLabel lblTotalRekap;
    private javax.swing.JLabel lblUserInfo;
    private javax.swing.JPanel pnlFilterRekap;
    private javax.swing.JPanel pnlFormPanen;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlTabPanen;
    private javax.swing.JPanel pnlTabRekap;
    private javax.swing.JPanel pnlTabelPanen;
    private javax.swing.JPanel pnlTabelRekap;
    private javax.swing.JRadioButton rbKaret;
    private javax.swing.JRadioButton rbSawit;
    private javax.swing.JScrollPane scrollPanen;
    private javax.swing.JScrollPane scrollRekap;
    private javax.swing.JSplitPane splitPanen;
    private javax.swing.JSplitPane splitRekap;
    private javax.swing.JTabbedPane tabMain;
    private javax.swing.JTable tblPanen;
    private javax.swing.JTable tblRekap;
    private javax.swing.JTextField txtInput1;
    private javax.swing.JTextField txtInput2;
    private javax.swing.JTextField txtTanggalPanen;
    private javax.swing.JTextField txtTglAkhir;
    private javax.swing.JTextField txtTglAwal;
    private javax.swing.JTextField txtTidakLayak;
    // End of variables declaration//GEN-END:variables
}
