package ui;

import com.intellij.openapi.project.Project;
import serviceNow.NSAccount;
import serviceNow.NSRolesRestServiceController;
import serviceNow.SNClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

public class AccountsUI extends JDialog
{
    private JPanel contentPane;
    private JButton nextButton;
    private JButton cancelButton;
    private JTable accountsTable;
    private JTextPane twoFactorAuthenticationTextPane;
    private ArrayList<NSAccount> nsAccounts;
    private Project project;
    private String userName;
    private String url;

    public AccountsUI(String nsEmail, String nsPassword, String url, Project project)
    {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(nextButton);

        this.userName = nsEmail;
        this.url = url;
        this.project = project;

        NSRolesRestServiceController nsRolesRestServiceController = new NSRolesRestServiceController();

//        ArrayList<NSAccount> nsAccounts = nsRolesRestServiceController.getNSAccounts(nsEmail, nsPassword, url);

        if (nsAccounts == null)
        {
            JOptionPane.showMessageDialog(null, "Error getting NetSuite Accounts from Roles Rest Service.\nPlease verify that your e-mail and password are correct.", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        else
        {

            twoFactorAuthenticationTextPane.setText("If your NetSuite account has 2FA enabled, then select an account/custom role with file upload permissions.");

            Collections.sort(nsAccounts);

            this.nsAccounts = nsAccounts;

            DefaultTableModel model = new DefaultTableModel()
            {
                @Override
                public boolean isCellEditable(int row, int column)
                {
                    return false;
                }
            };

            model.addColumn("ACCOUNT NAME");
            model.addColumn("ACCOUNT ID");
            model.addColumn("ROLE NAME");
            model.addColumn("ROLE ID");

            for (int i = 0; i < nsAccounts.size(); i++)
            {
                NSAccount account = nsAccounts.get(i);
                Vector<Object> row = new Vector<Object>();
                row.add(account.getAccountName());
                row.add(account.getAccountId());

                model.addRow(row);
            }

            accountsTable.setModel(model);
        }

        nextButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onNext();
            }
        });

        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onNext()
    {

        int selectedAccountIndex = accountsTable.getSelectedRow();

        if (selectedAccountIndex == -1)
        {
            JOptionPane.showMessageDialog(null, "Account selection is required", "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        NSAccount selectedAccount = nsAccounts.get(selectedAccountIndex);

        SNClient SNClient = null;

        try
        {
            SNClient = new SNClient(this.url, this.userName);
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null, "Error creating SNClient", "ERROR", JOptionPane.ERROR_MESSAGE);
        }

        if (SNClient != null)
        {
            try
            {
                SNClient.tryToLogin();
            }
            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(null, "Error logging in: " + ex.getCause(), "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }

            this.setVisible(false);
            FolderSelectionUI folderSelectionUI = new FolderSelectionUI(SNClient, this.project, this.url);
            folderSelectionUI.pack();
            folderSelectionUI.setLocationRelativeTo(null);
            folderSelectionUI.setVisible(true);
        }

        dispose();
    }

    private void onCancel()
    {
        dispose();
    }

    public ArrayList<NSAccount> getNsAccounts()
    {
        return this.nsAccounts;
    }
}
