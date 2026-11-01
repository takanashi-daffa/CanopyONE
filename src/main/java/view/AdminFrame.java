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
import model.Evaluasi;
import model.Panen;
import model.Pegawai;

public class AdminFrame extends javax.swing.JFrame {

    private String selectedBlokKode = null;
    private String selectedPegawaiId = null;
    private String selectedPanenId = null;
    private String selectedEvalId = null;

    private final DecimalFormat df = new DecimalFormat("#,##0.00");

    public AdminFrame() {
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
        cmbKomoditasBlok.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Sawit", "Karet"}));
        cmbRole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Petugas", "Admin", "Direksi"}));
        cmbFilterKomoditas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Semua", "Sawit", "Karet"}));
        cmbModeRekap.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Rentang Tanggal"}));
        tblBlokLahan.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBlokLahanMouseClicked(evt);
            }
        });
        tblPegawai.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPegawaiMouseClicked(evt);
            }
        });
        tblPanen.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPanenMouseClicked(evt);
            }
        });
        tblEvaluasi.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblEvaluasiMouseClicked(evt);
            }
        });
        refreshBlokTable();
        refreshPegawaiTable();
        loadBlokComboBox();
        refreshPanenTable();
        refreshRekapTable();
        refreshEvalTable();
        txtTanggalPanen.setText(LocalDate.now().toString());
        LocalDate today = LocalDate.now();
        String start = today.withDayOfMonth(1).toString();
        txtEvalAwal.setText(start);
        txtEvalAkhir.setText(today.toString());
        txtTglAwal.setText(start);
        txtTglAkhir.setText(today.toString());
    }

    public void refreshBlokTable() {
        DefaultTableModel model = (DefaultTableModel) tblBlokLahan.getModel();
        model.setRowCount(0);
        for (BlokLahan b : DataStore.getInstance().getBlokList()) {
            model.addRow(new Object[]{
                b.getKodeBlok(),
                b.getNamaLahan(),
                df.format(b.getLuasLahan()),
                b.getKomoditas()
            });
        }
        loadBlokComboBox();
    }

    private void resetBlokForm() {
        selectedBlokKode = null;
        txtKodeBlok.setText("");
        txtKodeBlok.setEditable(true);
        txtNamaLahan.setText("");
        txtLuasLahan.setText("");
        cmbKomoditasBlok.setSelectedIndex(0);
        tblBlokLahan.clearSelection();
    }

    public void refreshPegawaiTable() {
        DefaultTableModel model = (DefaultTableModel) tblPegawai.getModel();
        model.setRowCount(0);
        for (Pegawai p : DataStore.getInstance().getPegawaiList()) {
            model.addRow(new Object[]{
                p.getIdPegawai(),
                p.getNamaPegawai(),
                p.getUsername(),
                p.getNoTelp(),
                p.getRole()
            });
        }
    }

    private void resetPegawaiForm() {
        selectedPegawaiId = null;
        txtNamaPegawai.setText("");
        txtUsernamePgw.setText("");
        txtPasswordPgw.setText("");
        txtNoTelp.setText("");
        cmbRole.setSelectedIndex(0);
        tblPegawai.clearSelection();
    }

    private void loadBlokComboBox() {
        String selected = cmbBlokLahan.getSelectedItem() == null ? null : cmbBlokLahan.getSelectedItem().toString();
        String commodity = rbSawit.isSelected() ? "Sawit" : "Karet";
        cmbBlokLahan.removeAllItems();
        for (BlokLahan b : DataStore.getInstance().getBlokList()) {
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
        for (Panen p : DataStore.getInstance().getPanenList()) {
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

        btnGroupKomoditas = new javax.swing.ButtonGroup();
        pnlHeader = new javax.swing.JPanel();
        lblLogoHeader = new javax.swing.JLabel();
        lblUserInfo = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        tabMain = new javax.swing.JTabbedPane();
        pnlTabBlok = new javax.swing.JPanel();
        splitBlok = new javax.swing.JSplitPane();
        pnlFormBlok = new javax.swing.JPanel();
        lblKodeBlok = new javax.swing.JLabel();
        txtKodeBlok = new javax.swing.JTextField();
        lblNamaLahan = new javax.swing.JLabel();
        txtNamaLahan = new javax.swing.JTextField();
        lblLuasLahan = new javax.swing.JLabel();
        txtLuasLahan = new javax.swing.JTextField();
        lblKomoditasBlok = new javax.swing.JLabel();
        cmbKomoditasBlok = new javax.swing.JComboBox<>();
        btnSimpanBlok = new javax.swing.JButton();
        btnPerbaruiBlok = new javax.swing.JButton();
        btnHapusBlok = new javax.swing.JButton();
        btnResetBlok = new javax.swing.JButton();
        pnlTabelBlok = new javax.swing.JPanel();
        lblDaftarBlok = new javax.swing.JLabel();
        scrollBlok = new javax.swing.JScrollPane();
        tblBlokLahan = new javax.swing.JTable();
        pnlTabPegawai = new javax.swing.JPanel();
        splitPegawai = new javax.swing.JSplitPane();
        pnlFormPegawai = new javax.swing.JPanel();
        lblNamaPegawai = new javax.swing.JLabel();
        txtNamaPegawai = new javax.swing.JTextField();
        lblUsernamePgw = new javax.swing.JLabel();
        txtUsernamePgw = new javax.swing.JTextField();
        lblPasswordPgw = new javax.swing.JLabel();
        txtPasswordPgw = new javax.swing.JPasswordField();
        lblNoTelp = new javax.swing.JLabel();
        txtNoTelp = new javax.swing.JTextField();
        lblRole = new javax.swing.JLabel();
        cmbRole = new javax.swing.JComboBox<>();
        btnSimpanPgw = new javax.swing.JButton();
        btnPerbaruiPgw = new javax.swing.JButton();
        btnHapusPgw = new javax.swing.JButton();
        btnResetPgw = new javax.swing.JButton();
        pnlTabelPegawai = new javax.swing.JPanel();
        lblDaftarPegawai = new javax.swing.JLabel();
        scrollPegawai = new javax.swing.JScrollPane();
        tblPegawai = new javax.swing.JTable();
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
        txtTanggalPanen = new DateTextField();
        btnSimpan = new javax.swing.JButton();
        btnPerbarui = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        pnlTabelPanen = new javax.swing.JPanel();
        lblDaftarPanen = new javax.swing.JLabel();
        btnLihatRiwayat = new javax.swing.JButton();
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
        txtTglAwal = new DateTextField();
        lblTglAkhir = new javax.swing.JLabel();
        txtTglAkhir = new DateTextField();
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
        txtEvalAwal = new DateTextField();
        lblEvalAkhir = new javax.swing.JLabel();
        txtEvalAkhir = new DateTextField();
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
        setTitle("CanopyONE - Dashboard Administrator");

        pnlHeader.setBackground(new java.awt.Color(255, 255, 255));
        pnlHeader.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(229, 231, 235)));

        lblLogoHeader.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblLogoHeader.setForeground(new java.awt.Color(21, 94, 239));
        lblLogoHeader.setText("CanopyONE Panel Admin");

        lblUserInfo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblUserInfo.setForeground(new java.awt.Color(75, 85, 99));
        lblUserInfo.setText("Akses Penuh Sistem Manajemen Panen & Kebun");

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

        splitBlok.setDividerLocation(400);
        splitBlok.setDividerSize(6);

        pnlFormBlok.setBackground(new java.awt.Color(255, 255, 255));
        pnlFormBlok.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblKodeBlok.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblKodeBlok.setText("Kode Blok:");

        txtKodeBlok.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        lblNamaLahan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblNamaLahan.setText("Nama Blok / Lahan:");

        txtNamaLahan.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        lblLuasLahan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblLuasLahan.setText("Luas Lahan (Ha):");

        txtLuasLahan.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        lblKomoditasBlok.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblKomoditasBlok.setText("Komoditas Lahan:");

        cmbKomoditasBlok.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        btnSimpanBlok.setBackground(new java.awt.Color(21, 94, 239));
        btnSimpanBlok.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSimpanBlok.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpanBlok.setText("Tambah");
        btnSimpanBlok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanBlokActionPerformed(evt);
            }
        });

        btnPerbaruiBlok.setBackground(new java.awt.Color(245, 158, 11));
        btnPerbaruiBlok.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnPerbaruiBlok.setForeground(new java.awt.Color(255, 255, 255));
        btnPerbaruiBlok.setText("Perbarui");
        btnPerbaruiBlok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPerbaruiBlokActionPerformed(evt);
            }
        });

        btnHapusBlok.setBackground(new java.awt.Color(239, 68, 68));
        btnHapusBlok.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnHapusBlok.setForeground(new java.awt.Color(255, 255, 255));
        btnHapusBlok.setText("Hapus");
        btnHapusBlok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusBlokActionPerformed(evt);
            }
        });

        btnResetBlok.setBackground(new java.awt.Color(107, 114, 128));
        btnResetBlok.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnResetBlok.setForeground(new java.awt.Color(255, 255, 255));
        btnResetBlok.setText("Reset");
        btnResetBlok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetBlokActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlFormBlokLayout = new javax.swing.GroupLayout(pnlFormBlok);
        pnlFormBlok.setLayout(pnlFormBlokLayout);
        pnlFormBlokLayout.setHorizontalGroup(
            pnlFormBlokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormBlokLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFormBlokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtKodeBlok)
                    .addComponent(txtNamaLahan)
                    .addComponent(txtLuasLahan)
                    .addComponent(cmbKomoditasBlok, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlFormBlokLayout.createSequentialGroup()
                        .addGroup(pnlFormBlokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblKodeBlok)
                            .addComponent(lblNamaLahan)
                            .addComponent(lblLuasLahan)
                            .addComponent(lblKomoditasBlok))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlFormBlokLayout.createSequentialGroup()
                        .addComponent(btnSimpanBlok, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPerbaruiBlok, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHapusBlok, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnResetBlok, javax.swing.GroupLayout.PREFERRED_SIZE, 74, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlFormBlokLayout.setVerticalGroup(
            pnlFormBlokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormBlokLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblKodeBlok)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtKodeBlok, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblNamaLahan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNamaLahan, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblLuasLahan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtLuasLahan, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblKomoditasBlok)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbKomoditasBlok, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addGroup(pnlFormBlokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpanBlok, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPerbaruiBlok, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHapusBlok, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnResetBlok, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(155, Short.MAX_VALUE))
        );

        splitBlok.setLeftComponent(pnlFormBlok);

        pnlTabelBlok.setBackground(new java.awt.Color(255, 255, 255));
        pnlTabelBlok.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblDaftarBlok.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblDaftarBlok.setText("Daftar Blok Lahan Perkebunan:");

        tblBlokLahan.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblBlokLahan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Blok", "Nama Lahan", "Luas (Ha)", "Komoditas"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblBlokLahan.setRowHeight(26);
        scrollBlok.setViewportView(tblBlokLahan);

        javax.swing.GroupLayout pnlTabelBlokLayout = new javax.swing.GroupLayout(pnlTabelBlok);
        pnlTabelBlok.setLayout(pnlTabelBlokLayout);
        pnlTabelBlokLayout.setHorizontalGroup(
            pnlTabelBlokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTabelBlokLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTabelBlokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollBlok, javax.swing.GroupLayout.DEFAULT_SIZE, 732, Short.MAX_VALUE)
                    .addGroup(pnlTabelBlokLayout.createSequentialGroup()
                        .addComponent(lblDaftarBlok)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlTabelBlokLayout.setVerticalGroup(
            pnlTabelBlokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTabelBlokLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDaftarBlok)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollBlok, javax.swing.GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE)
                .addContainerGap())
        );

        splitBlok.setRightComponent(pnlTabelBlok);

        javax.swing.GroupLayout pnlTabBlokLayout = new javax.swing.GroupLayout(pnlTabBlok);
        pnlTabBlok.setLayout(pnlTabBlokLayout);
        pnlTabBlokLayout.setHorizontalGroup(
            pnlTabBlokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitBlok)
        );
        pnlTabBlokLayout.setVerticalGroup(
            pnlTabBlokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitBlok)
        );

        tabMain.addTab("Kelola Blok Lahan", pnlTabBlok);

        splitPegawai.setDividerLocation(400);
        splitPegawai.setDividerSize(6);

        pnlFormPegawai.setBackground(new java.awt.Color(255, 255, 255));
        pnlFormPegawai.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblNamaPegawai.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblNamaPegawai.setText("Nama Pegawai:");

        txtNamaPegawai.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        lblUsernamePgw.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblUsernamePgw.setText("Username:");

        txtUsernamePgw.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        lblPasswordPgw.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblPasswordPgw.setText("Password:");

        txtPasswordPgw.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        lblNoTelp.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblNoTelp.setText("No Telepon / WhatsApp:");

        txtNoTelp.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        lblRole.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblRole.setText("Hak Akses / Role:");

        cmbRole.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        btnSimpanPgw.setBackground(new java.awt.Color(21, 94, 239));
        btnSimpanPgw.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSimpanPgw.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpanPgw.setText("Tambah");
        btnSimpanPgw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanPgwActionPerformed(evt);
            }
        });

        btnPerbaruiPgw.setBackground(new java.awt.Color(245, 158, 11));
        btnPerbaruiPgw.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnPerbaruiPgw.setForeground(new java.awt.Color(255, 255, 255));
        btnPerbaruiPgw.setText("Perbarui");
        btnPerbaruiPgw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPerbaruiPgwActionPerformed(evt);
            }
        });

        btnHapusPgw.setBackground(new java.awt.Color(239, 68, 68));
        btnHapusPgw.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnHapusPgw.setForeground(new java.awt.Color(255, 255, 255));
        btnHapusPgw.setText("Hapus");
        btnHapusPgw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusPgwActionPerformed(evt);
            }
        });

        btnResetPgw.setBackground(new java.awt.Color(107, 114, 128));
        btnResetPgw.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnResetPgw.setForeground(new java.awt.Color(255, 255, 255));
        btnResetPgw.setText("Reset");
        btnResetPgw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetPgwActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlFormPegawaiLayout = new javax.swing.GroupLayout(pnlFormPegawai);
        pnlFormPegawai.setLayout(pnlFormPegawaiLayout);
        pnlFormPegawaiLayout.setHorizontalGroup(
            pnlFormPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormPegawaiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFormPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNamaPegawai)
                    .addComponent(txtUsernamePgw)
                    .addComponent(txtPasswordPgw)
                    .addComponent(txtNoTelp)
                    .addComponent(cmbRole, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlFormPegawaiLayout.createSequentialGroup()
                        .addGroup(pnlFormPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNamaPegawai)
                            .addComponent(lblUsernamePgw)
                            .addComponent(lblPasswordPgw)
                            .addComponent(lblNoTelp)
                            .addComponent(lblRole))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlFormPegawaiLayout.createSequentialGroup()
                        .addComponent(btnSimpanPgw, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPerbaruiPgw, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHapusPgw, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnResetPgw, javax.swing.GroupLayout.PREFERRED_SIZE, 74, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlFormPegawaiLayout.setVerticalGroup(
            pnlFormPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormPegawaiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblNamaPegawai)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNamaPegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblUsernamePgw)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtUsernamePgw, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblPasswordPgw)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPasswordPgw, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblNoTelp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNoTelp, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblRole)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbRole, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addGroup(pnlFormPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpanPgw, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPerbaruiPgw, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHapusPgw, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnResetPgw, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(155, Short.MAX_VALUE))
        );

        splitPegawai.setLeftComponent(pnlFormPegawai);

        pnlTabelPegawai.setBackground(new java.awt.Color(255, 255, 255));
        pnlTabelPegawai.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblDaftarPegawai.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblDaftarPegawai.setText("Daftar Akun Pegawai & Pengguna:");

        tblPegawai.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblPegawai.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pegawai", "Nama Lengkap", "Username", "No Telepon", "Role"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPegawai.setRowHeight(26);
        scrollPegawai.setViewportView(tblPegawai);

        javax.swing.GroupLayout pnlTabelPegawaiLayout = new javax.swing.GroupLayout(pnlTabelPegawai);
        pnlTabelPegawai.setLayout(pnlTabelPegawaiLayout);
        pnlTabelPegawaiLayout.setHorizontalGroup(
            pnlTabelPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTabelPegawaiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTabelPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPegawai, javax.swing.GroupLayout.DEFAULT_SIZE, 732, Short.MAX_VALUE)
                    .addGroup(pnlTabelPegawaiLayout.createSequentialGroup()
                        .addComponent(lblDaftarPegawai)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlTabelPegawaiLayout.setVerticalGroup(
            pnlTabelPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTabelPegawaiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDaftarPegawai)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollPegawai, javax.swing.GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE)
                .addContainerGap())
        );

        splitPegawai.setRightComponent(pnlTabelPegawai);

        javax.swing.GroupLayout pnlTabPegawaiLayout = new javax.swing.GroupLayout(pnlTabPegawai);
        pnlTabPegawai.setLayout(pnlTabPegawaiLayout);
        pnlTabPegawaiLayout.setHorizontalGroup(
            pnlTabPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPegawai)
        );
        pnlTabPegawaiLayout.setVerticalGroup(
            pnlTabPegawaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPegawai)
        );

        tabMain.addTab("Kelola Data Pegawai", pnlTabPegawai);

        splitPanen.setDividerLocation(400);
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
                        .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPerbarui, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnReset, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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

        btnLihatRiwayat.setBackground(new java.awt.Color(79, 70, 229));
        btnLihatRiwayat.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLihatRiwayat.setForeground(new java.awt.Color(255, 255, 255));
        btnLihatRiwayat.setText("Lihat Riwayat Perubahan");
        btnLihatRiwayat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLihatRiwayat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLihatRiwayatActionPerformed(evt);
            }
        });

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
                    .addComponent(scrollPanen, javax.swing.GroupLayout.DEFAULT_SIZE, 732, Short.MAX_VALUE)
                    .addGroup(pnlTabelPanenLayout.createSequentialGroup()
                        .addComponent(lblDaftarPanen)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnLihatRiwayat, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlTabelPanenLayout.setVerticalGroup(
            pnlTabelPanenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTabelPanenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTabelPanenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDaftarPanen)
                    .addComponent(btnLihatRiwayat, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollPanen, javax.swing.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)
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

        tabMain.addTab("Kelola Data Panen", pnlTabPanen);

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
        btnResetRekap.setText("Reset Filter");
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
                .addComponent(cmbModeRekap, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblFilterKomoditas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbFilterKomoditas, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTglAwal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTglAwal, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTglAkhir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTglAkhir, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(btnTampilkanRekap, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnResetRekap, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(140, Short.MAX_VALUE))
        );

        splitRekap.setLeftComponent(pnlFilterRekap);

        pnlTabelRekap.setBackground(new java.awt.Color(255, 255, 255));
        pnlTabelRekap.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblDaftarRekap.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblDaftarRekap.setText("Hasil Rekapitulasi Panen:");

        tblRekap.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblRekap.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kategori / Periode", "Komoditas", "Jumlah Data", "Total Hasil Panen", "Total Tidak Layak"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblRekap.setRowHeight(26);
        scrollRekap.setViewportView(tblRekap);

        lblTotalRekap.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
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
                        .addComponent(lblDaftarRekap)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTabelRekapLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblTotalRekap)))
                .addContainerGap())
        );
        pnlTabelRekapLayout.setVerticalGroup(
            pnlTabelRekapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTabelRekapLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDaftarRekap)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollRekap, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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

        tabMain.addTab("Rekapitulasi", pnlTabRekap);

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

        tabMain.addTab("Evaluasi Panen", pnlTabEval);

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

    private void btnSimpanBlokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanBlokActionPerformed
        try {
            String kode = txtKodeBlok.getText().trim();
            String nama = txtNamaLahan.getText().trim();
            double luas = Double.parseDouble(txtLuasLahan.getText().trim().replace(",", ""));
            String kom = cmbKomoditasBlok.getSelectedItem() == null ? "" : cmbKomoditasBlok.getSelectedItem().toString();

            if (kode.isEmpty() || nama.isEmpty() || luas <= 0 || kom.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Kode, nama, luas positif, dan komoditas wajib diisi.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            for (BlokLahan existing : DataStore.getInstance().getBlokList()) {
                if (existing.getKodeBlok().equalsIgnoreCase(kode)) {
                    JOptionPane.showMessageDialog(this, "Kode blok sudah digunakan.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            DataStore.getInstance().addBlok(new BlokLahan(kode, nama, luas, kom));
            JOptionPane.showMessageDialog(this, "Blok lahan berhasil ditambahkan!");
            refreshBlokTable();
            resetBlokForm();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Luas lahan harus berupa angka valid!", "Format Salah", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSimpanBlokActionPerformed

    private void btnPerbaruiBlokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPerbaruiBlokActionPerformed
        if (selectedBlokKode == null) {
            JOptionPane.showMessageDialog(this, "Pilih blok pada tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String nama = txtNamaLahan.getText().trim();
            double luas = Double.parseDouble(txtLuasLahan.getText().trim().replace(",", ""));
            String kom = cmbKomoditasBlok.getSelectedItem() == null ? "" : cmbKomoditasBlok.getSelectedItem().toString();
            if (nama.isEmpty() || luas <= 0 || kom.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama, luas positif, dan komoditas wajib diisi.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            DataStore.getInstance().updateBlok(new BlokLahan(selectedBlokKode, nama, luas, kom));
            JOptionPane.showMessageDialog(this, "Data blok lahan berhasil diperbarui!");
            refreshBlokTable();
            resetBlokForm();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Luas lahan harus berupa angka valid!", "Format Salah", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnPerbaruiBlokActionPerformed

    private void btnHapusBlokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusBlokActionPerformed
        if (selectedBlokKode == null) {
            JOptionPane.showMessageDialog(this, "Pilih blok pada tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        for (Panen panen : DataStore.getInstance().getPanenList()) {
            if (panen.getBlokLahan().startsWith(selectedBlokKode + " - ") || panen.getBlokLahan().equalsIgnoreCase(selectedBlokKode)) {
                JOptionPane.showMessageDialog(this, "Blok tidak dapat dihapus karena masih memiliki data panen.", "Blok Digunakan", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin menghapus data yang dipilih?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            DataStore.getInstance().deleteBlok(selectedBlokKode);
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
            refreshBlokTable();
            resetBlokForm();
        }
    }//GEN-LAST:event_btnHapusBlokActionPerformed

    private void btnResetBlokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetBlokActionPerformed
        resetBlokForm();
    }//GEN-LAST:event_btnResetBlokActionPerformed

    private void tblBlokLahanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBlokLahanMouseClicked
        int row = tblBlokLahan.getSelectedRow();
        if (row != -1) {
            selectedBlokKode = tblBlokLahan.getValueAt(row, 0).toString();
            txtKodeBlok.setText(selectedBlokKode);
            txtKodeBlok.setEditable(false);
            txtNamaLahan.setText(tblBlokLahan.getValueAt(row, 1).toString());
            txtLuasLahan.setText(tblBlokLahan.getValueAt(row, 2).toString().replace(",", ""));
            cmbKomoditasBlok.setSelectedItem(tblBlokLahan.getValueAt(row, 3).toString());
        }
    }//GEN-LAST:event_tblBlokLahanMouseClicked

    private void btnSimpanPgwActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanPgwActionPerformed
        String nama = txtNamaPegawai.getText().trim();
        String user = txtUsernamePgw.getText().trim();
        String pass = new String(txtPasswordPgw.getPassword());
        String telp = txtNoTelp.getText().trim();
        String role = cmbRole.getSelectedItem().toString();

        if (nama.isEmpty() || user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama, username, dan password wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        for (Pegawai existing : DataStore.getInstance().getPegawaiList()) {
            if (existing.getUsername().equalsIgnoreCase(user)) {
                JOptionPane.showMessageDialog(this, "Username sudah digunakan.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        String newId = DataStore.getInstance().generatePegawaiId();
        DataStore.getInstance().addPegawai(new Pegawai(newId, nama, user, pass, telp, role));
        JOptionPane.showMessageDialog(this, "Pegawai berhasil ditambahkan!");
        refreshPegawaiTable();
        resetPegawaiForm();
    }//GEN-LAST:event_btnSimpanPgwActionPerformed

    private void btnPerbaruiPgwActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPerbaruiPgwActionPerformed
        if (selectedPegawaiId == null) {
            JOptionPane.showMessageDialog(this, "Pilih pegawai pada tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nama = txtNamaPegawai.getText().trim();
        String user = txtUsernamePgw.getText().trim();
        String pass = new String(txtPasswordPgw.getPassword());
        String telp = txtNoTelp.getText().trim();
        String role = cmbRole.getSelectedItem() == null ? "Petugas" : cmbRole.getSelectedItem().toString();
        if (nama.isEmpty() || user.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama dan username wajib diisi.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
            return;
        }
        for (Pegawai existing : DataStore.getInstance().getPegawaiList()) {
            if (!existing.getIdPegawai().equalsIgnoreCase(selectedPegawaiId) && existing.getUsername().equalsIgnoreCase(user)) {
                JOptionPane.showMessageDialog(this, "Username sudah digunakan.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        Pegawai selected = DataStore.getInstance().findPegawaiById(selectedPegawaiId);
        if (selected == null) return;
        if (pass.isEmpty()) pass = selected.getPassword();
        DataStore.getInstance().updatePegawai(new Pegawai(selectedPegawaiId, nama, user, pass, telp, role));
        JOptionPane.showMessageDialog(this, "Data pegawai berhasil diperbarui!");
        refreshPegawaiTable();
        resetPegawaiForm();
    }//GEN-LAST:event_btnPerbaruiPgwActionPerformed

    private void btnHapusPgwActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusPgwActionPerformed
        if (selectedPegawaiId == null) {
            JOptionPane.showMessageDialog(this, "Pilih pegawai pada tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin menghapus data pegawai yang dipilih?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            DataStore.getInstance().deletePegawai(selectedPegawaiId);
            JOptionPane.showMessageDialog(this, "Data pegawai berhasil dihapus!");
            refreshPegawaiTable();
            resetPegawaiForm();
        }
    }//GEN-LAST:event_btnHapusPgwActionPerformed

    private void btnResetPgwActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetPgwActionPerformed
        resetPegawaiForm();
    }//GEN-LAST:event_btnResetPgwActionPerformed

    private void tblPegawaiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPegawaiMouseClicked
        int row = tblPegawai.getSelectedRow();
        if (row != -1) {
            selectedPegawaiId = tblPegawai.getValueAt(row, 0).toString();
            txtNamaPegawai.setText(tblPegawai.getValueAt(row, 1).toString());
            txtUsernamePgw.setText(tblPegawai.getValueAt(row, 2).toString());
            txtNoTelp.setText(tblPegawai.getValueAt(row, 3).toString());
            cmbRole.setSelectedItem(tblPegawai.getValueAt(row, 4).toString());
        }
    }//GEN-LAST:event_tblPegawaiMouseClicked

    private void rbSawitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbSawitActionPerformed
        updateKomoditasLabels();
        loadBlokComboBox();
    }//GEN-LAST:event_rbSawitActionPerformed

    private void rbKaretActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbKaretActionPerformed
        updateKomoditasLabels();
        loadBlokComboBox();
    }//GEN-LAST:event_rbKaretActionPerformed

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

    private void btnLihatRiwayatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLihatRiwayatActionPerformed
        RiwayatPerubahanDialog dialog = new RiwayatPerubahanDialog(this, true);
        dialog.setVisible(true);
    }//GEN-LAST:event_btnLihatRiwayatActionPerformed

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
    private javax.swing.ButtonGroup btnGroupKomoditas;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnHapusBlok;
    private javax.swing.JButton btnHapusEval;
    private javax.swing.JButton btnHapusPgw;
    private javax.swing.JButton btnHitungMetrik;
    private javax.swing.JButton btnLihatRiwayat;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnPerbarui;
    private javax.swing.JButton btnPerbaruiBlok;
    private javax.swing.JButton btnPerbaruiEval;
    private javax.swing.JButton btnPerbaruiPgw;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnResetBlok;
    private javax.swing.JButton btnResetEval;
    private javax.swing.JButton btnResetPgw;
    private javax.swing.JButton btnResetRekap;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnSimpanBlok;
    private javax.swing.JButton btnSimpanEval;
    private javax.swing.JButton btnSimpanPgw;
    private javax.swing.JButton btnTampilkanRekap;
    private javax.swing.JComboBox<String> cmbBlokLahan;
    private javax.swing.JComboBox<String> cmbFilterKomoditas;
    private javax.swing.JComboBox<String> cmbKomoditasBlok;
    private javax.swing.JComboBox<String> cmbModeRekap;
    private javax.swing.JComboBox<String> cmbRole;
    private javax.swing.JLabel lblBlok;
    private javax.swing.JLabel lblCatatan;
    private javax.swing.JLabel lblDaftarBlok;
    private javax.swing.JLabel lblDaftarEval;
    private javax.swing.JLabel lblDaftarPanen;
    private javax.swing.JLabel lblDaftarPegawai;
    private javax.swing.JLabel lblDaftarRekap;
    private javax.swing.JLabel lblEvalAkhir;
    private javax.swing.JLabel lblEvalAwal;
    private javax.swing.JLabel lblEvalPeriode;
    private javax.swing.JLabel lblFilterKomoditas;
    private javax.swing.JLabel lblInput1;
    private javax.swing.JLabel lblInput2;
    private javax.swing.JLabel lblKodeBlok;
    private javax.swing.JLabel lblKomoditas;
    private javax.swing.JLabel lblKomoditasBlok;
    private javax.swing.JLabel lblLogoHeader;
    private javax.swing.JLabel lblLuasLahan;
    private javax.swing.JLabel lblModeRekap;
    private javax.swing.JLabel lblNamaLahan;
    private javax.swing.JLabel lblNamaPegawai;
    private javax.swing.JLabel lblNoTelp;
    private javax.swing.JLabel lblPasswordPgw;
    private javax.swing.JLabel lblPersenTidakLayak;
    private javax.swing.JLabel lblRole;
    private javax.swing.JLabel lblStatusEval;
    private javax.swing.JLabel lblTanggal;
    private javax.swing.JLabel lblTglAkhir;
    private javax.swing.JLabel lblTglAwal;
    private javax.swing.JLabel lblTidakLayak;
    private javax.swing.JLabel lblTitlePersen;
    private javax.swing.JLabel lblTitleTotalBulan;
    private javax.swing.JLabel lblTotalBulan;
    private javax.swing.JLabel lblTotalRekap;
    private javax.swing.JLabel lblUserInfo;
    private javax.swing.JLabel lblUsernamePgw;
    private javax.swing.JPanel pnlFilterRekap;
    private javax.swing.JPanel pnlFormBlok;
    private javax.swing.JPanel pnlFormEval;
    private javax.swing.JPanel pnlFormPanen;
    private javax.swing.JPanel pnlFormPegawai;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlMetrikCard;
    private javax.swing.JPanel pnlTabBlok;
    private javax.swing.JPanel pnlTabEval;
    private javax.swing.JPanel pnlTabPanen;
    private javax.swing.JPanel pnlTabPegawai;
    private javax.swing.JPanel pnlTabRekap;
    private javax.swing.JPanel pnlTabelBlok;
    private javax.swing.JPanel pnlTabelEval;
    private javax.swing.JPanel pnlTabelPanen;
    private javax.swing.JPanel pnlTabelPegawai;
    private javax.swing.JPanel pnlTabelRekap;
    private javax.swing.JRadioButton rbKaret;
    private javax.swing.JRadioButton rbSawit;
    private javax.swing.JScrollPane scrollBlok;
    private javax.swing.JScrollPane scrollCatatan;
    private javax.swing.JScrollPane scrollEval;
    private javax.swing.JScrollPane scrollPanen;
    private javax.swing.JScrollPane scrollPegawai;
    private javax.swing.JScrollPane scrollRekap;
    private javax.swing.JSplitPane splitBlok;
    private javax.swing.JSplitPane splitEval;
    private javax.swing.JSplitPane splitPanen;
    private javax.swing.JSplitPane splitPegawai;
    private javax.swing.JSplitPane splitRekap;
    private javax.swing.JTabbedPane tabMain;
    private javax.swing.JTable tblBlokLahan;
    private javax.swing.JTable tblEvaluasi;
    private javax.swing.JTable tblPanen;
    private javax.swing.JTable tblPegawai;
    private javax.swing.JTable tblRekap;
    private javax.swing.JTextArea txtCatatanTindakLanjut;
    private javax.swing.JTextField txtEvalAkhir;
    private javax.swing.JTextField txtEvalAwal;
    private javax.swing.JTextField txtInput1;
    private javax.swing.JTextField txtInput2;
    private javax.swing.JTextField txtKodeBlok;
    private javax.swing.JTextField txtLuasLahan;
    private javax.swing.JTextField txtNamaLahan;
    private javax.swing.JTextField txtNamaPegawai;
    private javax.swing.JTextField txtNoTelp;
    private javax.swing.JPasswordField txtPasswordPgw;
    private javax.swing.JTextField txtStatusEval;
    private javax.swing.JTextField txtTanggalPanen;
    private javax.swing.JTextField txtTglAkhir;
    private javax.swing.JTextField txtTglAwal;
    private javax.swing.JTextField txtTidakLayak;
    private javax.swing.JTextField txtUsernamePgw;
    // End of variables declaration//GEN-END:variables
}
