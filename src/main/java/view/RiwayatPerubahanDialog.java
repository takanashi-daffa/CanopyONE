package view;

import java.util.List;
import javax.swing.table.DefaultTableModel;
import model.DataStore;
import model.Riwayat;

public class RiwayatPerubahanDialog extends javax.swing.JDialog {

    public RiwayatPerubahanDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(parent);
        tblRiwayat.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblRiwayatMouseClicked(evt);
            }
        });
        loadRiwayatData();
    }

    public void loadRiwayatData() {
        DefaultTableModel model = (DefaultTableModel) tblRiwayat.getModel();
        model.setRowCount(0);
        List<Riwayat> logs = DataStore.getInstance().getRiwayatList();
        for (Riwayat r : logs) {
            model.addRow(new Object[]{
                r.getIdRiwayat(),
                r.getWaktuPerubahan(),
                r.getJenisTindakan(),
                r.getUserId(),
                r.getIdPanen(),
                r.getDataSebelum(),
                r.getDataSetelah()
            });
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlHeader = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        btnTutup = new javax.swing.JButton();
        scrollTable = new javax.swing.JScrollPane();
        tblRiwayat = new javax.swing.JTable();
        pnlComparison = new javax.swing.JPanel();
        pnlBefore = new javax.swing.JPanel();
        lblBefore = new javax.swing.JLabel();
        scrollBefore = new javax.swing.JScrollPane();
        txtBefore = new javax.swing.JTextArea();
        pnlAfter = new javax.swing.JPanel();
        lblAfter = new javax.swing.JLabel();
        scrollAfter = new javax.swing.JScrollPane();
        txtAfter = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("CanopyONE - Riwayat Perubahan Data Panen");
        setPreferredSize(new java.awt.Dimension(880, 600));

        pnlHeader.setBackground(new java.awt.Color(255, 255, 255));
        pnlHeader.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(229, 231, 235)));

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(21, 94, 239));
        lblTitle.setText("Riwayat Perubahan Data Panen (Audit Log)");

        btnTutup.setBackground(new java.awt.Color(107, 114, 128));
        btnTutup.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTutup.setForeground(new java.awt.Color(255, 255, 255));
        btnTutup.setText("Tutup");
        btnTutup.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTutup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTutupActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlHeaderLayout = new javax.swing.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnTutup, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTitle)
                    .addComponent(btnTutup, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        tblRiwayat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Riwayat", "Waktu Perubahan", "Jenis Tindakan", "User ID", "ID Panen", "Data Sebelum", "Data Setelah"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblRiwayat.setRowHeight(24);
        scrollTable.setViewportView(tblRiwayat);

        pnlComparison.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Detail Perbandingan (Before vs After)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        pnlComparison.setLayout(new java.awt.GridLayout(1, 2, 12, 0));

        lblBefore.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblBefore.setForeground(new java.awt.Color(220, 38, 38));
        lblBefore.setText("Data Sebelum Perubahan:");

        txtBefore.setEditable(false);
        txtBefore.setColumns(20);
        txtBefore.setLineWrap(true);
        txtBefore.setRows(4);
        txtBefore.setWrapStyleWord(true);
        scrollBefore.setViewportView(txtBefore);

        javax.swing.GroupLayout pnlBeforeLayout = new javax.swing.GroupLayout(pnlBefore);
        pnlBefore.setLayout(pnlBeforeLayout);
        pnlBeforeLayout.setHorizontalGroup(
            pnlBeforeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollBefore, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addGroup(pnlBeforeLayout.createSequentialGroup()
                .addComponent(lblBefore)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlBeforeLayout.setVerticalGroup(
            pnlBeforeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBeforeLayout.createSequentialGroup()
                .addComponent(lblBefore)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollBefore, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
        );

        pnlComparison.add(pnlBefore);

        lblAfter.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblAfter.setForeground(new java.awt.Color(22, 163, 74));
        lblAfter.setText("Data Setelah Perubahan:");

        txtAfter.setEditable(false);
        txtAfter.setColumns(20);
        txtAfter.setLineWrap(true);
        txtAfter.setRows(4);
        txtAfter.setWrapStyleWord(true);
        scrollAfter.setViewportView(txtAfter);

        javax.swing.GroupLayout pnlAfterLayout = new javax.swing.GroupLayout(pnlAfter);
        pnlAfter.setLayout(pnlAfterLayout);
        pnlAfterLayout.setHorizontalGroup(
            pnlAfterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollAfter, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addGroup(pnlAfterLayout.createSequentialGroup()
                .addComponent(lblAfter)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlAfterLayout.setVerticalGroup(
            pnlAfterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAfterLayout.createSequentialGroup()
                .addComponent(lblAfter)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollAfter, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
        );

        pnlComparison.add(pnlAfter);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlComparison, javax.swing.GroupLayout.DEFAULT_SIZE, 848, Short.MAX_VALUE)
                    .addComponent(scrollTable))
                .addGap(16, 16, 16))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollTable, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlComparison, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnTutupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTutupActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnTutupActionPerformed

    private void tblRiwayatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblRiwayatMouseClicked
        int selectedRow = tblRiwayat.getSelectedRow();
        if (selectedRow != -1) {
            Object before = tblRiwayat.getValueAt(selectedRow, 5);
            Object after = tblRiwayat.getValueAt(selectedRow, 6);
            txtBefore.setText(before == null ? "" : before.toString());
            txtAfter.setText(after == null ? "" : after.toString());
        }
    }//GEN-LAST:event_tblRiwayatMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnTutup;
    private javax.swing.JLabel lblAfter;
    private javax.swing.JLabel lblBefore;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlAfter;
    private javax.swing.JPanel pnlBefore;
    private javax.swing.JPanel pnlComparison;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JScrollPane scrollAfter;
    private javax.swing.JScrollPane scrollBefore;
    private javax.swing.JScrollPane scrollTable;
    private javax.swing.JTable tblRiwayat;
    private javax.swing.JTextArea txtAfter;
    private javax.swing.JTextArea txtBefore;
    // End of variables declaration//GEN-END:variables
}
