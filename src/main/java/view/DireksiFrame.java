package view;

import component.DateTextField;
import controller.InputValidator;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.DataStore;
import model.Evaluasi;
import model.Panen;

public class DireksiFrame extends javax.swing.JFrame {

    private String selectedEvalId = null;
    private final DecimalFormat df = new DecimalFormat("#,##0.00");

    public DireksiFrame() {
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
        tblEvaluasi.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblEvaluasiMouseClicked(evt);
            }
        });
        refreshRekapTable();
        refreshEvalTable();
        LocalDate today = LocalDate.now();
        String start = today.withDayOfMonth(1).toString();
        txtEvalAwal.setText(start);
        txtEvalAkhir.setText(today.toString());
        txtTglAwal.setText(start);
        txtTglAkhir.setText(today.toString());
    }

    public void refreshRekapTable() {
        DefaultTableModel model = (DefaultTableModel) tblRekap.getModel();
        model.setRowCount(0);
        double total = 0;
        for (Panen p : DataStore.getInstance().getPanenList()) {
            total += p.getTotalPanen();
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
        lblTotalRekap.setText("Total Hasil Panen: " + df.format(total) + " Kg");
    }

    public void refreshEvalTable() {
        DefaultTableModel model = (DefaultTableModel) tblEvaluasi.getModel();
        model.setRowCount(0);
        for (Evaluasi ev : DataStore.getInstance().getEvaluasiList()) {
            model.addRow(new Object[]{
                ev.getIdEval(),
                ev.getTglAwal() + " s/d " + ev.getTglAkhir(),
                df.format(ev.getTotalPanen()) + " Kg",
                df.format(ev.getPersenTidakLayak()) + "%",
                ev.getStatus(),
                ev.getCatatan()
            });
        }
    }

    private void resetEvalForm() {
        selectedEvalId = null;
        LocalDate today = LocalDate.now();
        txtEvalAwal.setText(today.withDayOfMonth(1).toString());
        txtEvalAkhir.setText(today.toString());
        lblTotalBulan.setText("0.00 Kg");
        lblPersenTidakLayak.setText("0.00%");
        txtStatusEval.setText("");
        txtCatatanTindakLanjut.setText("");
        tblEvaluasi.clearSelection();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlHeader = new javax.swing.JPanel();
        lblLogoHeader = new javax.swing.JLabel();
        lblUserInfo = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        tabMain = new javax.swing.JTabbedPane();
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
        pnlTabEval = new javax.swing.JPanel();
        splitEval = new javax.swing.JSplitPane();
        pnlFormEval = new javax.swing.JPanel();
        lblEvalPeriode = new javax.swing.JLabel();
        lblEvalAwal = new javax.swing.JLabel();
        txtEvalAwal = new javax.swing.JTextField();
        lblEvalAkhir = new javax.swing.JLabel();
        txtEvalAkhir = new javax.swing.JTextField();
        btnHitungMetrik = new javax.swing.JButton();
        pnlMetrikCard = new javax.swing.JPanel();
        lblTitleTotalBulan = new javax.swing.JLabel();
        lblTotalBulan = new javax.swing.JLabel();
        lblTitlePersen = new javax.swing.JLabel();
        lblPersenTidakLayak = new javax.swing.JLabel();
        lblStatusEval = new javax.swing.JLabel();
        txtStatusEval = new javax.swing.JTextField();
        lblCatatan = new javax.swing.JLabel();
        scrollCatatan = new javax.swing.JScrollPane();
        txtCatatanTindakLanjut = new javax.swing.JTextArea();
        btnSimpanEval = new javax.swing.JButton();
        btnPerbaruiEval = new javax.swing.JButton();
        btnHapusEval = new javax.swing.JButton();
        btnResetEval = new javax.swing.JButton();
        pnlTabelEval = new javax.swing.JPanel();
        lblDaftarEval = new javax.swing.JLabel();
        scrollEval = new javax.swing.JScrollPane();
        tblEvaluasi = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CanopyONE - Dashboard Direksi");
        setPreferredSize(new java.awt.Dimension(1200, 750));

        pnlHeader.setBackground(new java.awt.Color(255, 255, 255));
        pnlHeader.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(229, 231, 235)));

        lblLogoHeader.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblLogoHeader.setForeground(new java.awt.Color(21, 94, 239));
        lblLogoHeader.setText("CanopyONE Panel Direksi");

        lblUserInfo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblUserInfo.setForeground(new java.awt.Color(75, 85, 99));
        lblUserInfo.setText("Monitoring Kinerja & Evaluasi Hasil Panen Komoditas");

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

        tabMain.addTab("1. Rekapitulasi Hasil Panen", pnlTabRekap);

        splitEval.setDividerLocation(420);
        splitEval.setDividerSize(6);

        pnlFormEval.setBackground(new java.awt.Color(255, 255, 255));
        pnlFormEval.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblEvalPeriode.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblEvalPeriode.setText("Periode Tanggal Evaluasi:");

        lblEvalAwal.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblEvalAwal.setText("Dari Tanggal:");

        txtEvalAwal.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtEvalAwal.setText("2026-10-01");

        lblEvalAkhir.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblEvalAkhir.setText("Sampai Tanggal:");

        txtEvalAkhir.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtEvalAkhir.setText("2026-10-31");

        btnHitungMetrik.setBackground(new java.awt.Color(21, 94, 239));
        btnHitungMetrik.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnHitungMetrik.setForeground(new java.awt.Color(255, 255, 255));
        btnHitungMetrik.setText("Kalkulasi Metrik Otomatis");
        btnHitungMetrik.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHitungMetrik.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHitungMetrikActionPerformed(evt);
            }
        });

        pnlMetrikCard.setBackground(new java.awt.Color(248, 250, 252));
        pnlMetrikCard.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblTitleTotalBulan.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblTitleTotalBulan.setForeground(new java.awt.Color(100, 116, 139));
        lblTitleTotalBulan.setText("TOTAL HASIL PERIODE:");

        lblTotalBulan.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTotalBulan.setForeground(new java.awt.Color(15, 23, 42));
        lblTotalBulan.setText("2,910.00 Kg");

        lblTitlePersen.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblTitlePersen.setForeground(new java.awt.Color(100, 116, 139));
        lblTitlePersen.setText("PERSENTASE TIDAK LAYAK:");

        lblPersenTidakLayak.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblPersenTidakLayak.setForeground(new java.awt.Color(220, 38, 38));
        lblPersenTidakLayak.setText("3.44%");

        javax.swing.GroupLayout pnlMetrikCardLayout = new javax.swing.GroupLayout(pnlMetrikCard);
        pnlMetrikCard.setLayout(pnlMetrikCardLayout);
        pnlMetrikCardLayout.setHorizontalGroup(
            pnlMetrikCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMetrikCardLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(pnlMetrikCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitleTotalBulan)
                    .addComponent(lblTotalBulan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlMetrikCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitlePersen)
                    .addComponent(lblPersenTidakLayak))
                .addGap(18, 18, 18))
        );
        pnlMetrikCardLayout.setVerticalGroup(
            pnlMetrikCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMetrikCardLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(pnlMetrikCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTitleTotalBulan)
                    .addComponent(lblTitlePersen))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMetrikCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalBulan)
                    .addComponent(lblPersenTidakLayak))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        lblStatusEval.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblStatusEval.setText("Status Evaluasi (Opsional):");

        txtStatusEval.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        lblCatatan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblCatatan.setText("Catatan Tindak Lanjut (Opsional):");

        txtCatatanTindakLanjut.setColumns(20);
        txtCatatanTindakLanjut.setLineWrap(true);
        txtCatatanTindakLanjut.setRows(3);
        txtCatatanTindakLanjut.setWrapStyleWord(true);
        scrollCatatan.setViewportView(txtCatatanTindakLanjut);

        btnSimpanEval.setBackground(new java.awt.Color(21, 94, 239));
        btnSimpanEval.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSimpanEval.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpanEval.setText("Simpan Evaluasi");
        btnSimpanEval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanEvalActionPerformed(evt);
            }
        });

        btnPerbaruiEval.setBackground(new java.awt.Color(245, 158, 11));
        btnPerbaruiEval.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnPerbaruiEval.setForeground(new java.awt.Color(255, 255, 255));
        btnPerbaruiEval.setText("Perbarui");
        btnPerbaruiEval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPerbaruiEvalActionPerformed(evt);
            }
        });

        btnHapusEval.setBackground(new java.awt.Color(239, 68, 68));
        btnHapusEval.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnHapusEval.setForeground(new java.awt.Color(255, 255, 255));
        btnHapusEval.setText("Hapus");
        btnHapusEval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusEvalActionPerformed(evt);
            }
        });

        btnResetEval.setBackground(new java.awt.Color(107, 114, 128));
        btnResetEval.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnResetEval.setForeground(new java.awt.Color(255, 255, 255));
        btnResetEval.setText("Reset");
        btnResetEval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetEvalActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlFormEvalLayout = new javax.swing.GroupLayout(pnlFormEval);
        pnlFormEval.setLayout(pnlFormEvalLayout);
        pnlFormEvalLayout.setHorizontalGroup(
            pnlFormEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormEvalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFormEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnHitungMetrik, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlMetrikCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtStatusEval)
                    .addComponent(scrollCatatan)
                    .addGroup(pnlFormEvalLayout.createSequentialGroup()
                        .addGroup(pnlFormEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblEvalAwal)
                            .addComponent(txtEvalAwal, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pnlFormEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblEvalAkhir)
                            .addComponent(txtEvalAkhir, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlFormEvalLayout.createSequentialGroup()
                        .addGroup(pnlFormEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblEvalPeriode)
                            .addComponent(lblStatusEval)
                            .addComponent(lblCatatan))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlFormEvalLayout.createSequentialGroup()
                        .addComponent(btnSimpanEval, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPerbaruiEval, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHapusEval, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnResetEval, javax.swing.GroupLayout.PREFERRED_SIZE, 63, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlFormEvalLayout.setVerticalGroup(
            pnlFormEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormEvalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblEvalPeriode)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEvalAwal)
                    .addComponent(lblEvalAkhir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFormEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEvalAwal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEvalAkhir, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnHitungMetrik, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlMetrikCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblStatusEval)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtStatusEval, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblCatatan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollCatatan, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pnlFormEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpanEval, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPerbaruiEval, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHapusEval, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnResetEval, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(50, Short.MAX_VALUE))
        );

        splitEval.setLeftComponent(pnlFormEval);

        pnlTabelEval.setBackground(new java.awt.Color(255, 255, 255));
        pnlTabelEval.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblDaftarEval.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblDaftarEval.setText("Daftar Riwayat Rekam Evaluasi Hasil Panen:");

        tblEvaluasi.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblEvaluasi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Eval", "Periode", "Total Panen", "Tidak Layak", "Status", "Catatan Tindak Lanjut"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblEvaluasi.setRowHeight(26);
        scrollEval.setViewportView(tblEvaluasi);

        javax.swing.GroupLayout pnlTabelEvalLayout = new javax.swing.GroupLayout(pnlTabelEval);
        pnlTabelEval.setLayout(pnlTabelEvalLayout);
        pnlTabelEvalLayout.setHorizontalGroup(
            pnlTabelEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTabelEvalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTabelEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollEval, javax.swing.GroupLayout.DEFAULT_SIZE, 712, Short.MAX_VALUE)
                    .addGroup(pnlTabelEvalLayout.createSequentialGroup()
                        .addComponent(lblDaftarEval)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlTabelEvalLayout.setVerticalGroup(
            pnlTabelEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTabelEvalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDaftarEval)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollEval, javax.swing.GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE)
                .addContainerGap())
        );

        splitEval.setRightComponent(pnlTabelEval);

        javax.swing.GroupLayout pnlTabEvalLayout = new javax.swing.GroupLayout(pnlTabEval);
        pnlTabEval.setLayout(pnlTabEvalLayout);
        pnlTabEvalLayout.setHorizontalGroup(
            pnlTabEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitEval)
        );
        pnlTabEvalLayout.setVerticalGroup(
            pnlTabEvalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitEval)
        );

        tabMain.addTab("2. Evaluasi Hasil Panen", pnlTabEval);

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

    private void btnHitungMetrikActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHitungMetrikActionPerformed
        String tglAwal = txtEvalAwal.getText().trim();
        String tglAkhir = txtEvalAkhir.getText().trim();
        if (!InputValidator.isValidDateRange(tglAwal, tglAkhir)) {
            JOptionPane.showMessageDialog(this, "Masukkan rentang tanggal valid dengan tanggal awal sebelum atau sama dengan tanggal akhir.", "Rentang Tanggal Tidak Valid", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double[] metrics = DataStore.getInstance().calculateMetrics(tglAwal, tglAkhir);

        lblTotalBulan.setText(df.format(metrics[0]) + " Kg");
        lblPersenTidakLayak.setText(df.format(metrics[1]) + "%");

        if (txtStatusEval.getText().trim().isEmpty()) {
            if (metrics[1] <= 4.0) {
                txtStatusEval.setText("Selesai");
            } else {
                txtStatusEval.setText("Perlu Peningkatan");
            }
        }
    }//GEN-LAST:event_btnHitungMetrikActionPerformed

    private void btnSimpanEvalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanEvalActionPerformed
        String tglAwal = txtEvalAwal.getText().trim();
        String tglAkhir = txtEvalAkhir.getText().trim();
        if (!InputValidator.isValidDateRange(tglAwal, tglAkhir)) {
            JOptionPane.showMessageDialog(this, "Masukkan rentang tanggal valid dengan tanggal awal sebelum atau sama dengan tanggal akhir.", "Rentang Tanggal Tidak Valid", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double[] metrics = DataStore.getInstance().calculateMetrics(tglAwal, tglAkhir);

        String status = txtStatusEval.getText().trim();
        String catatan = txtCatatanTindakLanjut.getText().trim();

        if (status.isEmpty()) status = "Selesai";
        String newId = DataStore.getInstance().generateEvalId();

        Evaluasi ev = new Evaluasi(newId, tglAwal, tglAkhir, metrics[0], metrics[1], status, catatan);
        DataStore.getInstance().addEvaluasi(ev);

        JOptionPane.showMessageDialog(this, "Evaluasi panen berhasil disimpan dengan ID: " + newId);
        refreshEvalTable();
        resetEvalForm();
    }//GEN-LAST:event_btnSimpanEvalActionPerformed

    private void btnPerbaruiEvalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPerbaruiEvalActionPerformed
        if (selectedEvalId == null) {
            JOptionPane.showMessageDialog(this, "Pilih data evaluasi pada tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tglAwal = txtEvalAwal.getText().trim();
        String tglAkhir = txtEvalAkhir.getText().trim();
        if (!InputValidator.isValidDateRange(tglAwal, tglAkhir)) {
            JOptionPane.showMessageDialog(this, "Masukkan rentang tanggal valid dengan tanggal awal sebelum atau sama dengan tanggal akhir.", "Rentang Tanggal Tidak Valid", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin menyimpan perubahan evaluasi?",
            "Konfirmasi Perubahan",
            JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        double[] metrics = DataStore.getInstance().calculateMetrics(tglAwal, tglAkhir);
        String status = txtStatusEval.getText().trim();
        String catatan = txtCatatanTindakLanjut.getText().trim();

        Evaluasi ev = new Evaluasi(selectedEvalId, tglAwal, tglAkhir, metrics[0], metrics[1], status, catatan);
        DataStore.getInstance().updateEvaluasi(ev);

        JOptionPane.showMessageDialog(this, "Data evaluasi berhasil diperbarui!");
        refreshEvalTable();
        resetEvalForm();
    }//GEN-LAST:event_btnPerbaruiEvalActionPerformed

    private void btnHapusEvalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusEvalActionPerformed
        if (selectedEvalId == null) {
            JOptionPane.showMessageDialog(this, "Pilih data evaluasi pada tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin menghapus data evaluasi yang dipilih?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            DataStore.getInstance().deleteEvaluasi(selectedEvalId);
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
            refreshEvalTable();
            resetEvalForm();
        }
    }//GEN-LAST:event_btnHapusEvalActionPerformed

    private void btnResetEvalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetEvalActionPerformed
        resetEvalForm();
    }//GEN-LAST:event_btnResetEvalActionPerformed

    private void tblEvaluasiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblEvaluasiMouseClicked
        int row = tblEvaluasi.getSelectedRow();
        if (row != -1) {
            selectedEvalId = tblEvaluasi.getValueAt(row, 0).toString();
            for (Evaluasi ev : DataStore.getInstance().getEvaluasiList()) {
                if (ev.getIdEval().equalsIgnoreCase(selectedEvalId)) {
                    txtEvalAwal.setText(ev.getTglAwal());
                    txtEvalAkhir.setText(ev.getTglAkhir());
                    lblTotalBulan.setText(df.format(ev.getTotalPanen()) + " Kg");
                    lblPersenTidakLayak.setText(df.format(ev.getPersenTidakLayak()) + "%");
                    txtStatusEval.setText(ev.getStatus());
                    txtCatatanTindakLanjut.setText(ev.getCatatan());
                    break;
                }
            }
        }
    }//GEN-LAST:event_tblEvaluasiMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHapusEval;
    private javax.swing.JButton btnHitungMetrik;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnPerbaruiEval;
    private javax.swing.JButton btnResetEval;
    private javax.swing.JButton btnResetRekap;
    private javax.swing.JButton btnSimpanEval;
    private javax.swing.JButton btnTampilkanRekap;
    private javax.swing.JComboBox<String> cmbFilterKomoditas;
    private javax.swing.JComboBox<String> cmbModeRekap;
    private javax.swing.JLabel lblCatatan;
    private javax.swing.JLabel lblDaftarEval;
    private javax.swing.JLabel lblDaftarRekap;
    private javax.swing.JLabel lblEvalAkhir;
    private javax.swing.JLabel lblEvalAwal;
    private javax.swing.JLabel lblEvalPeriode;
    private javax.swing.JLabel lblFilterKomoditas;
    private javax.swing.JLabel lblLogoHeader;
    private javax.swing.JLabel lblModeRekap;
    private javax.swing.JLabel lblPersenTidakLayak;
    private javax.swing.JLabel lblStatusEval;
    private javax.swing.JLabel lblTglAkhir;
    private javax.swing.JLabel lblTglAwal;
    private javax.swing.JLabel lblTitlePersen;
    private javax.swing.JLabel lblTitleTotalBulan;
    private javax.swing.JLabel lblTotalBulan;
    private javax.swing.JLabel lblTotalRekap;
    private javax.swing.JLabel lblUserInfo;
    private javax.swing.JPanel pnlFilterRekap;
    private javax.swing.JPanel pnlFormEval;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlMetrikCard;
    private javax.swing.JPanel pnlTabEval;
    private javax.swing.JPanel pnlTabRekap;
    private javax.swing.JPanel pnlTabelEval;
    private javax.swing.JPanel pnlTabelRekap;
    private javax.swing.JScrollPane scrollCatatan;
    private javax.swing.JScrollPane scrollEval;
    private javax.swing.JScrollPane scrollRekap;
    private javax.swing.JSplitPane splitEval;
    private javax.swing.JSplitPane splitRekap;
    private javax.swing.JTabbedPane tabMain;
    private javax.swing.JTable tblEvaluasi;
    private javax.swing.JTable tblRekap;
    private javax.swing.JTextArea txtCatatanTindakLanjut;
    private javax.swing.JTextField txtEvalAkhir;
    private javax.swing.JTextField txtEvalAwal;
    private javax.swing.JTextField txtStatusEval;
    private javax.swing.JTextField txtTglAkhir;
    private javax.swing.JTextField txtTglAwal;
    // End of variables declaration//GEN-END:variables
}
